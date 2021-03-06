package rss.torrents.searchers.config;

import java.util.Collection;

/**
 * User: dikmanm
 * Date: 07/01/14 18:26
 */
public interface SearcherConfigurationService {

	SearcherConfiguration getSearcherConfiguration(String name);

	Collection<SearcherConfiguration> getSearcherConfigurations();

	void updateSearcherConfiguration(SearcherConfiguration searcherConfiguration);

	void addDomain(String name, String domain);

	void removeDomain(String name, String domain);

    void torrentzSetEnabled(boolean isEnabled);

    boolean torrentzIsEnabled();
}
