package net.testaholic.acme.repository.search;

import net.testaholic.acme.domain.PhoneNumberInputField;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the PhoneNumberInputField entity.
 */
public interface PhoneNumberInputFieldSearchRepository extends ElasticsearchRepository<PhoneNumberInputField, Long> {
}
