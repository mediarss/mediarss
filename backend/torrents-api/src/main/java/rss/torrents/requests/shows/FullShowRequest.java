package rss.torrents.requests.shows;

import rss.torrents.MediaQuality;
import rss.torrents.Show;

/**
 * User: Michael Dikman
 * Date: 22/12/12
 * Time: 15:42
 */
public class FullShowRequest extends ShowRequest {

	private static final long serialVersionUID = -6622950945609852366L;

	public FullShowRequest(Long userId, String title, Show show, MediaQuality quality) {
		super(userId, title, show, quality);
	}

	@Override
	public EpisodeRequest copy() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FullShowRequest that = (FullShowRequest) o;

		if (!getTitle().equalsIgnoreCase(that.getTitle()))
			return false; // important ignore case! - when come from search for example
		if (getQuality() != that.getQuality()) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = getQuality().hashCode();
		result = 31 * result + getTitle().toLowerCase().hashCode(); // to match ignore case equals
		return result;
	}
}
