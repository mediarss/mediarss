package rss.test.services;

import org.springframework.stereotype.Component;
import rss.test.entities.NewsCreateResult;
import rss.test.util.JsonTranslation;

import java.util.Collections;

/**
 * User: dikmanm
 * Date: 12/02/2015 22:43
 */
@Component
public class NewsService extends BaseService {

    public long createNews(String message) {
        reporter.info("Creating news with message '" + message + "'");
        String response = sendPostRequest("rest/news", Collections.<String, Object>singletonMap("text", message));
        return JsonTranslation.jsonString2Object(response, NewsCreateResult.class).getId();
    }

    public void dismissNews() {
        reporter.info("Dismissing news");
        sendGetRequest("rest/news/dismiss");
    }
}
