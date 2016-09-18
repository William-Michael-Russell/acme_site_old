package net.testaholic.acme.web.rest;

import com.codahale.metrics.annotation.Timed;
import net.testaholic.acme.domain.PhoneNumberInputField;
import net.testaholic.acme.repository.PhoneNumberInputFieldRepository;
import net.testaholic.acme.repository.UserRepository;
import net.testaholic.acme.repository.search.PhoneNumberInputFieldSearchRepository;
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
 * REST controller for managing PhoneNumberInputField.
 */
@RestController
@RequestMapping("/api")
public class PhoneNumberInputFieldResource {

    private final Logger log = LoggerFactory.getLogger(PhoneNumberInputFieldResource.class);

    @Inject
    UserRepository userRepository;

    @Inject
    private PhoneNumberInputFieldRepository phoneNumberInputFieldRepository;

    @Inject
    private PhoneNumberInputFieldSearchRepository phoneNumberInputFieldSearchRepository;

    /**
     * POST  /phone-number-input-fields : Create a new phoneNumberInputField.
     *
     * @param phoneNumberInputField the phoneNumberInputField to create
     * @return the ResponseEntity with status 201 (Created) and with body the new phoneNumberInputField, or with status 400 (Bad Request) if the phoneNumberInputField has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/phone-number-input-fields",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PhoneNumberInputField> createPhoneNumberInputField(@Valid @RequestBody PhoneNumberInputField phoneNumberInputField) throws URISyntaxException {
        log.debug("REST request to save PhoneNumberInputField : {}", phoneNumberInputField);
        if (phoneNumberInputField.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("phoneNumberInputField", "idexists", "A new phoneNumberInputField cannot already have an ID")).body(null);
        }
        phoneNumberInputField.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
        PhoneNumberInputField result = phoneNumberInputFieldRepository.save(phoneNumberInputField);
        phoneNumberInputFieldSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/phone-number-input-fields/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("phoneNumberInputField", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /phone-number-input-fields : Updates an existing phoneNumberInputField.
     *
     * @param phoneNumberInputField the phoneNumberInputField to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated phoneNumberInputField,
     * or with status 400 (Bad Request) if the phoneNumberInputField is not valid,
     * or with status 500 (Internal Server Error) if the phoneNumberInputField couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/phone-number-input-fields",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PhoneNumberInputField> updatePhoneNumberInputField(@Valid @RequestBody PhoneNumberInputField phoneNumberInputField) throws URISyntaxException {
        log.debug("REST request to update PhoneNumberInputField : {}", phoneNumberInputField);
        if (phoneNumberInputField.getId() == null) {
            return createPhoneNumberInputField(phoneNumberInputField);
        }
        PhoneNumberInputField result = phoneNumberInputFieldRepository.save(phoneNumberInputField);
        phoneNumberInputFieldSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("phoneNumberInputField", phoneNumberInputField.getId().toString()))
            .body(result);
    }

    /**
     * GET  /phone-number-input-fields : get all the phoneNumberInputFields.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of phoneNumberInputFields in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/phone-number-input-fields",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<PhoneNumberInputField>> getAllPhoneNumberInputFields(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of PhoneNumberInputFields");
        Page<PhoneNumberInputField> page = phoneNumberInputFieldRepository.findByUserIsCurrentUser(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/phone-number-input-fields");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /phone-number-input-fields/:id : get the "id" phoneNumberInputField.
     *
     * @param id the id of the phoneNumberInputField to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the phoneNumberInputField, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/phone-number-input-fields/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PhoneNumberInputField> getPhoneNumberInputField(@PathVariable Long id) {
        log.debug("REST request to get PhoneNumberInputField : {}", id);
        PhoneNumberInputField phoneNumberInputField = phoneNumberInputFieldRepository.findOne(id);
        return Optional.ofNullable(phoneNumberInputField)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /phone-number-input-fields/:id : delete the "id" phoneNumberInputField.
     *
     * @param id the id of the phoneNumberInputField to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/phone-number-input-fields/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePhoneNumberInputField(@PathVariable Long id) {
        log.debug("REST request to delete PhoneNumberInputField : {}", id);
        phoneNumberInputFieldRepository.delete(id);
        phoneNumberInputFieldSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("phoneNumberInputField", id.toString())).build();
    }

    /**
     * SEARCH  /_search/phone-number-input-fields?query=:query : search for the phoneNumberInputField corresponding
     * to the query.
     *
     * @param query the query of the phoneNumberInputField search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/phone-number-input-fields",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<PhoneNumberInputField>> searchPhoneNumberInputFields(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of PhoneNumberInputFields for query {}", query);
        Page<PhoneNumberInputField> page = phoneNumberInputFieldSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/phone-number-input-fields");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
