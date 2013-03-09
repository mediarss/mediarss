package rss.services.downloader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.transaction.support.TransactionTemplate;
import rss.BaseTest;
import rss.dao.EpisodeDao;
import rss.dao.TorrentDao;
import rss.services.EmailService;
import rss.services.PageDownloader;
import rss.services.SearchResult;
import rss.services.shows.ShowService;
import rss.services.shows.ShowsProvider;
import rss.services.shows.SmartEpisodeSearcher;
import rss.entities.Episode;
import rss.services.EpisodeRequest;
import rss.entities.MediaQuality;
import rss.entities.Show;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TVShowsTorrentEntriesDownloaderTest extends BaseTest {

	@Mock
	private TorrentDao torrentDao;
	@Mock
	private EpisodeDao episodeDao;
	@Mock
	private EmailService emailService;
	@Mock
	private SmartEpisodeSearcher smartEpisodeSearcher;
	@Mock
	private TransactionTemplate transactionTemplate;
	@Mock
	private ExecutorService executor;
	@Mock
	private PageDownloader pageDownloader;
	@Mock
	private ShowService showService;
	@Mock
	private ShowsProvider tvComService;

	@InjectMocks
	private TVShowsTorrentEntriesDownloader downloader = new TVShowsTorrentEntriesDownloader();

	@Before
	@Override
	public void setup() {
		super.setup();
		mockExecutorServiceAsDirectExecutor(executor);
		mockTransactionTemplate(transactionTemplate);
	}

	@Test
	public void testEpisodeFoundInCacheButNotTorrents() {
		Show show = new Show();
		EpisodeRequest episodeRequest = new EpisodeRequest("name", show, MediaQuality.HD720P, 2, 1);
		Set<EpisodeRequest> episodeRequests = Collections.singleton(episodeRequest);
		Episode episode = new Episode(2, 1);

		when(episodeDao.find(any(Collection.class))).thenReturn(Collections.singletonList(episode));
		when(smartEpisodeSearcher.search(episodeRequest)).thenReturn(new SearchResult<Episode>(SearchResult.SearchStatus.NOT_FOUND));
//		when(showService.findShow(any(String.class))).thenReturn(Collections.singletonList(show));
//		when(tvComService.getEpisodesCount(show, 2)).thenReturn(5);

		DownloadResult<Episode, EpisodeRequest> downloadResult = downloader.download(episodeRequests, executor);

		assertEquals(0, downloadResult.getDownloaded().size());
		assertEquals(1, downloadResult.getMissing().size());
	}
}