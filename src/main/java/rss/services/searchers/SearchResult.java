package rss.services.searchers;

import org.apache.commons.lang3.StringUtils;
import rss.entities.Torrent;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Michael Dikman
 * Date: 26/11/12
 * Time: 00:46
 */
public class SearchResult {

	// awaiting aging is when torrent was found but still the aging period hasn't passed
	public enum SearchStatus {
		NOT_FOUND, FOUND, AWAITING_AGING
	}

	private SearchStatus searchStatus;
	private List<Torrent> torrents;
	private String source;

	public SearchResult(String source) {
		this(SearchStatus.FOUND);
		this.source = source;
	}

	public SearchResult(SearchStatus searchStatus) {
		torrents = new ArrayList<>();
		this.searchStatus = searchStatus;
	}

	public static SearchResult createNotFound() {
		return new SearchResult(SearchStatus.NOT_FOUND);
	}

	// for tests
	public SearchResult(Torrent torrent, String source, SearchStatus searchStatus) {
		this(source);
		this.setSearchStatus(searchStatus);
		this.addTorrent(torrent);
	}

	public SearchStatus getSearchStatus() {
		return searchStatus;
	}

	public void setSearchStatus(SearchStatus searchStatus) {
		this.searchStatus = searchStatus;
	}

	public List<Torrent> getTorrents() {
		return torrents;
	}

	public void addTorrent(Torrent torrent) {
		this.torrents.add(torrent);
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSource() {
		return source;
	}

	public String getImdbId() {
		for (Torrent torrent : torrents) {
			if (!StringUtils.isBlank(torrent.getImdbId())) {
				return torrent.getImdbId();
			}
		}

		return null;
	}

	public String getTorrentTitles() {
		StringBuilder sb = new StringBuilder();
		for (Torrent torrent : torrents) {
			sb.append(torrent.getTitle());
			sb.append(", ");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
}