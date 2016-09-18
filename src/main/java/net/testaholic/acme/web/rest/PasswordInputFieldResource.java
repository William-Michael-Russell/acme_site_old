package net.testaholic.acme.web.rest;

import com.codahale.metrics.annotation.Timed;
import net.testaholic.acme.domain.PasswordInputField;
import net.testaholic.acme.repository.PasswordInputFieldRepository;
import net.testaholic.acme.repository.UserRepository;
import net.testaholic.acme.repository.search.PasswordInputFieldSearchRepository;
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
 * REST controller for managing PasswordInputField.
 */
@RestController
@RequestMapping("/api")
public class PasswordInputFieldResource {

    private final Logger log = LoggerFactory.getLogger(PasswordInputFieldResource.class);

    @Inject
    UserRepository userRepository;

    @Inject
    private PasswordInputFieldRepository passwordInputFieldRepository;

    @Inject
    private PasswordInputFieldSearchRepository passwordInputFieldSearchRepository;

    /**
     * POST  /password-input-fields : Create a new passwordInputField.
     *
     * @param passwordInputField the passwordInputField to create
     * @return the ResponseEntity with status 201 (Created) and with body the new passwordInputField, or with status 400 (Bad Request) if the passwordInputField has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/password-input-fields",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PasswordInputField> createPasswordInputField(@Valid @RequestBody PasswordInputField passwordInputField) throws URISyntaxException {
        log.debug("REST request to save PasswordInputField : {}", passwordInputField);
        if (passwordInputField.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("passwordInputField", "idexists", "A new passwordInputField cannot already have an ID")).body(null);
        }
        passwordInputField.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
        PasswordInputField result = passwordInputFieldRepository.save(passwordInputField);
        passwordInputFieldSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/password-input-fields/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("passwordInputField", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /password-input-fields : Updates an existing passwordInputField.
     *
     * @param passwordInputField the passwordInputField to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated passwordInputField,
     * or with status 400 (Bad Request) if the passwordInputField is not valid,
     * or with status 500 (Internal Server Error) if the passwordInputField couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/password-input-fields",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PasswordInputField> updatePasswordInputField(@Valid @RequestBody PasswordInputField passwordInputField) throws URISyntaxException {
        log.debug("REST request to update PasswordInputField : {}", passwordInputField);
        if (passwordInputField.getId() == null) {
            return createPasswordInputField(passwordInputField);
        }
        PasswordInputField result = passwordInputFieldRepository.save(passwordInputField);
        passwordInputFieldSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("passwordInputField", passwordInputField.getId().toString()))
            .body(result);
    }

    /**
     * GET  /password-input-fields : get all the passwordInputFields.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of passwordInputFields in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/password-input-fields",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<PasswordInputField>> getAllPasswordInputFields(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of PasswordInputFields");
        Page<PasswordInputField> page = passwordInputFieldRepository.findByUserIsCurrentUser(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/password-input-fields");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /password-input-fields/:id : get the "id" passwordInputField.
     *
     * @param id the id of the passwordInputField to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the passwordInputField, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/password-input-fields/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PasswordInputField> getPasswordInputField(@PathVariable Long id) {
        log.debug("REST request to get PasswordInputField : {}", id);
        PasswordInputField passwordInputField = passwordInputFieldRepository.findOne(id);
        return Optional.ofNullable(passwordInputField)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /password-input-fields/:id : delete the "id" passwordInputField.
     *
     * @param id the id of the passwordInputField to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/password-input-fields/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePasswordInputField(@PathVariable Long id) {
        log.debug("REST request to delete PasswordInputField : {}", id);
        passwordInputFieldRepository.delete(id);
        passwordInputFieldSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("passwordInputField", id.toString())).build();
    }

    /**
     * SEARCH  /_search/password-input-fields?query=:query : search for the passwordInputField corresponding
     * to the query.
     *
     * @param query the query of the passwordInputField search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/password-input-fields",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<PasswordInputField>> searchPasswordInputFields(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of PasswordInputFields for query {}", query);
        Page<PasswordInputField> page = passwordInputFieldSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/password-input-fields");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
