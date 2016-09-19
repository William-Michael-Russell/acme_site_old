package net.testaholic.acme.web.rest;

import com.codahale.metrics.annotation.Timed;
import net.testaholic.acme.domain.AlphaNumericInputField;
import net.testaholic.acme.repository.AlphaNumericInputFieldRepository;
import net.testaholic.acme.repository.UserRepository;
import net.testaholic.acme.repository.search.AlphaNumericInputFieldSearchRepository;
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
 * REST controller for managing AlphaNumericInputField.
 */
@RestController
@RequestMapping("/api")
public class AlphaNumericInputFieldResource {

    private final Logger log = LoggerFactory.getLogger(AlphaNumericInputFieldResource.class);

    @Inject
    private UserRepository userRepository;

    @Inject
    private AlphaNumericInputFieldRepository alphaNumericInputFieldRepository;

    @Inject
    private AlphaNumericInputFieldSearchRepository alphaNumericInputFieldSearchRepository;

    /**
     * POST  /alpha-numeric-input-fields : Create a new alphaNumericInputField.
     *
     * @param alphaNumericInputField the alphaNumericInputField to create
     * @return the ResponseEntity with status 201 (Created) and with body the new alphaNumericInputField, or with status 400 (Bad Request) if the alphaNumericInputField has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/alpha-numeric-input-fields",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AlphaNumericInputField> createAlphaNumericInputField(@Valid @RequestBody AlphaNumericInputField alphaNumericInputField) throws URISyntaxException {
        log.debug("REST request to save AlphaNumericInputField : {}", alphaNumericInputField);
        if (alphaNumericInputField.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("alphaNumericInputField", "idexists", "A new alphaNumericInputField cannot already have an ID")).body(null);
        }
        alphaNumericInputField.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
        AlphaNumericInputField result = alphaNumericInputFieldRepository.save(alphaNumericInputField);
        alphaNumericInputFieldSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/alpha-numeric-input-fields/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("alphaNumericInputField", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /alpha-numeric-input-fields : Updates an existing alphaNumericInputField.
     *
     * @param alphaNumericInputField the alphaNumericInputField to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated alphaNumericInputField,
     * or with status 400 (Bad Request) if the alphaNumericInputField is not valid,
     * or with status 500 (Internal Server Error) if the alphaNumericInputField couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/alpha-numeric-input-fields",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AlphaNumericInputField> updateAlphaNumericInputField(@Valid @RequestBody AlphaNumericInputField alphaNumericInputField) throws URISyntaxException {
        log.debug("REST request to update AlphaNumericInputField : {}", alphaNumericInputField);
        if (alphaNumericInputField.getId() == null) {
            return createAlphaNumericInputField(alphaNumericInputField);
        }
        AlphaNumericInputField result = alphaNumericInputFieldRepository.save(alphaNumericInputField);
        alphaNumericInputFieldSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("alphaNumericInputField", alphaNumericInputField.getId().toString()))
            .body(result);
    }

    /**
     * GET  /alpha-numeric-input-fields : get all the alphaNumericInputFields.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of alphaNumericInputFields in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/alpha-numeric-input-fields",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<AlphaNumericInputField>> getAllAlphaNumericInputFields(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of AlphaNumericInputFields");
        Page<AlphaNumericInputField> page = alphaNumericInputFieldRepository.findByUserIsCurrentUser(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/alpha-numeric-input-fields");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /alpha-numeric-input-fields/:id : get the "id" alphaNumericInputField.
     *
     * @param id the id of the alphaNumericInputField to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the alphaNumericInputField, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/alpha-numeric-input-fields/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AlphaNumericInputField> getAlphaNumericInputField(@PathVariable Long id) {
        log.debug("REST request to get AlphaNumericInputField : {}", id);
        AlphaNumericInputField alphaNumericInputField = alphaNumericInputFieldRepository.findOne(id);
        return Optional.ofNullable(alphaNumericInputField)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /alpha-numeric-input-fields/:id : delete the "id" alphaNumericInputField.
     *
     * @param id the id of the alphaNumericInputField to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/alpha-numeric-input-fields/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteAlphaNumericInputField(@PathVariable Long id) {
        log.debug("REST request to delete AlphaNumericInputField : {}", id);
        alphaNumericInputFieldRepository.delete(id);
        alphaNumericInputFieldSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("alphaNumericInputField", id.toString())).build();
    }

    /**
     * SEARCH  /_search/alpha-numeric-input-fields?query=:query : search for the alphaNumericInputField corresponding
     * to the query.
     *
     * @param query the query of the alphaNumericInputField search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/alpha-numeric-input-fields",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<AlphaNumericInputField>> searchAlphaNumericInputFields(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of AlphaNumericInputFields for query {}", query);
        Page<AlphaNumericInputField> page = alphaNumericInputFieldSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/alpha-numeric-input-fields");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
