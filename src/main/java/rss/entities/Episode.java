package rss.entities;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: Michael Dikman
 * Date: 24/11/12
 * Time: 14:26
 */
@Entity
@Table(name = "episode")
@org.hibernate.annotations.Table(appliesTo = "episode", indexes = {
		@Index(name = "ep_showid_season_episode_idx", columnNames = {"show_id", "season", "episode"}),
		@Index(name = "ep_airdate_showId_idx", columnNames = {"air_date", "show_id"})
})
@NamedQueries({
		@NamedQuery(name = "Episode.findByTorrent",
				query = "select e from Episode as e join e.torrentIds as tid " +
						"where :torrentId = tid"),
		@NamedQuery(name = "Episode.getAirDatesBeforeNow",
				query = "select e.airDate " +
						"from User as u join u.shows as s join s.episodes as e " +
						"where u.id = :userId and e.airDate <= CURRENT_DATE() " +
						"group by e.airDate " +
						"order by e.airDate desc"),
		@NamedQuery(name = "Episode.getAirDatesAfterNow",
				query = "select e.airDate " +
						"from User as u join u.shows as s join s.episodes as e " +
						"where u.id = :userId and e.airDate > CURRENT_DATE() " +
						"group by e.airDate " +
						"order by e.airDate asc"),
		@NamedQuery(name = "Episode.getEpisodesByAirDate",
				query = "select e " +
						"from User as u join u.shows as s join s.episodes as e " +
						"where u.id = :userId and e.airDate in (:airDates)")
})
public class Episode extends Media {

	private static final long serialVersionUID = 4424414890903298183L;

	@Column(name = "season")
	private int season;

	@Column(name = "episode")
	private int episode;

	// eager cuz need it later for comparator stuff of show name
	@ManyToOne(targetEntity = Show.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "show_id")
	@Index(name = "episode_showid_idx")
	private Show show;

    // java.sql.Date doesn't save any minutes and hours
	@Column(name = "air_date")
	@Index(name = "episode_airdate_idx")
	private java.sql.Date airDate;

	@Column(name = "scan_date")
	private Date scanDate;

	@SuppressWarnings("UnusedDeclaration")
	public Episode() {
	}

	public Episode(int season, int episode) {
		this();
		this.season = season;
		this.episode = episode;
	}

	@Override
	public String getName() {
		return show.getName();
	}

	public int getSeason() {
		return season;
	}

	public int getEpisode() {
		return episode;
	}

	public void setSeason(int season) {
		this.season = season;
	}

	public void setEpisode(int episode) {
		this.episode = episode;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder().append(getName()).append(" ").append(getSeasonEpisode());
		if (airDate != null) {
			sb.append(" air date ").append(new SimpleDateFormat("yyyy/MM/dd").format(airDate));
		}
		return sb.toString();
	}

	public String getSeasonEpisode() {
		StringBuilder sb = new StringBuilder().append("s").append(StringUtils.leftPad(String.valueOf(season), 2, '0'));
		if (episode > 0) {
			sb.append("e").append(StringUtils.leftPad(String.valueOf(episode), 2, '0'));
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Episode that = (Episode) o;

		if (episode != that.episode) return false;
		if (season != that.season) return false;
		if (!getName().equals(that.getName())) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = getName().hashCode();
		result = 31 * result + season;
		result = 31 * result + episode;
		return result;
	}

	public void setShow(Show show) {
		this.show = show;
	}

	public Date getAirDate() {
		return airDate;
	}

	public void setAirDate(Date airDate) {
		this.airDate = normalizeDate(airDate);
	}

	private java.sql.Date normalizeDate(Date airDate) {
		if (airDate == null) {
			return null;
		}
		java.util.Calendar c1 = java.util.Calendar.getInstance();
		c1.setTime(airDate);
		java.util.Calendar c2 = java.util.Calendar.getInstance();
		c2.clear();
		c2.set(java.util.Calendar.YEAR, c1.get(java.util.Calendar.YEAR));
		c2.set(java.util.Calendar.MONTH, c1.get(java.util.Calendar.MONTH));
		c2.set(java.util.Calendar.DAY_OF_MONTH, c1.get(java.util.Calendar.DAY_OF_MONTH));
		return new java.sql.Date(c2.getTime().getTime());
	}

	public Date getScanDate() {
		return scanDate;
	}

	public void setScanDate(Date scanDate) {
		this.scanDate = scanDate;
	}

	public Show getShow() {
		return show;
	}

	public boolean isUnAired() {
		return getAirDate() != null && getAirDate().after(new Date());
	}
}
