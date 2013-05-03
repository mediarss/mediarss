package rss.services.searchers.simple;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rss.entities.Media;
import rss.entities.Torrent;
import rss.services.PageDownloader;
import rss.services.log.LogService;
import rss.services.requests.MediaRequest;
import rss.services.searchers.SearchResult;
import rss.services.searchers.SimpleTorrentSearcher;
import rss.services.shows.ShowService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Michael Dikman
 * Date: 04/12/12
 * Time: 19:17
 */
@Service("publichdSearcher")
public class PublichdSearcher<T extends MediaRequest, S extends Media> extends SimpleTorrentSearcher<T, S> {

	public static final String NAME = "publichd.se";
	public static final String PUBLICHD_TORRENT_URL = "http://" + NAME + "/index.php?page=torrent-details&id=";
	public static final Pattern PATTERN = Pattern.compile("<tag:torrents\\[\\].download /><a href=\".*?\">(.*?)<a href=(.*?)>.*AddDate</b></td>.*?>(.*?)</td>.*?seeds: (\\d+)", Pattern.DOTALL);

	@Autowired
	private PageDownloader pageDownloader;

	@Autowired
	protected LogService logService;

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected String getSearchUrl(T mediaRequest) {
		// todo: currently not handling search
		throw new UnsupportedOperationException();
	}

	@Override
	public SearchResult search(T mediaRequest) {
		// todo: currently not handling search
		return SearchResult.createNotFound();
	}

	@Override
	protected String getSearchByIdUrl(T mediaRequest) {
		if (mediaRequest.getHash() != null) {
			return PUBLICHD_TORRENT_URL + mediaRequest.getHash();
		}
		return null;
	}

	@Override
	protected SearchResult parseSearchResults(T mediaRequest, String url, String page) {
		// todo: currently not handling search
		return SearchResult.createNotFound();
	}

	@Override
	protected String getImdbUrl(Torrent torrent) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	protected List<Torrent> parseSearchResultsPage(T mediaRequest, String page) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	protected SearchResult parseTorrentPage(T mediaRequest, String page) {
		Matcher matcher = PATTERN.matcher(page);
		if (!matcher.find()) {
			if (!page.contains("Bad ID!")) { // in that case just id not found - not a parsing problem
				logService.error(getClass(), "Failed parsing page of " + mediaRequest.toString() + ": " + page);
			}
			return SearchResult.createNotFound();
		}

		String title = matcher.group(1).trim(); // sometimes comes with line break at the end - ruins log
		title = StringEscapeUtils.unescapeHtml4(title);
		String link = matcher.group(2);
		String uploadDataString = matcher.group(3);
		int seeders = Integer.parseInt(matcher.group(4));

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date uploadDate = null;
		try {
			uploadDate = formatter.parse(uploadDataString);
		} catch (ParseException e) {
			logService.error(getClass(), "Failed parsing date '" + uploadDataString + "': " + e.getMessage(), e);
		}

		String imdbUrl = parseImdbUrl(page, title);

		Torrent movieTorrent = new Torrent(title, link, uploadDate, seeders);
		SearchResult searchResult = new SearchResult(NAME);
		searchResult.addTorrent(movieTorrent);
		searchResult.getMetaData().setImdbUrl(imdbUrl);
		return searchResult;
	}
}
