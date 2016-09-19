package net.testaholic.acme.repository.search;

import net.testaholic.acme.domain.VideoUpload;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the VideoUpload entity.
 */
public interface VideoUploadSearchRepository extends ElasticsearchRepository<VideoUpload, Long> {
}
