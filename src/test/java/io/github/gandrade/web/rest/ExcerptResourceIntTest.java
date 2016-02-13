package io.github.gandrade.web.rest;

import io.github.gandrade.Application;
import io.github.gandrade.domain.Excerpt;
import io.github.gandrade.repository.ExcerptRepository;
import io.github.gandrade.repository.search.ExcerptSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the ExcerptResource REST controller.
 *
 * @see ExcerptResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ExcerptResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.of("Z"));

    private static final String DEFAULT_EXCERPT = "AAAAA";
    private static final String UPDATED_EXCERPT = "BBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_CREATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_CREATED_DATE_STR = dateTimeFormatter.format(DEFAULT_CREATED_DATE);

    @Inject
    private ExcerptRepository excerptRepository;

    @Inject
    private ExcerptSearchRepository excerptSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restExcerptMockMvc;

    private Excerpt excerpt;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ExcerptResource excerptResource = new ExcerptResource();
        ReflectionTestUtils.setField(excerptResource, "excerptSearchRepository", excerptSearchRepository);
        ReflectionTestUtils.setField(excerptResource, "excerptRepository", excerptRepository);
        this.restExcerptMockMvc = MockMvcBuilders.standaloneSetup(excerptResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        excerpt = new Excerpt();
        excerpt.setExcerpt(DEFAULT_EXCERPT);
        excerpt.setCreatedDate(DEFAULT_CREATED_DATE);
    }

    @Test
    @Transactional
    public void createExcerpt() throws Exception {
        int databaseSizeBeforeCreate = excerptRepository.findAll().size();

        // Create the Excerpt

        restExcerptMockMvc.perform(post("/api/excerpts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(excerpt)))
                .andExpect(status().isCreated());

        // Validate the Excerpt in the database
        List<Excerpt> excerpts = excerptRepository.findAll();
        assertThat(excerpts).hasSize(databaseSizeBeforeCreate + 1);
        Excerpt testExcerpt = excerpts.get(excerpts.size() - 1);
        assertThat(testExcerpt.getExcerpt()).isEqualTo(DEFAULT_EXCERPT);
        assertThat(testExcerpt.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
    }

    @Test
    @Transactional
    public void checkExcerptIsRequired() throws Exception {
        int databaseSizeBeforeTest = excerptRepository.findAll().size();
        // set the field null
        excerpt.setExcerpt(null);

        // Create the Excerpt, which fails.

        restExcerptMockMvc.perform(post("/api/excerpts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(excerpt)))
                .andExpect(status().isBadRequest());

        List<Excerpt> excerpts = excerptRepository.findAll();
        assertThat(excerpts).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCreatedDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = excerptRepository.findAll().size();
        // set the field null
        excerpt.setCreatedDate(null);

        // Create the Excerpt, which fails.

        restExcerptMockMvc.perform(post("/api/excerpts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(excerpt)))
                .andExpect(status().isBadRequest());

        List<Excerpt> excerpts = excerptRepository.findAll();
        assertThat(excerpts).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllExcerpts() throws Exception {
        // Initialize the database
        excerptRepository.saveAndFlush(excerpt);

        // Get all the excerpts
        restExcerptMockMvc.perform(get("/api/excerpts?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(excerpt.getId().intValue())))
                .andExpect(jsonPath("$.[*].excerpt").value(hasItem(DEFAULT_EXCERPT.toString())))
                .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE_STR)));
    }

    @Test
    @Transactional
    public void getExcerpt() throws Exception {
        // Initialize the database
        excerptRepository.saveAndFlush(excerpt);

        // Get the excerpt
        restExcerptMockMvc.perform(get("/api/excerpts/{id}", excerpt.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(excerpt.getId().intValue()))
            .andExpect(jsonPath("$.excerpt").value(DEFAULT_EXCERPT.toString()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE_STR));
    }

    @Test
    @Transactional
    public void getNonExistingExcerpt() throws Exception {
        // Get the excerpt
        restExcerptMockMvc.perform(get("/api/excerpts/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateExcerpt() throws Exception {
        // Initialize the database
        excerptRepository.saveAndFlush(excerpt);

		int databaseSizeBeforeUpdate = excerptRepository.findAll().size();

        // Update the excerpt
        excerpt.setExcerpt(UPDATED_EXCERPT);
        excerpt.setCreatedDate(UPDATED_CREATED_DATE);

        restExcerptMockMvc.perform(put("/api/excerpts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(excerpt)))
                .andExpect(status().isOk());

        // Validate the Excerpt in the database
        List<Excerpt> excerpts = excerptRepository.findAll();
        assertThat(excerpts).hasSize(databaseSizeBeforeUpdate);
        Excerpt testExcerpt = excerpts.get(excerpts.size() - 1);
        assertThat(testExcerpt.getExcerpt()).isEqualTo(UPDATED_EXCERPT);
        assertThat(testExcerpt.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    public void deleteExcerpt() throws Exception {
        // Initialize the database
        excerptRepository.saveAndFlush(excerpt);

		int databaseSizeBeforeDelete = excerptRepository.findAll().size();

        // Get the excerpt
        restExcerptMockMvc.perform(delete("/api/excerpts/{id}", excerpt.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Excerpt> excerpts = excerptRepository.findAll();
        assertThat(excerpts).hasSize(databaseSizeBeforeDelete - 1);
    }
}
