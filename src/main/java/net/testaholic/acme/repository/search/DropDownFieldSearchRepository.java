package net.testaholic.acme.repository.search;

import net.testaholic.acme.domain.DropDownField;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the DropDownField entity.
 */
public interface DropDownFieldSearchRepository extends ElasticsearchRepository<DropDownField, Long> {
}
