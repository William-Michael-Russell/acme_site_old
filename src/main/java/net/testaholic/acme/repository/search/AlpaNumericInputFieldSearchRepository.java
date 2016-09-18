package net.testaholic.acme.repository.search;

import net.testaholic.acme.domain.AlpaNumericInputField;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the AlpaNumericInputField entity.
 */
public interface AlpaNumericInputFieldSearchRepository extends ElasticsearchRepository<AlpaNumericInputField, Long> {
}
