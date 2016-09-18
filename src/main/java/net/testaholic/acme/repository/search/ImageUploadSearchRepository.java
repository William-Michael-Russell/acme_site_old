package net.testaholic.acme.repository.search;

import net.testaholic.acme.domain.ImageUpload;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ImageUpload entity.
 */
public interface ImageUploadSearchRepository extends ElasticsearchRepository<ImageUpload, Long> {
}
