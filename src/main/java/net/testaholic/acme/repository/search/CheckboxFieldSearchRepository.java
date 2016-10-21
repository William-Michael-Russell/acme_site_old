package net.testaholic.acme.repository.search;

import net.testaholic.acme.domain.CheckboxField;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the CheckboxField entity.
 */
public interface CheckboxFieldSearchRepository extends ElasticsearchRepository<CheckboxField, Long> {
}
