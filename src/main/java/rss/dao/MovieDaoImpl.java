package rss.dao;

import org.springframework.stereotype.Repository;
import rss.entities.Movie;
import rss.entities.Torrent;
import rss.entities.User;
import rss.entities.UserMovie;

import java.util.*;

/**
 * User: Michael Dikman
 * Date: 24/05/12
 * Time: 11:05
 */
@Repository
public class MovieDaoImpl extends BaseDaoJPA<Movie> implements MovieDao {

	@Override
	public Collection<Movie> findUploadedSince(Date uploadedDate) {
		Map<String, Object> params = new HashMap<>(1);
		params.put("dateUploaded", uploadedDate);
		return super.findByNamedQueryAndNamedParams("Movie.findByDateUploaded", params);
	}

	@Override
	public Movie findByName(String name) {
		Map<String, Object> params = new HashMap<>(1);
		params.put("name", name);
		return uniqueResult(super.<Movie>findByNamedQueryAndNamedParams("Movie.findByName", params));
	}

	@Override
	public Movie find(Torrent torrent) {
		Map<String, Object> params = new HashMap<>(1);
		params.put("torrentId", torrent.getId());
		return uniqueResult(super.<Movie>findByNamedQueryAndNamedParams("Movie.findByTorrent", params));
	}

	@Override
	public void persist(UserMovie userMovie) {
		em.persist(userMovie);
	}

	@Override
	public UserMovie findUserMovie(long movieId, User user) {
		Map<String, Object> params = new HashMap<>(2);
		params.put("movieId", movieId);
		params.put("userId", user.getId());
		return uniqueResult(super.<UserMovie>findByNamedQueryAndNamedParams("UserMovie.findUserMovie", params));
	}

	@Override
	public Movie findByImdbUrl(String imdbUrl) {
		Map<String, Object> params = new HashMap<>(1);
		params.put("imdbUrl", imdbUrl);
		return uniqueResult(super.<Movie>findByNamedQueryAndNamedParams("Movie.findByImdbUrl", params));
	}

	@Override
	public List<UserMovie> findFutureUserMovies(User user) {
		Map<String, Object> params = new HashMap<>(1);
		params.put("userId", user.getId());
		return super.findByNamedQueryAndNamedParams("UserMovie.findFutureUserMovies", params);
	}

	@Override
	public Collection<User> findUsersForFutureMovie(Movie movie) {
		Map<String, Object> params = new HashMap<>(1);
		params.put("movieId", movie.getId());
		return super.findByNamedQueryAndNamedParams("UserMovie.findUsersForFutureMovie", params);
	}
}
