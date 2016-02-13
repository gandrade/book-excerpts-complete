package io.github.gandrade.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.gandrade.domain.Excerpt;
import io.github.gandrade.repository.ExcerptRepository;
import io.github.gandrade.repository.search.ExcerptSearchRepository;
import io.github.gandrade.web.rest.util.HeaderUtil;
import io.github.gandrade.web.rest.util.PaginationUtil;
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
 * REST controller for managing Excerpt.
 */
@RestController
@RequestMapping("/api")
public class ExcerptResource {

    private final Logger log = LoggerFactory.getLogger(ExcerptResource.class);
        
    @Inject
    private ExcerptRepository excerptRepository;
    
    @Inject
    private ExcerptSearchRepository excerptSearchRepository;
    
    /**
     * POST  /excerpts -> Create a new excerpt.
     */
    @RequestMapping(value = "/excerpts",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Excerpt> createExcerpt(@Valid @RequestBody Excerpt excerpt) throws URISyntaxException {
        log.debug("REST request to save Excerpt : {}", excerpt);
        if (excerpt.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("excerpt", "idexists", "A new excerpt cannot already have an ID")).body(null);
        }
        Excerpt result = excerptRepository.save(excerpt);
        excerptSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/excerpts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("excerpt", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /excerpts -> Updates an existing excerpt.
     */
    @RequestMapping(value = "/excerpts",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Excerpt> updateExcerpt(@Valid @RequestBody Excerpt excerpt) throws URISyntaxException {
        log.debug("REST request to update Excerpt : {}", excerpt);
        if (excerpt.getId() == null) {
            return createExcerpt(excerpt);
        }
        Excerpt result = excerptRepository.save(excerpt);
        excerptSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("excerpt", excerpt.getId().toString()))
            .body(result);
    }

    /**
     * GET  /excerpts -> get all the excerpts.
     */
    @RequestMapping(value = "/excerpts",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Excerpt>> getAllExcerpts(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Excerpts");
        Page<Excerpt> page = excerptRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/excerpts");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /excerpts/:id -> get the "id" excerpt.
     */
    @RequestMapping(value = "/excerpts/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Excerpt> getExcerpt(@PathVariable Long id) {
        log.debug("REST request to get Excerpt : {}", id);
        Excerpt excerpt = excerptRepository.findOne(id);
        return Optional.ofNullable(excerpt)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /excerpts/:id -> delete the "id" excerpt.
     */
    @RequestMapping(value = "/excerpts/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteExcerpt(@PathVariable Long id) {
        log.debug("REST request to delete Excerpt : {}", id);
        excerptRepository.delete(id);
        excerptSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("excerpt", id.toString())).build();
    }

    /**
     * SEARCH  /_search/excerpts/:query -> search for the excerpt corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/excerpts/{query:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Excerpt> searchExcerpts(@PathVariable String query) {
        log.debug("REST request to search Excerpts for query {}", query);
        return StreamSupport
            .stream(excerptSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
