package net.testaholic.acme.web.rest;

import com.codahale.metrics.annotation.Timed;
import net.testaholic.acme.domain.ImageUpload;
import net.testaholic.acme.repository.ImageUploadRepository;
import net.testaholic.acme.repository.UserRepository;
import net.testaholic.acme.repository.search.ImageUploadSearchRepository;
import net.testaholic.acme.security.SecurityUtils;
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
 * REST controller for managing ImageUpload.
 */
@RestController
@RequestMapping("/api")
public class ImageUploadResource {

    private final Logger log = LoggerFactory.getLogger(ImageUploadResource.class);

    @Inject
    private UserRepository userRepository;

    @Inject
    private ImageUploadRepository imageUploadRepository;

    @Inject
    private ImageUploadSearchRepository imageUploadSearchRepository;

    /**
     * POST  /image-uploads : Create a new imageUpload.
     *
     * @param imageUpload the imageUpload to create
     * @return the ResponseEntity with status 201 (Created) and with body the new imageUpload, or with status 400 (Bad Request) if the imageUpload has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/image-uploads",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ImageUpload> createImageUpload(@Valid @RequestBody ImageUpload imageUpload) throws URISyntaxException {
        log.debug("REST request to save ImageUpload : {}", imageUpload);
        if (imageUpload.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("imageUpload", "idexists", "A new imageUpload cannot already have an ID")).body(null);
        }
        imageUpload.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
        ImageUpload result = imageUploadRepository.save(imageUpload);
        imageUploadSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/image-uploads/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("imageUpload", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /image-uploads : Updates an existing imageUpload.
     *
     * @param imageUpload the imageUpload to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated imageUpload,
     * or with status 400 (Bad Request) if the imageUpload is not valid,
     * or with status 500 (Internal Server Error) if the imageUpload couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/image-uploads",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ImageUpload> updateImageUpload(@Valid @RequestBody ImageUpload imageUpload) throws URISyntaxException {
        log.debug("REST request to update ImageUpload : {}", imageUpload);
        if (imageUpload.getId() == null) {
            return createImageUpload(imageUpload);
        }
        ImageUpload result = imageUploadRepository.save(imageUpload);
        imageUploadSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("imageUpload", imageUpload.getId().toString()))
            .body(result);
    }

    /**
     * GET  /image-uploads : get all the imageUploads.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of imageUploads in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/image-uploads",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ImageUpload>> getAllImageUploads(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ImageUploads");
        Page<ImageUpload> page = imageUploadRepository.findByUserIsCurrentUser(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/image-uploads");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /image-uploads/:id : get the "id" imageUpload.
     *
     * @param id the id of the imageUpload to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the imageUpload, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/image-uploads/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ImageUpload> getImageUpload(@PathVariable Long id) {
        log.debug("REST request to get ImageUpload : {}", id);
        ImageUpload imageUpload = imageUploadRepository.findOne(id);
        return Optional.ofNullable(imageUpload)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /image-uploads/:id : delete the "id" imageUpload.
     *
     * @param id the id of the imageUpload to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/image-uploads/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteImageUpload(@PathVariable Long id) {
        log.debug("REST request to delete ImageUpload : {}", id);
        imageUploadRepository.delete(id);
        imageUploadSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("imageUpload", id.toString())).build();
    }

    /**
     * SEARCH  /_search/image-uploads?query=:query : search for the imageUpload corresponding
     * to the query.
     *
     * @param query the query of the imageUpload search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/image-uploads",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ImageUpload>> searchImageUploads(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of ImageUploads for query {}", query);
        Page<ImageUpload> page = imageUploadSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/image-uploads");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
