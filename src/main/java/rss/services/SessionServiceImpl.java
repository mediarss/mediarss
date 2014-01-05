package rss.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import rss.UserNotLoggedInException;
import rss.controllers.vo.ShowsScheduleVO;
import rss.dao.UserDao;
import rss.entities.User;
import rss.services.shows.UsersSearchesCache;

import java.util.Date;

/**
 * User: Michael Dikman
 * Date: 01/12/12
 * Time: 13:50
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.INTERFACES)
public class SessionServiceImpl implements SessionService {

	@Autowired
	private UserDao userDao;

	// not holding the actual user, cuz then need to make him be in sync with the database all the time
	private Long loggedInUserId;
	private Long impersonatedUserId;
	private Date prevLoginDate;
	private UsersSearchesCache usersSearchesCache;
	private ShowsScheduleVO schedule;

	public void setLoggedInUser(User user) {
		loggedInUserId = user.getId();
		impersonatedUserId = null;
		prevLoginDate = user.getLastLogin();
		usersSearchesCache = new UsersSearchesCache();
		if (prevLoginDate == null) {
			prevLoginDate = new Date();
		}
	}

	@Override
	public Long getLoggedInUserId() {
		if (loggedInUserId == null) {
			throw new UserNotLoggedInException();
		}
		if (impersonatedUserId != null) {
			return impersonatedUserId;
		}
		return loggedInUserId;
	}

	@Override
	public boolean isUserLogged() {
		return loggedInUserId != null;
	}

	public Long getImpersonatedUserId() {
		return impersonatedUserId;
	}

	@Override
	public ShowsScheduleVO getSchedule() {
		return schedule;
	}

	@Override
	public void setSchedule(ShowsScheduleVO schedule) {
		this.schedule = schedule;
	}

	@Override
	public void clearLoggedInUser() {
		loggedInUserId = null;
		impersonatedUserId = null;
		prevLoginDate = null;
		schedule = null;
		usersSearchesCache = new UsersSearchesCache();
	}

	@Override
	public Date getPrevLoginDate() {
		return prevLoginDate;
	}

	public UsersSearchesCache getUsersSearchesCache() {
		return usersSearchesCache;
	}

	@Override
	public void impersonate(Long userId) {
		impersonatedUserId = userId;
	}
}
