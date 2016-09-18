package net.testaholic.acme.web.rest;

import com.codahale.metrics.annotation.Timed;
import net.testaholic.acme.domain.NumericInputField;
import net.testaholic.acme.repository.NumericInputFieldRepository;
import net.testaholic.acme.repository.UserRepository;
import net.testaholic.acme.repository.search.NumericInputFieldSearchRepository;
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
 * REST controller for managing NumericInputField.
 */
@RestController
@RequestMapping("/api")
public class NumericInputFieldResource {

    private final Logger log = LoggerFactory.getLogger(NumericInputFieldResource.class);

    @Inject
    private UserRepository userRepository;

    @Inject
    private NumericInputFieldRepository numericInputFieldRepository;

    @Inject
    private NumericInputFieldSearchRepository numericInputFieldSearchRepository;

    /**
     * POST  /numeric-input-fields : Create a new numericInputField.
     *
     * @param numericInputField the numericInputField to create
     * @return the ResponseEntity with status 201 (Created) and with body the new numericInputField, or with status 400 (Bad Request) if the numericInputField has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/numeric-input-fields",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<NumericInputField> createNumericInputField(@Valid @RequestBody NumericInputField numericInputField) throws URISyntaxException {
        log.debug("REST request to save NumericInputField : {}", numericInputField);
        if (numericInputField.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("numericInputField", "idexists", "A new numericInputField cannot already have an ID")).body(null);
        }
        numericInputField.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
        NumericInputField result = numericInputFieldRepository.save(numericInputField);

        numericInputFieldSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/numeric-input-fields/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("numericInputField", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /numeric-input-fields : Updates an existing numericInputField.
     *
     * @param numericInputField the numericInputField to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated numericInputField,
     * or with status 400 (Bad Request) if the numericInputField is not valid,
     * or with status 500 (Internal Server Error) if the numericInputField couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/numeric-input-fields",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<NumericInputField> updateNumericInputField(@Valid @RequestBody NumericInputField numericInputField) throws URISyntaxException {
        log.debug("REST request to update NumericInputField : {}", numericInputField);
        if (numericInputField.getId() == null) {
            return createNumericInputField(numericInputField);
        }
        NumericInputField result = numericInputFieldRepository.save(numericInputField);
        numericInputFieldSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("numericInputField", numericInputField.getId().toString()))
            .body(result);
    }

    /**
     * GET  /numeric-input-fields : get all the numericInputFields.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of numericInputFields in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/numeric-input-fields",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<NumericInputField>> getAllNumericInputFields(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of NumericInputFields");
        Page<NumericInputField> page = numericInputFieldRepository.findByUserIsCurrentUser(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/numeric-input-fields");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /numeric-input-fields/:id : get the "id" numericInputField.
     *
     * @param id the id of the numericInputField to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the numericInputField, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/numeric-input-fields/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<NumericInputField> getNumericInputField(@PathVariable Long id) {
        log.debug("REST request to get NumericInputField : {}", id);
        NumericInputField numericInputField = numericInputFieldRepository.findOne(id);
        return Optional.ofNullable(numericInputField)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /numeric-input-fields/:id : delete the "id" numericInputField.
     *
     * @param id the id of the numericInputField to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/numeric-input-fields/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteNumericInputField(@PathVariable Long id) {
        log.debug("REST request to delete NumericInputField : {}", id);
        numericInputFieldRepository.delete(id);
        numericInputFieldSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("numericInputField", id.toString())).build();
    }

    /**
     * SEARCH  /_search/numeric-input-fields?query=:query : search for the numericInputField corresponding
     * to the query.
     *
     * @param query the query of the numericInputField search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/numeric-input-fields",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<NumericInputField>> searchNumericInputFields(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of NumericInputFields for query {}", query);
        Page<NumericInputField> page = numericInputFieldSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/numeric-input-fields");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
