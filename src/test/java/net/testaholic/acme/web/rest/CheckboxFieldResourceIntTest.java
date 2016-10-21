package net.testaholic.acme.web.rest;

import net.testaholic.acme.AcmeSiteApp;
import net.testaholic.acme.domain.CheckboxField;
import net.testaholic.acme.repository.CheckboxFieldRepository;
import net.testaholic.acme.repository.search.CheckboxFieldSearchRepository;

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
 * Test class for the CheckboxFieldResource REST controller.
 *
 * @see CheckboxFieldResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AcmeSiteApp.class)
@WebAppConfiguration
@IntegrationTest
public class CheckboxFieldResourceIntTest {

    private static final String DEFAULT_CHECKBOX_OPTION = "AAAAA";
    private static final String UPDATED_CHECKBOX_OPTION = "BBBBB";

    @Inject
    private CheckboxFieldRepository checkboxFieldRepository;

    @Inject
    private CheckboxFieldSearchRepository checkboxFieldSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restCheckboxFieldMockMvc;

    private CheckboxField checkboxField;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CheckboxFieldResource checkboxFieldResource = new CheckboxFieldResource();
        ReflectionTestUtils.setField(checkboxFieldResource, "checkboxFieldSearchRepository", checkboxFieldSearchRepository);
        ReflectionTestUtils.setField(checkboxFieldResource, "checkboxFieldRepository", checkboxFieldRepository);
        this.restCheckboxFieldMockMvc = MockMvcBuilders.standaloneSetup(checkboxFieldResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        checkboxFieldSearchRepository.deleteAll();
        checkboxField = new CheckboxField();
        checkboxField.setCheckboxOption(DEFAULT_CHECKBOX_OPTION);
    }

    @Test
    @Transactional
    public void createCheckboxField() throws Exception {
        int databaseSizeBeforeCreate = checkboxFieldRepository.findAll().size();

        // Create the CheckboxField

        restCheckboxFieldMockMvc.perform(post("/api/checkbox-fields")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(checkboxField)))
                .andExpect(status().isCreated());

        // Validate the CheckboxField in the database
        List<CheckboxField> checkboxFields = checkboxFieldRepository.findAll();
        assertThat(checkboxFields).hasSize(databaseSizeBeforeCreate + 1);
        CheckboxField testCheckboxField = checkboxFields.get(checkboxFields.size() - 1);
        assertThat(testCheckboxField.getCheckboxOption()).isEqualTo(DEFAULT_CHECKBOX_OPTION);

        // Validate the CheckboxField in ElasticSearch
        CheckboxField checkboxFieldEs = checkboxFieldSearchRepository.findOne(testCheckboxField.getId());
        assertThat(checkboxFieldEs).isEqualToComparingFieldByField(testCheckboxField);
    }

    @Test
    @Transactional
    public void checkCheckboxOptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = checkboxFieldRepository.findAll().size();
        // set the field null
        checkboxField.setCheckboxOption(null);

        // Create the CheckboxField, which fails.

        restCheckboxFieldMockMvc.perform(post("/api/checkbox-fields")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(checkboxField)))
                .andExpect(status().isBadRequest());

        List<CheckboxField> checkboxFields = checkboxFieldRepository.findAll();
        assertThat(checkboxFields).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCheckboxFields() throws Exception {
        // Initialize the database
        checkboxFieldRepository.saveAndFlush(checkboxField);

        // Get all the checkboxFields
        restCheckboxFieldMockMvc.perform(get("/api/checkbox-fields?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(checkboxField.getId().intValue())))
                .andExpect(jsonPath("$.[*].checkboxOption").value(hasItem(DEFAULT_CHECKBOX_OPTION.toString())));
    }

    @Test
    @Transactional
    public void getCheckboxField() throws Exception {
        // Initialize the database
        checkboxFieldRepository.saveAndFlush(checkboxField);

        // Get the checkboxField
        restCheckboxFieldMockMvc.perform(get("/api/checkbox-fields/{id}", checkboxField.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(checkboxField.getId().intValue()))
            .andExpect(jsonPath("$.checkboxOption").value(DEFAULT_CHECKBOX_OPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCheckboxField() throws Exception {
        // Get the checkboxField
        restCheckboxFieldMockMvc.perform(get("/api/checkbox-fields/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCheckboxField() throws Exception {
        // Initialize the database
        checkboxFieldRepository.saveAndFlush(checkboxField);
        checkboxFieldSearchRepository.save(checkboxField);
        int databaseSizeBeforeUpdate = checkboxFieldRepository.findAll().size();

        // Update the checkboxField
        CheckboxField updatedCheckboxField = new CheckboxField();
        updatedCheckboxField.setId(checkboxField.getId());
        updatedCheckboxField.setCheckboxOption(UPDATED_CHECKBOX_OPTION);

        restCheckboxFieldMockMvc.perform(put("/api/checkbox-fields")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedCheckboxField)))
                .andExpect(status().isOk());

        // Validate the CheckboxField in the database
        List<CheckboxField> checkboxFields = checkboxFieldRepository.findAll();
        assertThat(checkboxFields).hasSize(databaseSizeBeforeUpdate);
        CheckboxField testCheckboxField = checkboxFields.get(checkboxFields.size() - 1);
        assertThat(testCheckboxField.getCheckboxOption()).isEqualTo(UPDATED_CHECKBOX_OPTION);

        // Validate the CheckboxField in ElasticSearch
        CheckboxField checkboxFieldEs = checkboxFieldSearchRepository.findOne(testCheckboxField.getId());
        assertThat(checkboxFieldEs).isEqualToComparingFieldByField(testCheckboxField);
    }

    @Test
    @Transactional
    public void deleteCheckboxField() throws Exception {
        // Initialize the database
        checkboxFieldRepository.saveAndFlush(checkboxField);
        checkboxFieldSearchRepository.save(checkboxField);
        int databaseSizeBeforeDelete = checkboxFieldRepository.findAll().size();

        // Get the checkboxField
        restCheckboxFieldMockMvc.perform(delete("/api/checkbox-fields/{id}", checkboxField.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean checkboxFieldExistsInEs = checkboxFieldSearchRepository.exists(checkboxField.getId());
        assertThat(checkboxFieldExistsInEs).isFalse();

        // Validate the database is empty
        List<CheckboxField> checkboxFields = checkboxFieldRepository.findAll();
        assertThat(checkboxFields).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchCheckboxField() throws Exception {
        // Initialize the database
        checkboxFieldRepository.saveAndFlush(checkboxField);
        checkboxFieldSearchRepository.save(checkboxField);

        // Search the checkboxField
        restCheckboxFieldMockMvc.perform(get("/api/_search/checkbox-fields?query=id:" + checkboxField.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(checkboxField.getId().intValue())))
            .andExpect(jsonPath("$.[*].checkboxOption").value(hasItem(DEFAULT_CHECKBOX_OPTION.toString())));
    }
}
