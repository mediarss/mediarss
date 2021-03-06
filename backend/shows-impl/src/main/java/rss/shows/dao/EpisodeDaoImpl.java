package rss.shows.dao;

import org.springframework.stereotype.Repository;
import rss.ems.dao.BaseDaoJPA;
import rss.torrents.Episode;
import rss.torrents.Show;
import rss.torrents.Torrent;
import rss.torrents.requests.shows.DoubleEpisodeRequest;
import rss.torrents.requests.shows.FullSeasonRequest;
import rss.torrents.requests.shows.ShowRequest;
import rss.torrents.requests.shows.SingleEpisodeRequest;
import rss.user.User;
import rss.util.DateUtils;

import java.util.*;

/**
 * User: Michael Dikman
 * Date: 24/05/12
 * Time: 11:05
 */
@Repository
public class EpisodeDaoImpl extends BaseDaoJPA<Episode> implements EpisodeDao {

	@Override
	protected Class<? extends Episode> getPersistentClass() {
		return EpisodeImpl.class;
	}

	@Override
	public List<Episode> find(ShowRequest episodeRequest) {
		return findByRequests(Collections.<ShowRequest>singletonList(episodeRequest));
	}

	@Override
	public List<Episode> findByRequests(Collection<ShowRequest> showRequests) {
		if (showRequests.isEmpty()) {
			return Collections.emptyList();
		}

		StringBuilder query = new StringBuilder();
		List<Object> params = new ArrayList<>();

		String orPart = " or ";
		query.append("select t from Episode as t where ");
		int counter = 0;
		for (ShowRequest episodeRequest : showRequests) {
			if (episodeRequest instanceof SingleEpisodeRequest) {
				SingleEpisodeRequest ser = (SingleEpisodeRequest) episodeRequest;
				counter = generateSingleEpisodePart(query, params, orPart, counter, episodeRequest.getShow().getId(), ser.getSeason(), ser.getEpisode());
			} else if (episodeRequest instanceof DoubleEpisodeRequest) {
				DoubleEpisodeRequest der = (DoubleEpisodeRequest) episodeRequest;
				counter = generateSingleEpisodePart(query, params, orPart, counter, episodeRequest.getShow().getId(), der.getSeason(), der.getEpisode1());
				counter = generateSingleEpisodePart(query, params, orPart, counter, episodeRequest.getShow().getId(), der.getSeason(), der.getEpisode2());
			} else if (episodeRequest instanceof FullSeasonRequest) {
				FullSeasonRequest fsr = (FullSeasonRequest) episodeRequest;
				counter = generateSingleEpisodePart(query, params, orPart, counter, episodeRequest.getShow().getId(), fsr.getSeason(), -1);
			} else {
				throw new IllegalArgumentException("Cannot search for requests of type: " + episodeRequest.getClass());
			}
		}
		query.delete(query.length() - orPart.length(), query.length());

		return find(query.toString(), params.toArray());
	}

	private int generateSingleEpisodePart(StringBuilder query, List<Object> params, String orPart, int counter,
										  long showId, int season, int episode) {
		query.append("(t.show.id = :p").append(counter++).append(" and t.season = :p").append(counter++).append(" and t.episode = :p").append(counter++).append(")");
		query.append(orPart);
		params.add(showId);
		params.add(season);
		params.add(episode);
		return counter;
	}

	@Override
	public Episode find(Torrent torrent) {
		Map<String, Object> params = new HashMap<>(1);
		params.put("torrentId", torrent.getId());
		return uniqueResult(super.<Episode>findByNamedQueryAndNamedParams("Episode.findByTorrent", params));
	}

	@Override
	public Collection<Episode> getEpisodesForSchedule(User user) {
		Map<String, Object> params = new HashMap<>(1);
		params.put("userId", user.getId());
		List<Date> datesBefore = super.findByNamedQueryAndNamedParams("Episode.getAirDatesBeforeNow", params, 7);
		List<Date> datesAfter = super.findByNamedQueryAndNamedParams("Episode.getAirDatesAfterNow", params, 7);
		List<Date> dates = new ArrayList<>();
		dates.addAll(datesBefore);
		dates.addAll(datesAfter);

		if (dates.isEmpty()) {
			return Collections.emptyList();
		}

		Map<String, Object> params2 = new HashMap<>(1);
		params2.put("userId", user.getId());
		params2.put("airDates", dates);
		return super.findByNamedQueryAndNamedParams("Episode.getEpisodesByAirDate", params2);
	}

	@Override
	public boolean exists(Show show, Episode episode) {
		StringBuilder query = new StringBuilder();
		List<Object> params = new ArrayList<>();

		query.append("select count(t) from Episode as t where ");
		generateSingleEpisodePart(query, params, "", 0, show.getId(), episode.getSeason(), episode.getEpisode());
		return uniqueResult(super.<Long>find(query.toString(), params.toArray())) > 0;
	}


	@Override
	public Collection<Episode> getEpisodesToDownload(User user) {
		// add to feed everything aired since last feed was generated and a buffer of 14 days backwards
		Map<String, Object> params = new HashMap<>(2);
		params.put("userId", user.getId());
		params.put("fromDate", DateUtils.getPastDate(user.getLastShowsFeedGenerated(), 14));
		return super.findByNamedQueryAndNamedParams("Episode.getEpisodesToDownload", params);
	}
}
