package rss.entities;

import javax.persistence.*;

/**
 * User: Michael Dikman
 * Date: 03/12/12
 * Time: 08:25
 */
@Entity
@Table(name = "movie")
@NamedQueries({
		@NamedQuery(name = "Movie.findByDateUploaded",
				query = "select m from Movie as m join m.torrentIds as tid " +
						"where tid in (select t.id from Torrent as t where t.dateUploaded > :dateUploaded)"),
		@NamedQuery(name = "Movie.findByTorrent",
				query = "select m from Movie as m join m.torrentIds as tid " +
						"where :torrentId = tid"),
//		@NamedQuery(name = "Movie.findByName",
//				query = "select m from Movie as m " +
//						"where m.name = :name"),
		@NamedQuery(name = "Movie.findByImdbUrl",
				query = "select m from Movie as m " +
						"where m.imdbUrl = :imdbUrl")
})
public class Movie extends Media {

	private static final long serialVersionUID = 8378048151514553873L;

	@Column(name = "name")
	private String name;

	// this is only needed for movies, for tv shows better use tv.com
	@Column(name = "imdb_url", unique = true)
	private String imdbUrl;

	@Column(name = "year")
	private int year;

	private Movie() {
	}

	public Movie(String name, String imdbUrl, int year) {
		this.name = name;
		this.imdbUrl = imdbUrl;
		this.year = year;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Movie movie = (Movie) o;

		//noinspection RedundantIfStatement
		if (!getName().equals(movie.getName())) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	public void setImdbUrl(String imdbUrl) {
		this.imdbUrl = imdbUrl;
	}

	public String getImdbUrl() {
		return imdbUrl;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
}
