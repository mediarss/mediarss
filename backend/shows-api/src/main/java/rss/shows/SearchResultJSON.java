package rss.shows;

import rss.torrents.UserTorrentJSON;

import java.util.*;

/**
 * User: dikmanm
 * Date: 21/02/13 22:25
 */
public class SearchResultJSON {

	private String id;
	private Collection<UserTorrentJSON> episodes;
	private Date start;
	private Date end;
	private String originalSearchTerm;
	private String actualSearchTerm;
	private String displayLabel;
	private ShowJSON show;
	private Collection<ShowJSON> didYouMean;
	private int episodesCount;

	public SearchResultJSON(String originalSearchTerm, String actualSearchTerm, Collection<UserTorrentJSON> episodes) {
		this.episodes = episodes;
		this.originalSearchTerm = originalSearchTerm;
		this.actualSearchTerm = actualSearchTerm;
		this.didYouMean = new ArrayList<>();
		this.start = new Date();
		this.end = new Date();
		this.id = UUID.randomUUID().toString();
	}

	public SearchResultJSON(String originalSearchTerm, String actualSearchTerm) {
		this(originalSearchTerm, actualSearchTerm, new ArrayList<UserTorrentJSON>());
	}

	public static SearchResultJSON createNoResults(String searchTerm) {
		return new SearchResultJSON(searchTerm, searchTerm, Collections.<UserTorrentJSON>emptyList());
	}

	public static SearchResultJSON createDidYouMean(String searchTerm, Collection<ShowJSON> shows) {
		SearchResultJSON esr = new SearchResultJSON(searchTerm, null, Collections.<UserTorrentJSON>emptyList());
		esr.didYouMean.addAll(shows);
		return esr;
	}

	public static SearchResultJSON createWithResult(String originalSearchTerm, String actualSearchTerm,
													Collection<UserTorrentJSON> results, Collection<ShowJSON> shows) {
		SearchResultJSON esr = new SearchResultJSON(originalSearchTerm, actualSearchTerm, results);
		esr.didYouMean.addAll(shows);
		return esr;
	}

	public static SearchResultJSON createWithResult(String originalSearchTerm, String actualSearchTerm,
													ShowJSON show, String displayLabel, Collection<ShowJSON> shows) {
		SearchResultJSON esr = new SearchResultJSON(originalSearchTerm, actualSearchTerm);
		esr.setDisplayLabel(displayLabel);
		esr.didYouMean.addAll(shows);
		esr.setEnd(null); // in progress
		esr.setShow(show);
		return esr;
	}

	public Collection<UserTorrentJSON> getEpisodes() {
		return episodes;
	}

	public void setEpisodes(Collection<UserTorrentJSON> episodes) {
		this.episodes = episodes;
	}

	public String getOriginalSearchTerm() {
		return originalSearchTerm;
	}

	public String getActualSearchTerm() {
		return actualSearchTerm;
	}

	public Collection<ShowJSON> getDidYouMean() {
		return didYouMean;
	}

	public Date getStart() {
		return start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public String getId() {
		return id;
	}

	public String getDisplayLabel() {
		return displayLabel;
	}

	public void setDisplayLabel(String displayLabel) {
		this.displayLabel = displayLabel;
	}

	public ShowJSON getShow() {
		return show;
	}

	public void setShow(ShowJSON show) {
		this.show = show;
	}

	public int getEpisodesCount() {
		return episodesCount;
	}

	public void setEpisodesCount(int episodesCount) {
		this.episodesCount = episodesCount;
	}
}
