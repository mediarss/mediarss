package rss.torrents.requests.shows;

import rss.torrents.MediaQuality;
import rss.torrents.Show;
import rss.torrents.requests.MediaRequest;

/**
 * User: dikmanm
 * Date: 15/04/13 10:18
 */
public abstract class ShowRequest extends MediaRequest {

	private MediaQuality quality;
	private Show show;
	private Long userId;

	public ShowRequest(Long userId, String title, Show show, MediaQuality quality) {
		super(title, 1);
		this.quality = quality;
		this.show = show;
		this.userId = userId;
	}

	public void setQuality(MediaQuality quality) {
		this.quality = quality;
	}

	public MediaQuality getQuality() {
		return quality;
	}

	public void setShow(Show show) {
		this.show = show;
	}

	public Show getShow() {
		return show;
	}

	public abstract EpisodeRequest copy();

	public Long getUserId() {
		return userId;
	}
}
