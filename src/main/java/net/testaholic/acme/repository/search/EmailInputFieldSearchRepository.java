package net.testaholic.acme.repository.search;

import net.testaholic.acme.domain.EmailInputField;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the EmailInputField entity.
 */
public interface EmailInputFieldSearchRepository extends ElasticsearchRepository<EmailInputField, Long> {
}
