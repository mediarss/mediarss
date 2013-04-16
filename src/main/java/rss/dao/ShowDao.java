package rss.dao;

import rss.entities.Show;
import rss.services.shows.CachedShow;

import java.util.Collection;
import java.util.List;

/**
 * User: Michael Dikman
 * Date: 12/05/12
 * Time: 15:30
 */
public interface ShowDao extends Dao<Show> {

    Show findByName(String name);

	Collection<Show> findNotEnded();

	List<Show> autoCompleteShowNames(String term);

	List<CachedShow> findCachedShows();

	Show findByTvRageId(int tvRageId);
}
