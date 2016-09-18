package net.testaholic.acme.repository.search;

import net.testaholic.acme.domain.NumericInputField;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the NumericInputField entity.
 */
public interface NumericInputFieldSearchRepository extends ElasticsearchRepository<NumericInputField, Long> {
}
