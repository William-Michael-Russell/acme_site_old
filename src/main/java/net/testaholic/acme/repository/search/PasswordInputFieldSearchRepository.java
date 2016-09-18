package net.testaholic.acme.repository.search;

import net.testaholic.acme.domain.PasswordInputField;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the PasswordInputField entity.
 */
public interface PasswordInputFieldSearchRepository extends ElasticsearchRepository<PasswordInputField, Long> {
}
