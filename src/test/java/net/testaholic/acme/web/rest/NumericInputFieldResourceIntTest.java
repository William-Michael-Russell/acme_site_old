package net.testaholic.acme.web.rest;

import net.testaholic.acme.AcmeSiteApp;
import net.testaholic.acme.domain.NumericInputField;
import net.testaholic.acme.repository.NumericInputFieldRepository;
import net.testaholic.acme.repository.search.NumericInputFieldSearchRepository;

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
 * Test class for the NumericInputFieldResource REST controller.
 *
 * @see NumericInputFieldResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AcmeSiteApp.class)
@WebAppConfiguration
@IntegrationTest
public class NumericInputFieldResourceIntTest {


    private static final Integer DEFAULT_NUMERIC_FIELD = 1;
    private static final Integer UPDATED_NUMERIC_FIELD = 2;

    @Inject
    private NumericInputFieldRepository numericInputFieldRepository;

    @Inject
    private NumericInputFieldSearchRepository numericInputFieldSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restNumericInputFieldMockMvc;

    private NumericInputField numericInputField;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        NumericInputFieldResource numericInputFieldResource = new NumericInputFieldResource();
        ReflectionTestUtils.setField(numericInputFieldResource, "numericInputFieldSearchRepository", numericInputFieldSearchRepository);
        ReflectionTestUtils.setField(numericInputFieldResource, "numericInputFieldRepository", numericInputFieldRepository);
        this.restNumericInputFieldMockMvc = MockMvcBuilders.standaloneSetup(numericInputFieldResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        numericInputFieldSearchRepository.deleteAll();
        numericInputField = new NumericInputField();
        numericInputField.setNumericField(DEFAULT_NUMERIC_FIELD);
    }

    @Test
    @Transactional
    public void createNumericInputField() throws Exception {
        int databaseSizeBeforeCreate = numericInputFieldRepository.findAll().size();

        // Create the NumericInputField

        restNumericInputFieldMockMvc.perform(post("/api/numeric-input-fields")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(numericInputField)))
                .andExpect(status().isCreated());

        // Validate the NumericInputField in the database
        List<NumericInputField> numericInputFields = numericInputFieldRepository.findAll();
        assertThat(numericInputFields).hasSize(databaseSizeBeforeCreate + 1);
        NumericInputField testNumericInputField = numericInputFields.get(numericInputFields.size() - 1);
        assertThat(testNumericInputField.getNumericField()).isEqualTo(DEFAULT_NUMERIC_FIELD);

        // Validate the NumericInputField in ElasticSearch
        NumericInputField numericInputFieldEs = numericInputFieldSearchRepository.findOne(testNumericInputField.getId());
        assertThat(numericInputFieldEs).isEqualToComparingFieldByField(testNumericInputField);
    }

    @Test
    @Transactional
    public void checkNumericFieldIsRequired() throws Exception {
        int databaseSizeBeforeTest = numericInputFieldRepository.findAll().size();
        // set the field null
        numericInputField.setNumericField(null);

        // Create the NumericInputField, which fails.

        restNumericInputFieldMockMvc.perform(post("/api/numeric-input-fields")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(numericInputField)))
                .andExpect(status().isBadRequest());

        List<NumericInputField> numericInputFields = numericInputFieldRepository.findAll();
        assertThat(numericInputFields).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllNumericInputFields() throws Exception {
        // Initialize the database
        numericInputFieldRepository.saveAndFlush(numericInputField);

        // Get all the numericInputFields
        restNumericInputFieldMockMvc.perform(get("/api/numeric-input-fields?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(numericInputField.getId().intValue())))
                .andExpect(jsonPath("$.[*].numericField").value(hasItem(DEFAULT_NUMERIC_FIELD)));
    }

    @Test
    @Transactional
    public void getNumericInputField() throws Exception {
        // Initialize the database
        numericInputFieldRepository.saveAndFlush(numericInputField);

        // Get the numericInputField
        restNumericInputFieldMockMvc.perform(get("/api/numeric-input-fields/{id}", numericInputField.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(numericInputField.getId().intValue()))
            .andExpect(jsonPath("$.numericField").value(DEFAULT_NUMERIC_FIELD));
    }

    @Test
    @Transactional
    public void getNonExistingNumericInputField() throws Exception {
        // Get the numericInputField
        restNumericInputFieldMockMvc.perform(get("/api/numeric-input-fields/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateNumericInputField() throws Exception {
        // Initialize the database
        numericInputFieldRepository.saveAndFlush(numericInputField);
        numericInputFieldSearchRepository.save(numericInputField);
        int databaseSizeBeforeUpdate = numericInputFieldRepository.findAll().size();

        // Update the numericInputField
        NumericInputField updatedNumericInputField = new NumericInputField();
        updatedNumericInputField.setId(numericInputField.getId());
        updatedNumericInputField.setNumericField(UPDATED_NUMERIC_FIELD);

        restNumericInputFieldMockMvc.perform(put("/api/numeric-input-fields")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedNumericInputField)))
                .andExpect(status().isOk());

        // Validate the NumericInputField in the database
        List<NumericInputField> numericInputFields = numericInputFieldRepository.findAll();
        assertThat(numericInputFields).hasSize(databaseSizeBeforeUpdate);
        NumericInputField testNumericInputField = numericInputFields.get(numericInputFields.size() - 1);
        assertThat(testNumericInputField.getNumericField()).isEqualTo(UPDATED_NUMERIC_FIELD);

        // Validate the NumericInputField in ElasticSearch
        NumericInputField numericInputFieldEs = numericInputFieldSearchRepository.findOne(testNumericInputField.getId());
        assertThat(numericInputFieldEs).isEqualToComparingFieldByField(testNumericInputField);
    }

    @Test
    @Transactional
    public void deleteNumericInputField() throws Exception {
        // Initialize the database
        numericInputFieldRepository.saveAndFlush(numericInputField);
        numericInputFieldSearchRepository.save(numericInputField);
        int databaseSizeBeforeDelete = numericInputFieldRepository.findAll().size();

        // Get the numericInputField
        restNumericInputFieldMockMvc.perform(delete("/api/numeric-input-fields/{id}", numericInputField.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean numericInputFieldExistsInEs = numericInputFieldSearchRepository.exists(numericInputField.getId());
        assertThat(numericInputFieldExistsInEs).isFalse();

        // Validate the database is empty
        List<NumericInputField> numericInputFields = numericInputFieldRepository.findAll();
        assertThat(numericInputFields).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchNumericInputField() throws Exception {
        // Initialize the database
        numericInputFieldRepository.saveAndFlush(numericInputField);
        numericInputFieldSearchRepository.save(numericInputField);

        // Search the numericInputField
        restNumericInputFieldMockMvc.perform(get("/api/_search/numeric-input-fields?query=id:" + numericInputField.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(numericInputField.getId().intValue())))
            .andExpect(jsonPath("$.[*].numericField").value(hasItem(DEFAULT_NUMERIC_FIELD)));
    }
}
