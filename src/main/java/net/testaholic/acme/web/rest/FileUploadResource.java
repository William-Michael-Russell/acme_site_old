package net.testaholic.acme.web.rest;

import com.codahale.metrics.annotation.Timed;
import net.testaholic.acme.domain.FileUpload;
import net.testaholic.acme.repository.FileUploadRepository;
import net.testaholic.acme.repository.search.FileUploadSearchRepository;
import net.testaholic.acme.web.rest.util.HeaderUtil;
import net.testaholic.acme.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing FileUpload.
 */
@RestController
@RequestMapping("/api")
public class FileUploadResource {

    private final Logger log = LoggerFactory.getLogger(FileUploadResource.class);
        
    @Inject
    private FileUploadRepository fileUploadRepository;
    
    @Inject
    private FileUploadSearchRepository fileUploadSearchRepository;
    
    /**
     * POST  /file-uploads : Create a new fileUpload.
     *
     * @param fileUpload the fileUpload to create
     * @return the ResponseEntity with status 201 (Created) and with body the new fileUpload, or with status 400 (Bad Request) if the fileUpload has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/file-uploads",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<FileUpload> createFileUpload(@Valid @RequestBody FileUpload fileUpload) throws URISyntaxException {
        log.debug("REST request to save FileUpload : {}", fileUpload);
        if (fileUpload.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("fileUpload", "idexists", "A new fileUpload cannot already have an ID")).body(null);
        }
        FileUpload result = fileUploadRepository.save(fileUpload);
        fileUploadSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/file-uploads/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("fileUpload", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /file-uploads : Updates an existing fileUpload.
     *
     * @param fileUpload the fileUpload to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated fileUpload,
     * or with status 400 (Bad Request) if the fileUpload is not valid,
     * or with status 500 (Internal Server Error) if the fileUpload couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/file-uploads",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<FileUpload> updateFileUpload(@Valid @RequestBody FileUpload fileUpload) throws URISyntaxException {
        log.debug("REST request to update FileUpload : {}", fileUpload);
        if (fileUpload.getId() == null) {
            return createFileUpload(fileUpload);
        }
        FileUpload result = fileUploadRepository.save(fileUpload);
        fileUploadSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("fileUpload", fileUpload.getId().toString()))
            .body(result);
    }

    /**
     * GET  /file-uploads : get all the fileUploads.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of fileUploads in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/file-uploads",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<FileUpload>> getAllFileUploads(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of FileUploads");
        Page<FileUpload> page = fileUploadRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/file-uploads");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /file-uploads/:id : get the "id" fileUpload.
     *
     * @param id the id of the fileUpload to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the fileUpload, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/file-uploads/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<FileUpload> getFileUpload(@PathVariable Long id) {
        log.debug("REST request to get FileUpload : {}", id);
        FileUpload fileUpload = fileUploadRepository.findOne(id);
        return Optional.ofNullable(fileUpload)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /file-uploads/:id : delete the "id" fileUpload.
     *
     * @param id the id of the fileUpload to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/file-uploads/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteFileUpload(@PathVariable Long id) {
        log.debug("REST request to delete FileUpload : {}", id);
        fileUploadRepository.delete(id);
        fileUploadSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("fileUpload", id.toString())).build();
    }

    /**
     * SEARCH  /_search/file-uploads?query=:query : search for the fileUpload corresponding
     * to the query.
     *
     * @param query the query of the fileUpload search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/file-uploads",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<FileUpload>> searchFileUploads(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of FileUploads for query {}", query);
        Page<FileUpload> page = fileUploadSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/file-uploads");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
