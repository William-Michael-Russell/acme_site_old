package net.testaholic.acme.web.rest;

import com.codahale.metrics.annotation.Timed;
import net.testaholic.acme.domain.AudioUpload;
import net.testaholic.acme.repository.AudioUploadRepository;
import net.testaholic.acme.repository.UserRepository;
import net.testaholic.acme.repository.search.AudioUploadSearchRepository;
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
 * REST controller for managing AudioUpload.
 */
@RestController
@RequestMapping("/api")
public class AudioUploadResource {

    private final Logger log = LoggerFactory.getLogger(AudioUploadResource.class);

    @Inject
    private UserRepository userRepository;

    @Inject
    private AudioUploadRepository audioUploadRepository;

    @Inject
    private AudioUploadSearchRepository audioUploadSearchRepository;

    /**
     * POST  /audio-uploads : Create a new audioUpload.
     *
     * @param audioUpload the audioUpload to create
     * @return the ResponseEntity with status 201 (Created) and with body the new audioUpload, or with status 400 (Bad Request) if the audioUpload has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/audio-uploads",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AudioUpload> createAudioUpload(@Valid @RequestBody AudioUpload audioUpload) throws URISyntaxException {
        log.debug("REST request to save AudioUpload : {}", audioUpload);
        if (audioUpload.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("audioUpload", "idexists", "A new audioUpload cannot already have an ID")).body(null);
        }
        audioUpload.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
        AudioUpload result = audioUploadRepository.save(audioUpload);
        audioUploadSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/audio-uploads/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("audioUpload", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /audio-uploads : Updates an existing audioUpload.
     *
     * @param audioUpload the audioUpload to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated audioUpload,
     * or with status 400 (Bad Request) if the audioUpload is not valid,
     * or with status 500 (Internal Server Error) if the audioUpload couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/audio-uploads",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AudioUpload> updateAudioUpload(@Valid @RequestBody AudioUpload audioUpload) throws URISyntaxException {
        log.debug("REST request to update AudioUpload : {}", audioUpload);
        if (audioUpload.getId() == null) {
            return createAudioUpload(audioUpload);
        }
        AudioUpload result = audioUploadRepository.save(audioUpload);
        audioUploadSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("audioUpload", audioUpload.getId().toString()))
            .body(result);
    }

    /**
     * GET  /audio-uploads : get all the audioUploads.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of audioUploads in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/audio-uploads",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<AudioUpload>> getAllAudioUploads(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of AudioUploads");
        Page<AudioUpload> page = audioUploadRepository.findByUserIsCurrentUser(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/audio-uploads");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /audio-uploads/:id : get the "id" audioUpload.
     *
     * @param id the id of the audioUpload to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the audioUpload, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/audio-uploads/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AudioUpload> getAudioUpload(@PathVariable Long id) {
        log.debug("REST request to get AudioUpload : {}", id);
        AudioUpload audioUpload = audioUploadRepository.findOne(id);
        return Optional.ofNullable(audioUpload)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /audio-uploads/:id : delete the "id" audioUpload.
     *
     * @param id the id of the audioUpload to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/audio-uploads/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteAudioUpload(@PathVariable Long id) {
        log.debug("REST request to delete AudioUpload : {}", id);
        audioUploadRepository.delete(id);
        audioUploadSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("audioUpload", id.toString())).build();
    }

    /**
     * SEARCH  /_search/audio-uploads?query=:query : search for the audioUpload corresponding
     * to the query.
     *
     * @param query the query of the audioUpload search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/audio-uploads",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<AudioUpload>> searchAudioUploads(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of AudioUploads for query {}", query);
        Page<AudioUpload> page = audioUploadSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/audio-uploads");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
