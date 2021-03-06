package rss.movies;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import rss.PageDownloader;
import rss.log.LogService;
import rss.movies.dao.MovieDao;
import rss.movies.dao.MovieImpl;
import rss.movies.imdb.IMDBAutoCompleteItem;
import rss.movies.imdb.IMDBParseResult;
import rss.movies.imdb.IMDBService;
import rss.movies.imdb.IMDBServiceImpl;
import rss.torrents.Movie;

import java.util.*;

/**
 * User: dikmanm
 * Date: 19/02/14 07:41
 */
@Service
public class TopMoviesDownloaderImpl implements TopMoviesDownloader {

    public static final String URL = "http://www.moviefone.com/dvd?sort=most-popular&page=";

    @Autowired
    private PageDownloader pageDownloader;

    @Autowired
    private LogService logService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private IMDBService imdbService;

    @Autowired
    private MovieDao movieDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Set<Movie> getTopMovies(int count) {
        HashSet<Movie> result = new HashSet<>();

        int pageNum = 1;
        while (result.size() < count) {
            String page = pageDownloader.downloadPage(URL + pageNum++);
            Document doc = Jsoup.parse(page);
            for (Element element : doc.select("a.movie-title")) {
                String name = element.text();
                String imdbId = null;
                Collection<IMDBAutoCompleteItem> searchResults = imdbService.search(name);
                if (searchResults.size() > 1) {
                    // sort by year and take the latest
                    ArrayList<IMDBAutoCompleteItem> searchResultsList = new ArrayList<>(searchResults);
                    Collections.sort(searchResultsList, new Comparator<IMDBAutoCompleteItem>() {
                        @Override
                        public int compare(IMDBAutoCompleteItem o1, IMDBAutoCompleteItem o2) {
                            // compare in reverse - biggest first
                            return Integer.compare(o2.getYear(), o1.getYear());
                        }
                    });
                    imdbId = IMDBServiceImpl.IMDB_URL + searchResultsList.get(0);
                } else if (searchResults.size() == 1) {
                    imdbId = IMDBServiceImpl.IMDB_URL + searchResults.iterator().next().getId();
                }

                Movie movie = movieDao.findByImdbUrl(imdbId);
                if (movie == null) {
                    IMDBParseResult imdbParseResult = imdbService.downloadMovieFromIMDB(imdbId);
                    if (imdbParseResult.isFound()) {
                        movie = new MovieImpl(imdbParseResult.getName(), imdbId, imdbParseResult.getYear(), imdbParseResult.getReleaseDate());
                        movieService.addMovie(movie, imdbParseResult);
                    }
                }
                if (movie != null) {
                    result.add(movie);
                }

                if (result.size() == count) {
                    break;
                }
            }
        }

        return result;
    }
}
