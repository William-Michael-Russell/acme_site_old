package net.testaholic.acme.repository.search;

import net.testaholic.acme.domain.RadioCheckboxField;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the RadioCheckboxField entity.
 */
public interface RadioCheckboxFieldSearchRepository extends ElasticsearchRepository<RadioCheckboxField, Long> {
}
