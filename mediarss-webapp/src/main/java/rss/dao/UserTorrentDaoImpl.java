package rss.dao;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.springframework.stereotype.Repository;
import rss.entities.*;

import java.util.*;

/**
 * User: Michael Dikman
 * Date: 24/05/12
 * Time: 11:05
 */
@Repository
public class UserTorrentDaoImpl extends BaseDaoJPA<UserTorrent> implements UserTorrentDao {

	@Override
	public List<UserTorrent> findUserMoviesForUserFeed(Date dateUploaded, User user) {
		Map<String, Object> params = new HashMap<>(2);
		params.put("dateUploaded", dateUploaded);
		params.put("userId", user.getId());
		return super.findByNamedQueryAndNamedParams("UserMovieTorrent.findUserMoviesForUserFeed", params);
	}

	@Override
	public List<UserTorrent> findEpisodesAddedSince(User user, Date dateAdded) {
		Map<String, Object> params = new HashMap<>(2);
		params.put("dateAdded", dateAdded);
		params.put("userId", user.getId());
		return super.findByNamedQueryAndNamedParams("UserEpisodeTorrent.findEpisodesAddedSince", params);
	}

	@Override
	public List<UserMovieTorrent> findUserMovieTorrents(User user, Collection<Movie> movies) {
		if (movies.isEmpty()) {
			return Collections.emptyList();
		}

		Map<String, Object> params = new HashMap<>(2);
		params.put("userId", user.getId());
		params.put("movieIds", Collections2.transform(movies, new Function<Movie, Long>() {
			@Override
			public Long apply(rss.entities.Movie movie) {
				return movie.getId();
			}
		}));
		return super.findByNamedQueryAndNamedParams("UserMovieTorrent.findUserMovieTorrents", params);
	}

	@Override
	public List<UserTorrent> findUserEpisodeTorrentByTorrentId(long torrentId) {
		Map<String, Object> params = new HashMap<>(1);
		params.put("torrentId", torrentId);
		return super.findByNamedQueryAndNamedParams("UserEpisodeTorrent.findUserTorrentByTorrentId", params);
	}

	@Override
	public UserMovieTorrent findUserMovieTorrent(User user, long torrentId) {
		Map<String, Object> params = new HashMap<>(2);
		params.put("userId", user.getId());
		params.put("torrentIds", Collections.singleton(torrentId));
		List<UserMovieTorrent> list = super.findByNamedQueryAndNamedParams("UserMovieTorrent.findUserMovieTorrentsByTorrentIds", params);
		return list.isEmpty() ? null : list.get(0);
	}

	@Override
	public UserEpisodeTorrent findUserEpisodeTorrent(User user, long torrentId) {
		Map<String, Object> params = new HashMap<>(2);
		params.put("userId", user.getId());
		params.put("torrentIds", Collections.singleton(torrentId));
		List<UserEpisodeTorrent> list = super.findByNamedQueryAndNamedParams("UserEpisodeTorrent.findUserEpisodeTorrents", params);
		return list.isEmpty() ? null : list.get(0);
	}

	@Override
	public Collection<UserTorrent> findUserEpisodes(long userId, Collection<Episode> episodes) {
		Set<Long> ids = new HashSet<>();
		for (Episode episode : episodes) {
			ids.addAll(episode.getTorrentIds());
		}

		if (ids.isEmpty()) {
			return Collections.emptyList();
		}

		Map<String, Object> params = new HashMap<>(2);
		params.put("userId", userId);
		params.put("torrentIds", ids);
		return super.findByNamedQueryAndNamedParams("UserEpisodeTorrent.findUserEpisodeTorrents", params);
	}
}