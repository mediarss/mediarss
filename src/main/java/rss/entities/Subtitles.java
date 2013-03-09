package rss.entities;

import rss.SubtitleLanguage;

import javax.persistence.*;

/**
 * User: dikmanm
 * Date: 25/01/13 00:08
 */
@Entity
@Table(name = "subtitles")
@NamedQueries({
		@NamedQuery(name = "Subtitles.find",
				query = "select s from Subtitles as s " +
						"where s.torrent.id = :torrentId and s.language = :language")
})
public class Subtitles extends BaseEntity {
	private static final long serialVersionUID = 5929747050786576285L;

	@Column(name = "language")
	private SubtitleLanguage language;

	@ManyToOne(targetEntity = Torrent.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "torrent_id")
	private Torrent torrent;

	@Column(name = "release_name")
	private String releaseName;

	@Column(name = "data")
	@Lob
	private byte[] data;

	@Column(name = "external_id")
	private String externalId;

	@Column(name = "file_name")
	private String fileName;

	public SubtitleLanguage getLanguage() {
		return language;
	}

	public void setLanguage(SubtitleLanguage language) {
		this.language = language;
	}

	public Torrent getTorrent() {
		return torrent;
	}

	public void setTorrent(Torrent torrent) {
		this.torrent = torrent;
	}

	public String getReleaseName() {
		return releaseName;
	}

	public void setReleaseName(String releaseName) {
		this.releaseName = releaseName;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}
}
