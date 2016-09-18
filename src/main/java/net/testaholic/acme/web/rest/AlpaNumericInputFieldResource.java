package net.testaholic.acme.web.rest;

import com.codahale.metrics.annotation.Timed;
import net.testaholic.acme.domain.AlpaNumericInputField;
import net.testaholic.acme.repository.AlpaNumericInputFieldRepository;
import net.testaholic.acme.repository.search.AlpaNumericInputFieldSearchRepository;
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
 * REST controller for managing AlpaNumericInputField.
 */
@RestController
@RequestMapping("/api")
public class AlpaNumericInputFieldResource {

    private final Logger log = LoggerFactory.getLogger(AlpaNumericInputFieldResource.class);
        
    @Inject
    private AlpaNumericInputFieldRepository alpaNumericInputFieldRepository;
    
    @Inject
    private AlpaNumericInputFieldSearchRepository alpaNumericInputFieldSearchRepository;
    
    /**
     * POST  /alpa-numeric-input-fields : Create a new alpaNumericInputField.
     *
     * @param alpaNumericInputField the alpaNumericInputField to create
     * @return the ResponseEntity with status 201 (Created) and with body the new alpaNumericInputField, or with status 400 (Bad Request) if the alpaNumericInputField has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/alpa-numeric-input-fields",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AlpaNumericInputField> createAlpaNumericInputField(@Valid @RequestBody AlpaNumericInputField alpaNumericInputField) throws URISyntaxException {
        log.debug("REST request to save AlpaNumericInputField : {}", alpaNumericInputField);
        if (alpaNumericInputField.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("alpaNumericInputField", "idexists", "A new alpaNumericInputField cannot already have an ID")).body(null);
        }
        AlpaNumericInputField result = alpaNumericInputFieldRepository.save(alpaNumericInputField);
        alpaNumericInputFieldSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/alpa-numeric-input-fields/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("alpaNumericInputField", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /alpa-numeric-input-fields : Updates an existing alpaNumericInputField.
     *
     * @param alpaNumericInputField the alpaNumericInputField to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated alpaNumericInputField,
     * or with status 400 (Bad Request) if the alpaNumericInputField is not valid,
     * or with status 500 (Internal Server Error) if the alpaNumericInputField couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/alpa-numeric-input-fields",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AlpaNumericInputField> updateAlpaNumericInputField(@Valid @RequestBody AlpaNumericInputField alpaNumericInputField) throws URISyntaxException {
        log.debug("REST request to update AlpaNumericInputField : {}", alpaNumericInputField);
        if (alpaNumericInputField.getId() == null) {
            return createAlpaNumericInputField(alpaNumericInputField);
        }
        AlpaNumericInputField result = alpaNumericInputFieldRepository.save(alpaNumericInputField);
        alpaNumericInputFieldSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("alpaNumericInputField", alpaNumericInputField.getId().toString()))
            .body(result);
    }

    /**
     * GET  /alpa-numeric-input-fields : get all the alpaNumericInputFields.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of alpaNumericInputFields in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/alpa-numeric-input-fields",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<AlpaNumericInputField>> getAllAlpaNumericInputFields(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of AlpaNumericInputFields");
        Page<AlpaNumericInputField> page = alpaNumericInputFieldRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/alpa-numeric-input-fields");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /alpa-numeric-input-fields/:id : get the "id" alpaNumericInputField.
     *
     * @param id the id of the alpaNumericInputField to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the alpaNumericInputField, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/alpa-numeric-input-fields/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AlpaNumericInputField> getAlpaNumericInputField(@PathVariable Long id) {
        log.debug("REST request to get AlpaNumericInputField : {}", id);
        AlpaNumericInputField alpaNumericInputField = alpaNumericInputFieldRepository.findOne(id);
        return Optional.ofNullable(alpaNumericInputField)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /alpa-numeric-input-fields/:id : delete the "id" alpaNumericInputField.
     *
     * @param id the id of the alpaNumericInputField to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/alpa-numeric-input-fields/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteAlpaNumericInputField(@PathVariable Long id) {
        log.debug("REST request to delete AlpaNumericInputField : {}", id);
        alpaNumericInputFieldRepository.delete(id);
        alpaNumericInputFieldSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("alpaNumericInputField", id.toString())).build();
    }

    /**
     * SEARCH  /_search/alpa-numeric-input-fields?query=:query : search for the alpaNumericInputField corresponding
     * to the query.
     *
     * @param query the query of the alpaNumericInputField search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/alpa-numeric-input-fields",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<AlpaNumericInputField>> searchAlpaNumericInputFields(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of AlpaNumericInputFields for query {}", query);
        Page<AlpaNumericInputField> page = alpaNumericInputFieldSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/alpa-numeric-input-fields");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
