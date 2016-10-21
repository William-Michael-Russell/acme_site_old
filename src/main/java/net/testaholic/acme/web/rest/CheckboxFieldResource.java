package net.testaholic.acme.web.rest;

import com.codahale.metrics.annotation.Timed;
import net.testaholic.acme.domain.CheckboxField;
import net.testaholic.acme.repository.CheckboxFieldRepository;
import net.testaholic.acme.repository.UserRepository;
import net.testaholic.acme.repository.search.CheckboxFieldSearchRepository;
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
 * REST controller for managing CheckboxField.
 */
@RestController
@RequestMapping("/api")
public class CheckboxFieldResource {

    private final Logger log = LoggerFactory.getLogger(CheckboxFieldResource.class);

    @Inject
    private CheckboxFieldRepository checkboxFieldRepository;

    @Inject
    private CheckboxFieldSearchRepository checkboxFieldSearchRepository;

    @Inject
    private UserRepository userRepository;

    /**
     * POST  /checkbox-fields : Create a new checkboxField.
     *
     * @param checkboxField the checkboxField to create
     * @return the ResponseEntity with status 201 (Created) and with body the new checkboxField, or with status 400 (Bad Request) if the checkboxField has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/checkbox-fields",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CheckboxField> createCheckboxField(@Valid @RequestBody CheckboxField checkboxField) throws URISyntaxException {
        log.debug("REST request to save CheckboxField : {}", checkboxField);
        if (checkboxField.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("checkboxField", "idexists", "A new checkboxField cannot already have an ID")).body(null);
        }

        checkboxField.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
        CheckboxField result = checkboxFieldRepository.save(checkboxField);
        checkboxFieldSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/checkbox-fields/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("checkboxField", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /checkbox-fields : Updates an existing checkboxField.
     *
     * @param checkboxField the checkboxField to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated checkboxField,
     * or with status 400 (Bad Request) if the checkboxField is not valid,
     * or with status 500 (Internal Server Error) if the checkboxField couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/checkbox-fields",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CheckboxField> updateCheckboxField(@Valid @RequestBody CheckboxField checkboxField) throws URISyntaxException {
        log.debug("REST request to update CheckboxField : {}", checkboxField);
        if (checkboxField.getId() == null) {
            return createCheckboxField(checkboxField);
        }
        CheckboxField result = checkboxFieldRepository.save(checkboxField);
        checkboxFieldSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("checkboxField", checkboxField.getId().toString()))
            .body(result);
    }

    /**
     * GET  /checkbox-fields : get all the checkboxFields.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of checkboxFields in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/checkbox-fields",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CheckboxField>> getAllCheckboxFields(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of CheckboxFields");
        Page<CheckboxField> page = checkboxFieldRepository.findByUserIsCurrentUser(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/checkbox-fields");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /checkbox-fields/:id : get the "id" checkboxField.
     *
     * @param id the id of the checkboxField to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the checkboxField, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/checkbox-fields/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CheckboxField> getCheckboxField(@PathVariable Long id) {
        log.debug("REST request to get CheckboxField : {}", id);
        CheckboxField checkboxField = checkboxFieldRepository.findOne(id);
        return Optional.ofNullable(checkboxField)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /checkbox-fields/:id : delete the "id" checkboxField.
     *
     * @param id the id of the checkboxField to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/checkbox-fields/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteCheckboxField(@PathVariable Long id) {
        log.debug("REST request to delete CheckboxField : {}", id);
        checkboxFieldRepository.delete(id);
        checkboxFieldSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("checkboxField", id.toString())).build();
    }

    /**
     * SEARCH  /_search/checkbox-fields?query=:query : search for the checkboxField corresponding
     * to the query.
     *
     * @param query the query of the checkboxField search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/checkbox-fields",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CheckboxField>> searchCheckboxFields(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of CheckboxFields for query {}", query);
        Page<CheckboxField> page = checkboxFieldSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/checkbox-fields");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
