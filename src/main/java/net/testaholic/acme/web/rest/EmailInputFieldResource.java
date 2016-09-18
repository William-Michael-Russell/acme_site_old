package net.testaholic.acme.web.rest;

import com.codahale.metrics.annotation.Timed;
import net.testaholic.acme.domain.EmailInputField;
import net.testaholic.acme.repository.EmailInputFieldRepository;
import net.testaholic.acme.repository.UserRepository;
import net.testaholic.acme.repository.search.EmailInputFieldSearchRepository;
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
 * REST controller for managing EmailInputField.
 */
@RestController
@RequestMapping("/api")
public class EmailInputFieldResource {

    private final Logger log = LoggerFactory.getLogger(EmailInputFieldResource.class);

    @Inject
    private UserRepository userRepository;

    @Inject
    private EmailInputFieldRepository emailInputFieldRepository;

    @Inject
    private EmailInputFieldSearchRepository emailInputFieldSearchRepository;

    /**
     * POST  /email-input-fields : Create a new emailInputField.
     *
     * @param emailInputField the emailInputField to create
     * @return the ResponseEntity with status 201 (Created) and with body the new emailInputField, or with status 400 (Bad Request) if the emailInputField has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/email-input-fields",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<EmailInputField> createEmailInputField(@Valid @RequestBody EmailInputField emailInputField) throws URISyntaxException {
        log.debug("REST request to save EmailInputField : {}", emailInputField);
        if (emailInputField.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("emailInputField", "idexists", "A new emailInputField cannot already have an ID")).body(null);
        }
        emailInputField.setLogin(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
        System.out.println(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
        EmailInputField result = emailInputFieldRepository.save(emailInputField);
        emailInputFieldSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/email-input-fields/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("emailInputField", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /email-input-fields : Updates an existing emailInputField.
     *
     * @param emailInputField the emailInputField to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated emailInputField,
     * or with status 400 (Bad Request) if the emailInputField is not valid,
     * or with status 500 (Internal Server Error) if the emailInputField couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/email-input-fields",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<EmailInputField> updateEmailInputField(@Valid @RequestBody EmailInputField emailInputField) throws URISyntaxException {
        log.debug("REST request to update EmailInputField : {}", emailInputField);
        if (emailInputField.getId() == null) {
            return createEmailInputField(emailInputField);
        }
        EmailInputField result = emailInputFieldRepository.save(emailInputField);
        emailInputFieldSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("emailInputField", emailInputField.getId().toString()))
            .body(result);
    }

    /**
     * GET  /email-input-fields : get all the emailInputFields.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of emailInputFields in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/email-input-fields",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<EmailInputField>> getAllEmailInputFields(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of EmailInputFields");
        log.debug("\n\n"+SecurityUtils.getCurrentUserLogin());
        Page<EmailInputField> page = emailInputFieldRepository.findByLoginIsCurrentUser(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/email-input-fields");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /email-input-fields/:id : get the "id" emailInputField.
     *
     * @param id the id of the emailInputField to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the emailInputField, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/email-input-fields/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<EmailInputField> getEmailInputField(@PathVariable Long id) {
        log.debug("REST request to get EmailInputField : {}", id);
        EmailInputField emailInputField = emailInputFieldRepository.findOne(id);
        return Optional.ofNullable(emailInputField)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /email-input-fields/:id : delete the "id" emailInputField.
     *
     * @param id the id of the emailInputField to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/email-input-fields/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteEmailInputField(@PathVariable Long id) {
        log.debug("REST request to delete EmailInputField : {}", id);
        emailInputFieldRepository.delete(id);
        emailInputFieldSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("emailInputField", id.toString())).build();
    }

    /**
     * SEARCH  /_search/email-input-fields?query=:query : search for the emailInputField corresponding
     * to the query.
     *
     * @param query the query of the emailInputField search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/email-input-fields",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<EmailInputField>> searchEmailInputFields(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of EmailInputFields for query {}", query);
        Page<EmailInputField> page = emailInputFieldSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/email-input-fields");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
