package net.testaholic.acme.web.rest;

import net.testaholic.acme.AcmeSiteApp;
import net.testaholic.acme.domain.AlpaNumericInputField;
import net.testaholic.acme.repository.AlpaNumericInputFieldRepository;
import net.testaholic.acme.repository.search.AlpaNumericInputFieldSearchRepository;

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
 * Test class for the AlpaNumericInputFieldResource REST controller.
 *
 * @see AlpaNumericInputFieldResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AcmeSiteApp.class)
@WebAppConfiguration
@IntegrationTest
public class AlpaNumericInputFieldResourceIntTest {

    private static final String DEFAULT_ALPHA_NUMERIC_FIELD = "A";
    private static final String UPDATED_ALPHA_NUMERIC_FIELD = "B";

    @Inject
    private AlpaNumericInputFieldRepository alpaNumericInputFieldRepository;

    @Inject
    private AlpaNumericInputFieldSearchRepository alpaNumericInputFieldSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restAlpaNumericInputFieldMockMvc;

    private AlpaNumericInputField alpaNumericInputField;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AlpaNumericInputFieldResource alpaNumericInputFieldResource = new AlpaNumericInputFieldResource();
        ReflectionTestUtils.setField(alpaNumericInputFieldResource, "alpaNumericInputFieldSearchRepository", alpaNumericInputFieldSearchRepository);
        ReflectionTestUtils.setField(alpaNumericInputFieldResource, "alpaNumericInputFieldRepository", alpaNumericInputFieldRepository);
        this.restAlpaNumericInputFieldMockMvc = MockMvcBuilders.standaloneSetup(alpaNumericInputFieldResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        alpaNumericInputFieldSearchRepository.deleteAll();
        alpaNumericInputField = new AlpaNumericInputField();
        alpaNumericInputField.setAlphaNumericField(DEFAULT_ALPHA_NUMERIC_FIELD);
    }

    @Test
    @Transactional
    public void createAlpaNumericInputField() throws Exception {
        int databaseSizeBeforeCreate = alpaNumericInputFieldRepository.findAll().size();

        // Create the AlpaNumericInputField

        restAlpaNumericInputFieldMockMvc.perform(post("/api/alpa-numeric-input-fields")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(alpaNumericInputField)))
                .andExpect(status().isCreated());

        // Validate the AlpaNumericInputField in the database
        List<AlpaNumericInputField> alpaNumericInputFields = alpaNumericInputFieldRepository.findAll();
        assertThat(alpaNumericInputFields).hasSize(databaseSizeBeforeCreate + 1);
        AlpaNumericInputField testAlpaNumericInputField = alpaNumericInputFields.get(alpaNumericInputFields.size() - 1);
        assertThat(testAlpaNumericInputField.getAlphaNumericField()).isEqualTo(DEFAULT_ALPHA_NUMERIC_FIELD);

