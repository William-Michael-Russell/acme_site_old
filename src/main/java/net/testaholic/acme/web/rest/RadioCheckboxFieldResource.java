package net.testaholic.acme.web.rest;

import com.codahale.metrics.annotation.Timed;
import net.testaholic.acme.domain.RadioCheckboxField;
import net.testaholic.acme.repository.RadioCheckboxFieldRepository;
import net.testaholic.acme.repository.UserRepository;
import net.testaholic.acme.repository.search.RadioCheckboxFieldSearchRepository;
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
 * REST controller for managing RadioCheckboxField.
 */
@RestController
@RequestMapping("/api")
public class RadioCheckboxFieldResource {

    private final Logger log = LoggerFactory.getLogger(RadioCheckboxFieldResource.class);

    @Inject
    private UserRepository userRepository;

    @Inject
    private RadioCheckboxFieldRepository radioCheckboxFieldRepository;

    @Inject
    private RadioCheckboxFieldSearchRepository radioCheckboxFieldSearchRepository;

    /**
     * POST  /radio-checkbox-fields : Create a new radioCheckboxField.
     *
     * @param radioCheckboxField the radioCheckboxField to create
     * @return the ResponseEntity with status 201 (Created) and with body the new radioCheckboxField, or with status 400 (Bad Request) if the radioCheckboxField has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/radio-checkbox-fields",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<RadioCheckboxField> createRadioCheckboxField(@Valid @RequestBody RadioCheckboxField radioCheckboxField) throws URISyntaxException {
        log.debug("REST request to save RadioCheckboxField : {}", radioCheckboxField);
        if (radioCheckboxField.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("radioCheckboxField", "idexists", "A new radioCheckboxField cannot already have an ID")).body(null);
        }
        radioCheckboxField.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
        RadioCheckboxField result = radioCheckboxFieldRepository.save(radioCheckboxField);
        radioCheckboxFieldSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/radio-checkbox-fields/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("radioCheckboxField", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /radio-checkbox-fields : Updates an existing radioCheckboxField.
     *
     * @param radioCheckboxField the radioCheckboxField to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated radioCheckboxField,
     * or with status 400 (Bad Request) if the radioCheckboxField is not valid,
     * or with status 500 (Internal Server Error) if the radioCheckboxField couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/radio-checkbox-fields",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<RadioCheckboxField> updateRadioCheckboxField(@Valid @RequestBody RadioCheckboxField radioCheckboxField) throws URISyntaxException {
        log.debug("REST request to update RadioCheckboxField : {}", radioCheckboxField);
        if (radioCheckboxField.getId() == null) {
            return createRadioCheckboxField(radioCheckboxField);
        }
        RadioCheckboxField result = radioCheckboxFieldRepository.save(radioCheckboxField);
        radioCheckboxFieldSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("radioCheckboxField", radioCheckboxField.getId().toString()))
            .body(result);
    }

    /**
     * GET  /radio-checkbox-fields : get all the radioCheckboxFields.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of radioCheckboxFields in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/radio-checkbox-fields",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<RadioCheckboxField>> getAllRadioCheckboxFields(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of RadioCheckboxFields");
        Page<RadioCheckboxField> page = radioCheckboxFieldRepository.findByUserIsCurrentUser(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/radio-checkbox-fields");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /radio-checkbox-fields/:id : get the "id" radioCheckboxField.
     *
     * @param id the id of the radioCheckboxField to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the radioCheckboxField, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/radio-checkbox-fields/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<RadioCheckboxField> getRadioCheckboxField(@PathVariable Long id) {
        log.debug("REST request to get RadioCheckboxField : {}", id);
        RadioCheckboxField radioCheckboxField = radioCheckboxFieldRepository.findOne(id);
        return Optional.ofNullable(radioCheckboxField)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /radio-checkbox-fields/:id : delete the "id" radioCheckboxField.
     *
     * @param id the id of the radioCheckboxField to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/radio-checkbox-fields/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteRadioCheckboxField(@PathVariable Long id) {
        log.debug("REST request to delete RadioCheckboxField : {}", id);
        radioCheckboxFieldRepository.delete(id);
        radioCheckboxFieldSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("radioCheckboxField", id.toString())).build();
    }

    /**
     * SEARCH  /_search/radio-checkbox-fields?query=:query : search for the radioCheckboxField corresponding
     * to the query.
     *
     * @param query the query of the radioCheckboxField search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/radio-checkbox-fields",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<RadioCheckboxField>> searchRadioCheckboxFields(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of RadioCheckboxFields for query {}", query);
        Page<RadioCheckboxField> page = radioCheckboxFieldSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/radio-checkbox-fields");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
