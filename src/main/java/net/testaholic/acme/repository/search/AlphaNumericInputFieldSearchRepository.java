package net.testaholic.acme.repository.search;

import net.testaholic.acme.domain.AlphaNumericInputField;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the AlphaNumericInputField entity.
 */
public interface AlphaNumericInputFieldSearchRepository extends ElasticsearchRepository<AlphaNumericInputField, Long> {
}
