package rss.torrents.requests.shows;

import org.apache.commons.lang3.StringUtils;
import rss.torrents.MediaQuality;
import rss.torrents.Show;

/**
 * User: Michael Dikman
 * Date: 22/12/12
 * Time: 15:42
 */
public class SingleEpisodeRequest extends EpisodeRequest {

	private int episode;

	public SingleEpisodeRequest(Long userId, String title, Show show, MediaQuality quality, int season, int episode) {
		super(userId, title, show, quality, season);
		this.episode = episode;
	}

	public String getSeasonEpisode() {
		return "s" + StringUtils.leftPad(String.valueOf(getSeason()), 2, '0') +
			   "e" + StringUtils.leftPad(String.valueOf(episode), 2, '0');
	}

	@Override
	public EpisodeRequest copy() {
		return new SingleEpisodeRequest(getUserId(), getTitle(), getShow(), getQuality(), getSeason(), getEpisode());
	}

	public int getEpisode() {
		return episode;
	}

	public void setEpisode(int episode) {
		this.episode = episode;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SingleEpisodeRequest that = (SingleEpisodeRequest) o;

		if (episode != that.episode) return false;
		if (!getTitle().equalsIgnoreCase(that.getTitle()))
			return false; // important ignore case! - when come from search for example
		if (getSeason() != that.getSeason()) return false;
		if (getQuality() != that.getQuality()) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = getQuality().hashCode();
		result = 31 * result + getTitle().toLowerCase().hashCode(); // to match ignore case equals
		result = 31 * result + getSeason();
		result = 31 * result + episode;
		return result;
	}
}
