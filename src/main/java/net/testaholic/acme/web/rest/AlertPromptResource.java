package net.testaholic.acme.web.rest;

import com.codahale.metrics.annotation.Timed;
import net.testaholic.acme.domain.AlertPrompt;
import net.testaholic.acme.repository.AlertPromptRepository;
import net.testaholic.acme.repository.UserRepository;
import net.testaholic.acme.repository.search.AlertPromptSearchRepository;
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
 * REST controller for managing AlertPrompt.
 */
@RestController
@RequestMapping("/api")
public class AlertPromptResource {

    private final Logger log = LoggerFactory.getLogger(AlertPromptResource.class);

    @Inject
    private AlertPromptRepository alertPromptRepository;

    @Inject
    private AlertPromptSearchRepository alertPromptSearchRepository;

    @Inject
    private UserRepository userRepository;

    /**
     * POST  /alert-prompts : Create a new alertPrompt.
     *
     * @param alertPrompt the alertPrompt to create
     * @return the ResponseEntity with status 201 (Created) and with body the new alertPrompt, or with status 400 (Bad Request) if the alertPrompt has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/alert-prompts",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AlertPrompt> createAlertPrompt(@Valid @RequestBody AlertPrompt alertPrompt) throws URISyntaxException {
        log.debug("REST request to save AlertPrompt : {}", alertPrompt);
        if (alertPrompt.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("alertPrompt", "idexists", "A new alertPrompt cannot already have an ID")).body(null);
        }
        alertPrompt.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
        AlertPrompt result = alertPromptRepository.save(alertPrompt);
        alertPromptSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/alert-prompts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("alertPrompt", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /alert-prompts : Updates an existing alertPrompt.
     *
     * @param alertPrompt the alertPrompt to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated alertPrompt,
     * or with status 400 (Bad Request) if the alertPrompt is not valid,
     * or with status 500 (Internal Server Error) if the alertPrompt couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/alert-prompts",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AlertPrompt> updateAlertPrompt(@Valid @RequestBody AlertPrompt alertPrompt) throws URISyntaxException {
        log.debug("REST request to update AlertPrompt : {}", alertPrompt);
        if (alertPrompt.getId() == null) {
            return createAlertPrompt(alertPrompt);
        }
        AlertPrompt result = alertPromptRepository.save(alertPrompt);
        alertPromptSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("alertPrompt", alertPrompt.getId().toString()))
            .body(result);
    }

    /**
     * GET  /alert-prompts : get all the alertPrompts.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of alertPrompts in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/alert-prompts",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<AlertPrompt>> getAllAlertPrompts(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of AlertPrompts");
        Page<AlertPrompt> page = alertPromptRepository.findByUserIsCurrentUser(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/alert-prompts");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /alert-prompts/:id : get the "id" alertPrompt.
     *
     * @param id the id of the alertPrompt to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the alertPrompt, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/alert-prompts/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AlertPrompt> getAlertPrompt(@PathVariable Long id) {
        log.debug("REST request to get AlertPrompt : {}", id);
        AlertPrompt alertPrompt = alertPromptRepository.findOne(id);
        return Optional.ofNullable(alertPrompt)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /alert-prompts/:id : delete the "id" alertPrompt.
     *
     * @param id the id of the alertPrompt to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/alert-prompts/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteAlertPrompt(@PathVariable Long id) {
        log.debug("REST request to delete AlertPrompt : {}", id);
        alertPromptRepository.delete(id);
        alertPromptSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("alertPrompt", id.toString())).build();
    }

    /**
     * SEARCH  /_search/alert-prompts?query=:query : search for the alertPrompt corresponding
     * to the query.
     *
     * @param query the query of the alertPrompt search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/alert-prompts",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<AlertPrompt>> searchAlertPrompts(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of AlertPrompts for query {}", query);
        Page<AlertPrompt> page = alertPromptSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/alert-prompts");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
