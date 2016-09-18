package net.testaholic.acme.web.rest;

import net.testaholic.acme.AcmeSiteApp;
import net.testaholic.acme.domain.AlphaNumericInputField;
import net.testaholic.acme.repository.AlphaNumericInputFieldRepository;
import net.testaholic.acme.repository.search.AlphaNumericInputFieldSearchRepository;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the AlphaNumericInputFieldResource REST controller.
 *
 * @see AlphaNumericInputFieldResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AcmeSiteApp.class)
@WebAppConfiguration
@IntegrationTest
public class AlphaNumericInputFieldResourceIntTest {

    private static final String DEFAULT_ALPHA_NUMERIC_FIELD = "A";
    private static final String UPDATED_ALPHA_NUMERIC_FIELD = "B";

    @Inject
    private AlphaNumericInputFieldRepository alphaNumericInputFieldRepository;

    @Inject
    private AlphaNumericInputFieldSearchRepository alphaNumericInputFieldSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restAlphaNumericInputFieldMockMvc;

    private AlphaNumericInputField alphaNumericInputField;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AlphaNumericInputFieldResource alphaNumericInputFieldResource = new AlphaNumericInputFieldResource();
        ReflectionTestUtils.setField(alphaNumericInputFieldResource, "alphaNumericInputFieldSearchRepository", alphaNumericInputFieldSearchRepository);
        ReflectionTestUtils.setField(alphaNumericInputFieldResource, "alphaNumericInputFieldRepository", alphaNumericInputFieldRepository);
        this.restAlphaNumericInputFieldMockMvc = MockMvcBuilders.standaloneSetup(alphaNumericInputFieldResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        alphaNumericInputFieldSearchRepository.deleteAll();
        alphaNumericInputField = new AlphaNumericInputField();
        alphaNumericInputField.setAlphaNumericField(DEFAULT_ALPHA_NUMERIC_FIELD);
    }

    @Test
    @Transactional
    public void createAlphaNumericInputField() throws Exception {
        int databaseSizeBeforeCreate = alphaNumericInputFieldRepository.findAll().size();

        // Create the AlphaNumericInputField

        restAlphaNumericInputFieldMockMvc.perform(post("/api/alpha-numeric-input-fields")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(alphaNumericInputField)))
                .andExpect(status().isCreated());

        // Validate the AlphaNumericInputField in the database
        List<AlphaNumericInputField> alphaNumericInputFields = alphaNumericInputFieldRepository.findAll();
        assertThat(alphaNumericInputFields).hasSize(databaseSizeBeforeCreate + 1);
        AlphaNumericInputField testAlphaNumericInputField = alphaNumericInputFields.get(alphaNumericInputFields.size() - 1);
        assertThat(testAlphaNumericInputField.getAlphaNumericField()).isEqualTo(DEFAULT_ALPHA_NUMERIC_FIELD);

