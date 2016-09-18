package net.testaholic.acme.repository.search;

import net.testaholic.acme.domain.FileUpload;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the FileUpload entity.
 */
public interface FileUploadSearchRepository extends ElasticsearchRepository<FileUpload, Long> {
}