        // Validate the AlpaNumericInputField in ElasticSearch
        AlpaNumericInputField alpaNumericInputFieldEs = alpaNumericInputFieldSearchRepository.findOne(testAlpaNumericInputField.getId());
        assertThat(alpaNumericInputFieldEs).isEqualToComparingFieldByField(testAlpaNumericInputField);
    }

    @Test
    @Transactional
    public void checkAlphaNumericFieldIsRequired() throws Exception {
        int databaseSizeBeforeTest = alpaNumericInputFieldRepository.findAll().size();
        // set the field null
        alpaNumericInputField.setAlphaNumericField(null);

        // Create the AlpaNumericInputField, which fails.

        restAlpaNumericInputFieldMockMvc.perform(post("/api/alpa-numeric-input-fields")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(alpaNumericInputField)))
                .andExpect(status().isBadRequest());

        List<AlpaNumericInputField> alpaNumericInputFields = alpaNumericInputFieldRepository.findAll();
        assertThat(alpaNumericInputFields).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAlpaNumericInputFields() throws Exception {
        // Initialize the database
        alpaNumericInputFieldRepository.saveAndFlush(alpaNumericInputField);

        // Get all the alpaNumericInputFields
        restAlpaNumericInputFieldMockMvc.perform(get("/api/alpa-numeric-input-fields?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(alpaNumericInputField.getId().intValue())))
                .andExpect(jsonPath("$.[*].alphaNumericField").value(hasItem(DEFAULT_ALPHA_NUMERIC_FIELD.toString())));
    }

    @Test
    @Transactional
    public void getAlpaNumericInputField() throws Exception {
        // Initialize the database
        alpaNumericInputFieldRepository.saveAndFlush(alpaNumericInputField);

        // Get the alpaNumericInputField
        restAlpaNumericInputFieldMockMvc.perform(get("/api/alpa-numeric-input-fields/{id}", alpaNumericInputField.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(alpaNumericInputField.getId().intValue()))
            .andExpect(jsonPath("$.alphaNumericField").value(DEFAULT_ALPHA_NUMERIC_FIELD.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAlpaNumericInputField() throws Exception {
        // Get the alpaNumericInputField
        restAlpaNumericInputFieldMockMvc.perform(get("/api/alpa-numeric-input-fields/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAlpaNumericInputField() throws Exception {
        // Initialize the database
        alpaNumericInputFieldRepository.saveAndFlush(alpaNumericInputField);
        alpaNumericInputFieldSearchRepository.save(alpaNumericInputField);
        int databaseSizeBeforeUpdate = alpaNumericInputFieldRepository.findAll().size();

        // Update the alpaNumericInputField
        AlpaNumericInputField updatedAlpaNumericInputField = new AlpaNumericInputField();
        updatedAlpaNumericInputField.setId(alpaNumericInputField.getId());
        updatedAlpaNumericInputField.setAlphaNumericField(UPDATED_ALPHA_NUMERIC_FIELD);

        restAlpaNumericInputFieldMockMvc.perform(put("/api/alpa-numeric-input-fields")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedAlpaNumericInputField)))
                .andExpect(status().isOk());

        // Validate the AlpaNumericInputField in the database
        List<AlpaNumericInputField> alpaNumericInputFields = alpaNumericInputFieldRepository.findAll();
        assertThat(alpaNumericInputFields).hasSize(databaseSizeBeforeUpdate);
        AlpaNumericInputField testAlpaNumericInputField = alpaNumericInputFields.get(alpaNumericInputFields.size() - 1);
        assertThat(testAlpaNumericInputField.getAlphaNumericField()).isEqualTo(UPDATED_ALPHA_NUMERIC_FIELD);

        // Validate the AlpaNumericInputField in ElasticSearch
        AlpaNumericInputField alpaNumericInputFieldEs = alpaNumericInputFieldSearchRepository.findOne(testAlpaNumericInputField.getId());
        assertThat(alpaNumericInputFieldEs).isEqualToComparingFieldByField(testAlpaNumericInputField);
    }

    @Test
    @Transactional
    public void deleteAlpaNumericInputField() throws Exception {
        // Initialize the database
        alpaNumericInputFieldRepository.saveAndFlush(alpaNumericInputField);
        alpaNumericInputFieldSearchRepository.save(alpaNumericInputField);
        int databaseSizeBeforeDelete = alpaNumericInputFieldRepository.findAll().size();

        // Get the alpaNumericInputField
        restAlpaNumericInputFieldMockMvc.perform(delete("/api/alpa-numeric-input-fields/{id}", alpaNumericInputField.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean alpaNumericInputFieldExistsInEs = alpaNumericInputFieldSearchRepository.exists(alpaNumericInputField.getId());
        assertThat(alpaNumericInputFieldExistsInEs).isFalse();

        // Validate the database is empty
        List<AlpaNumericInputField> alpaNumericInputFields = alpaNumericInputFieldRepository.findAll();
        assertThat(alpaNumericInputFields).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchAlpaNumericInputField() throws Exception {
        // Initialize the database
        alpaNumericInputFieldRepository.saveAndFlush(alpaNumericInputField);
        alpaNumericInputFieldSearchRepository.save(alpaNumericInputField);

        // Search the alpaNumericInputField
        restAlpaNumericInputFieldMockMvc.perform(get("/api/_search/alpa-numeric-input-fields?query=id:" + alpaNumericInputField.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(alpaNumericInputField.getId().intValue())))
            .andExpect(jsonPath("$.[*].alphaNumericField").value(hasItem(DEFAULT_ALPHA_NUMERIC_FIELD.toString())));
    }
}
