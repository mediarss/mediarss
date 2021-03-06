package rss.torrents.searchers.composite;

import org.springframework.stereotype.Service;
import rss.torrents.Torrent;
import rss.torrents.requests.movies.MovieRequest;
import rss.torrents.searchers.SearchResult;

/**
 * User: Michael Dikman
 * Date: 04/12/12
 * Time: 19:15
 */
@Service("moviesCompositeSearcher")
public class MoviesCompositeSearcher extends DefaultCompositeSearcher<MovieRequest> {

	@Override
	protected SearchResult.SearcherFailedReason onTorrentFound(SearchResult searchResult) {
		// case of no IMDB url
		if (getImdbId(searchResult) == null) {
			return SearchResult.SearcherFailedReason.NO_IMDB_ID;
		}
		return null;
	}

	private String getImdbId(SearchResult searchResult) {
		for (Torrent torrent : searchResult.<Torrent>getDownloadables()) {
			if (torrent.getImdbId() != null) {
				return torrent.getImdbId();
			}
		}
		return null;
	}
}
