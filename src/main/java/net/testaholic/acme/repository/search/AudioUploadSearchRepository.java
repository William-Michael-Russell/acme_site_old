package net.testaholic.acme.repository.search;

import net.testaholic.acme.domain.AudioUpload;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the AudioUpload entity.
 */
public interface AudioUploadSearchRepository extends ElasticsearchRepository<AudioUpload, Long> {
}