        // Validate the AlphaNumericInputField in ElasticSearch
        AlphaNumericInputField alphaNumericInputFieldEs = alphaNumericInputFieldSearchRepository.findOne(testAlphaNumericInputField.getId());
        assertThat(alphaNumericInputFieldEs).isEqualToComparingFieldByField(testAlphaNumericInputField);
    }

    @Test
    @Transactional
    public void checkAlphaNumericFieldIsRequired() throws Exception {
        int databaseSizeBeforeTest = alphaNumericInputFieldRepository.findAll().size();
        // set the field null
        alphaNumericInputField.setAlphaNumericField(null);

        // Create the AlphaNumericInputField, which fails.

        restAlphaNumericInputFieldMockMvc.perform(post("/api/alpha-numeric-input-fields")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(alphaNumericInputField)))
                .andExpect(status().isBadRequest());

        List<AlphaNumericInputField> alphaNumericInputFields = alphaNumericInputFieldRepository.findAll();
        assertThat(alphaNumericInputFields).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAlphaNumericInputFields() throws Exception {
        // Initialize the database
        alphaNumericInputFieldRepository.saveAndFlush(alphaNumericInputField);

        // Get all the alphaNumericInputFields
        restAlphaNumericInputFieldMockMvc.perform(get("/api/alpha-numeric-input-fields?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(alphaNumericInputField.getId().intValue())))
                .andExpect(jsonPath("$.[*].alphaNumericField").value(hasItem(DEFAULT_ALPHA_NUMERIC_FIELD.toString())));
    }

    @Test
    @Transactional
    public void getAlphaNumericInputField() throws Exception {
        // Initialize the database
        alphaNumericInputFieldRepository.saveAndFlush(alphaNumericInputField);

        // Get the alphaNumericInputField
        restAlphaNumericInputFieldMockMvc.perform(get("/api/alpha-numeric-input-fields/{id}", alphaNumericInputField.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(alphaNumericInputField.getId().intValue()))
            .andExpect(jsonPath("$.alphaNumericField").value(DEFAULT_ALPHA_NUMERIC_FIELD.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAlphaNumericInputField() throws Exception {
        // Get the alphaNumericInputField
        restAlphaNumericInputFieldMockMvc.perform(get("/api/alpha-numeric-input-fields/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAlphaNumericInputField() throws Exception {
        // Initialize the database
        alphaNumericInputFieldRepository.saveAndFlush(alphaNumericInputField);
        alphaNumericInputFieldSearchRepository.save(alphaNumericInputField);
        int databaseSizeBeforeUpdate = alphaNumericInputFieldRepository.findAll().size();

        // Update the alphaNumericInputField
        AlphaNumericInputField updatedAlphaNumericInputField = new AlphaNumericInputField();
        updatedAlphaNumericInputField.setId(alphaNumericInputField.getId());
        updatedAlphaNumericInputField.setAlphaNumericField(UPDATED_ALPHA_NUMERIC_FIELD);

        restAlphaNumericInputFieldMockMvc.perform(put("/api/alpha-numeric-input-fields")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedAlphaNumericInputField)))
                .andExpect(status().isOk());

        // Validate the AlphaNumericInputField in the database
        List<AlphaNumericInputField> alphaNumericInputFields = alphaNumericInputFieldRepository.findAll();
        assertThat(alphaNumericInputFields).hasSize(databaseSizeBeforeUpdate);
        AlphaNumericInputField testAlphaNumericInputField = alphaNumericInputFields.get(alphaNumericInputFields.size() - 1);
        assertThat(testAlphaNumericInputField.getAlphaNumericField()).isEqualTo(UPDATED_ALPHA_NUMERIC_FIELD);

        // Validate the AlphaNumericInputField in ElasticSearch
        AlphaNumericInputField alphaNumericInputFieldEs = alphaNumericInputFieldSearchRepository.findOne(testAlphaNumericInputField.getId());
        assertThat(alphaNumericInputFieldEs).isEqualToComparingFieldByField(testAlphaNumericInputField);
    }

    @Test
    @Transactional
    public void deleteAlphaNumericInputField() throws Exception {
        // Initialize the database
        alphaNumericInputFieldRepository.saveAndFlush(alphaNumericInputField);
        alphaNumericInputFieldSearchRepository.save(alphaNumericInputField);
        int databaseSizeBeforeDelete = alphaNumericInputFieldRepository.findAll().size();

        // Get the alphaNumericInputField
        restAlphaNumericInputFieldMockMvc.perform(delete("/api/alpha-numeric-input-fields/{id}", alphaNumericInputField.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean alphaNumericInputFieldExistsInEs = alphaNumericInputFieldSearchRepository.exists(alphaNumericInputField.getId());
        assertThat(alphaNumericInputFieldExistsInEs).isFalse();

        // Validate the database is empty
        List<AlphaNumericInputField> alphaNumericInputFields = alphaNumericInputFieldRepository.findAll();
        assertThat(alphaNumericInputFields).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchAlphaNumericInputField() throws Exception {
        // Initialize the database
        alphaNumericInputFieldRepository.saveAndFlush(alphaNumericInputField);
        alphaNumericInputFieldSearchRepository.save(alphaNumericInputField);

        // Search the alphaNumericInputField
        restAlphaNumericInputFieldMockMvc.perform(get("/api/_search/alpha-numeric-input-fields?query=id:" + alphaNumericInputField.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(alphaNumericInputField.getId().intValue())))
            .andExpect(jsonPath("$.[*].alphaNumericField").value(hasItem(DEFAULT_ALPHA_NUMERIC_FIELD.toString())));
    }
}
