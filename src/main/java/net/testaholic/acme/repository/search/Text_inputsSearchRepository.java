package net.testaholic.acme.repository.search;

import net.testaholic.acme.domain.Text_inputs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Text_inputs entity.
 */
public interface Text_inputsSearchRepository extends ElasticsearchRepository<Text_inputs, Long> {
}
