package net.testaholic.acme.web.rest;

import com.codahale.metrics.annotation.Timed;
import net.testaholic.acme.domain.DropDownField;
import net.testaholic.acme.repository.DropDownFieldRepository;
import net.testaholic.acme.repository.UserRepository;
import net.testaholic.acme.repository.search.DropDownFieldSearchRepository;
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
 * REST controller for managing DropDownField.
 */
@RestController
@RequestMapping("/api")
public class DropDownFieldResource {

    private final Logger log = LoggerFactory.getLogger(DropDownFieldResource.class);

    @Inject
    private DropDownFieldRepository dropDownFieldRepository;

    @Inject
    private DropDownFieldSearchRepository dropDownFieldSearchRepository;

    @Inject
    private UserRepository userRepository;

    /**
     * POST  /drop-down-fields : Create a new dropDownField.
     *
     * @param dropDownField the dropDownField to create
     * @return the ResponseEntity with status 201 (Created) and with body the new dropDownField, or with status 400 (Bad Request) if the dropDownField has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/drop-down-fields",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DropDownField> createDropDownField(@Valid @RequestBody DropDownField dropDownField) throws URISyntaxException {
        log.debug("REST request to save DropDownField : {}", dropDownField);
        if (dropDownField.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("dropDownField", "idexists", "A new dropDownField cannot already have an ID")).body(null);
        }
        dropDownField.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
        DropDownField result = dropDownFieldRepository.save(dropDownField);
        dropDownFieldSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/drop-down-fields/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("dropDownField", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /drop-down-fields : Updates an existing dropDownField.
     *
     * @param dropDownField the dropDownField to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated dropDownField,
     * or with status 400 (Bad Request) if the dropDownField is not valid,
     * or with status 500 (Internal Server Error) if the dropDownField couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/drop-down-fields",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DropDownField> updateDropDownField(@Valid @RequestBody DropDownField dropDownField) throws URISyntaxException {
        log.debug("REST request to update DropDownField : {}", dropDownField);
        if (dropDownField.getId() == null) {
            return createDropDownField(dropDownField);
        }
        DropDownField result = dropDownFieldRepository.save(dropDownField);
        dropDownFieldSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("dropDownField", dropDownField.getId().toString()))
            .body(result);
    }

    /**
     * GET  /drop-down-fields : get all the dropDownFields.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of dropDownFields in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/drop-down-fields",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<DropDownField>> getAllDropDownFields(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of DropDownFields");
        Page<DropDownField> page = dropDownFieldRepository.findByUserIsCurrentUser(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/drop-down-fields");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /drop-down-fields/:id : get the "id" dropDownField.
     *
     * @param id the id of the dropDownField to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the dropDownField, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/drop-down-fields/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DropDownField> getDropDownField(@PathVariable Long id) {
        log.debug("REST request to get DropDownField : {}", id);
        DropDownField dropDownField = dropDownFieldRepository.findOne(id);
        return Optional.ofNullable(dropDownField)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /drop-down-fields/:id : delete the "id" dropDownField.
     *
     * @param id the id of the dropDownField to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/drop-down-fields/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteDropDownField(@PathVariable Long id) {
        log.debug("REST request to delete DropDownField : {}", id);
        dropDownFieldRepository.delete(id);
        dropDownFieldSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("dropDownField", id.toString())).build();
    }

    /**
     * SEARCH  /_search/drop-down-fields?query=:query : search for the dropDownField corresponding
     * to the query.
     *
     * @param query the query of the dropDownField search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/drop-down-fields",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<DropDownField>> searchDropDownFields(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of DropDownFields for query {}", query);
        Page<DropDownField> page = dropDownFieldSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/drop-down-fields");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
