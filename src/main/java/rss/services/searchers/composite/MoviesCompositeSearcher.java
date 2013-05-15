package rss.services.searchers.composite;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import rss.entities.Torrent;
import rss.services.requests.MovieRequest;
import rss.services.searchers.SearchResult;

/**
 * User: Michael Dikman
 * Date: 04/12/12
 * Time: 19:15
 */
@Service("moviesCompositeSearcher")
public class MoviesCompositeSearcher extends DefaultCompositeSearcher<MovieRequest> {

	@Override
	protected String onTorrentFound(SearchResult searchResult) {
		// case of no IMDB url
		if (getImdbId(searchResult) == null) {
			return "no IMDB id";
		}
		return null;
	}

	private String getImdbId(SearchResult searchResult) {
		for (Torrent torrent : searchResult.<Torrent>getDownloadables()) {
			if (!StringUtils.isBlank(torrent.getImdbId())) {
				return torrent.getImdbId();
			}
		}
		return null;
	}
}
