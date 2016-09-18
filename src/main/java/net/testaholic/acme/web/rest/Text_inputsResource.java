package net.testaholic.acme.web.rest;

import com.codahale.metrics.annotation.Timed;
import net.testaholic.acme.domain.Text_inputs;
import net.testaholic.acme.repository.Text_inputsRepository;
import net.testaholic.acme.repository.search.Text_inputsSearchRepository;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Text_inputs.
 */
@RestController
@RequestMapping("/api")
public class Text_inputsResource {

    private final Logger log = LoggerFactory.getLogger(Text_inputsResource.class);
        
    @Inject
    private Text_inputsRepository text_inputsRepository;
    
    @Inject
    private Text_inputsSearchRepository text_inputsSearchRepository;
    
    /**
     * POST  /text-inputs : Create a new text_inputs.
     *
     * @param text_inputs the text_inputs to create
     * @return the ResponseEntity with status 201 (Created) and with body the new text_inputs, or with status 400 (Bad Request) if the text_inputs has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/text-inputs",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Text_inputs> createText_inputs(@RequestBody Text_inputs text_inputs) throws URISyntaxException {
        log.debug("REST request to save Text_inputs : {}", text_inputs);
        if (text_inputs.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("text_inputs", "idexists", "A new text_inputs cannot already have an ID")).body(null);
        }
        Text_inputs result = text_inputsRepository.save(text_inputs);
        text_inputsSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/text-inputs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("text_inputs", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /text-inputs : Updates an existing text_inputs.
     *
     * @param text_inputs the text_inputs to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated text_inputs,
     * or with status 400 (Bad Request) if the text_inputs is not valid,
     * or with status 500 (Internal Server Error) if the text_inputs couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/text-inputs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Text_inputs> updateText_inputs(@RequestBody Text_inputs text_inputs) throws URISyntaxException {
        log.debug("REST request to update Text_inputs : {}", text_inputs);
        if (text_inputs.getId() == null) {
            return createText_inputs(text_inputs);
        }
        Text_inputs result = text_inputsRepository.save(text_inputs);
        text_inputsSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("text_inputs", text_inputs.getId().toString()))
            .body(result);
    }

    /**
     * GET  /text-inputs : get all the text_inputs.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of text_inputs in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/text-inputs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Text_inputs>> getAllText_inputs(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Text_inputs");
        Page<Text_inputs> page = text_inputsRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/text-inputs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /text-inputs/:id : get the "id" text_inputs.
     *
     * @param id the id of the text_inputs to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the text_inputs, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/text-inputs/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Text_inputs> getText_inputs(@PathVariable Long id) {
        log.debug("REST request to get Text_inputs : {}", id);
        Text_inputs text_inputs = text_inputsRepository.findOne(id);
        return Optional.ofNullable(text_inputs)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /text-inputs/:id : delete the "id" text_inputs.
     *
     * @param id the id of the text_inputs to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/text-inputs/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteText_inputs(@PathVariable Long id) {
        log.debug("REST request to delete Text_inputs : {}", id);
        text_inputsRepository.delete(id);
        text_inputsSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("text_inputs", id.toString())).build();
    }

    /**
     * SEARCH  /_search/text-inputs?query=:query : search for the text_inputs corresponding
     * to the query.
     *
     * @param query the query of the text_inputs search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/text-inputs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Text_inputs>> searchText_inputs(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Text_inputs for query {}", query);
        Page<Text_inputs> page = text_inputsSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/text-inputs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
