package net.testaholic.acme.web.rest;

import com.codahale.metrics.annotation.Timed;
import net.testaholic.acme.domain.VideoUpload;
import net.testaholic.acme.repository.UserRepository;
import net.testaholic.acme.repository.VideoUploadRepository;
import net.testaholic.acme.repository.search.VideoUploadSearchRepository;
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
 * REST controller for managing VideoUpload.
 */
@RestController
@RequestMapping("/api")
public class VideoUploadResource {

    private final Logger log = LoggerFactory.getLogger(VideoUploadResource.class);

    @Inject
    private UserRepository userRepository;

    @Inject
    private VideoUploadRepository videoUploadRepository;

    @Inject
    private VideoUploadSearchRepository videoUploadSearchRepository;

    /**
     * POST  /video-uploads : Create a new videoUpload.
     *
     * @param videoUpload the videoUpload to create
     * @return the ResponseEntity with status 201 (Created) and with body the new videoUpload, or with status 400 (Bad Request) if the videoUpload has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/video-uploads",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<VideoUpload> createVideoUpload(@Valid @RequestBody VideoUpload videoUpload) throws URISyntaxException {
        log.debug("REST request to save VideoUpload : {}", videoUpload);
        if (videoUpload.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("videoUpload", "idexists", "A new videoUpload cannot already have an ID")).body(null);
        }
        videoUpload.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
        VideoUpload result = videoUploadRepository.save(videoUpload);
        videoUploadSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/video-uploads/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("videoUpload", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /video-uploads : Updates an existing videoUpload.
     *
     * @param videoUpload the videoUpload to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated videoUpload,
     * or with status 400 (Bad Request) if the videoUpload is not valid,
     * or with status 500 (Internal Server Error) if the videoUpload couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/video-uploads",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<VideoUpload> updateVideoUpload(@Valid @RequestBody VideoUpload videoUpload) throws URISyntaxException {
        log.debug("REST request to update VideoUpload : {}", videoUpload);
        if (videoUpload.getId() == null) {
            return createVideoUpload(videoUpload);
        }
        VideoUpload result = videoUploadRepository.save(videoUpload);
        videoUploadSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("videoUpload", videoUpload.getId().toString()))
            .body(result);
    }

    /**
     * GET  /video-uploads : get all the videoUploads.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of videoUploads in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/video-uploads",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<VideoUpload>> getAllVideoUploads(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of VideoUploads");
        Page<VideoUpload> page = videoUploadRepository.findByUserIsCurrentUser(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/video-uploads");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /video-uploads/:id : get the "id" videoUpload.
     *
     * @param id the id of the videoUpload to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the videoUpload, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/video-uploads/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<VideoUpload> getVideoUpload(@PathVariable Long id) {
        log.debug("REST request to get VideoUpload : {}", id);
        VideoUpload videoUpload = videoUploadRepository.findOne(id);
        return Optional.ofNullable(videoUpload)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /video-uploads/:id : delete the "id" videoUpload.
     *
     * @param id the id of the videoUpload to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/video-uploads/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteVideoUpload(@PathVariable Long id) {
        log.debug("REST request to delete VideoUpload : {}", id);
        videoUploadRepository.delete(id);
        videoUploadSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("videoUpload", id.toString())).build();
    }

    /**
     * SEARCH  /_search/video-uploads?query=:query : search for the videoUpload corresponding
     * to the query.
     *
     * @param query the query of the videoUpload search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/video-uploads",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<VideoUpload>> searchVideoUploads(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of VideoUploads for query {}", query);
        Page<VideoUpload> page = videoUploadSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/video-uploads");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
