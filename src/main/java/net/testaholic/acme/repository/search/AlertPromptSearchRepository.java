package net.testaholic.acme.repository.search;

import net.testaholic.acme.domain.AlertPrompt;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the AlertPrompt entity.
 */
public interface AlertPromptSearchRepository extends ElasticsearchRepository<AlertPrompt, Long> {
}
