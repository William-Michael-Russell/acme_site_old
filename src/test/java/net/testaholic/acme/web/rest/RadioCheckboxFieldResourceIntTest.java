package net.testaholic.acme.web.rest;

import net.testaholic.acme.AcmeSiteApp;
import net.testaholic.acme.domain.RadioCheckboxField;
import net.testaholic.acme.repository.RadioCheckboxFieldRepository;
import net.testaholic.acme.repository.search.RadioCheckboxFieldSearchRepository;

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
 * Test class for the RadioCheckboxFieldResource REST controller.
 *
 * @see RadioCheckboxFieldResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AcmeSiteApp.class)
@WebAppConfiguration
@IntegrationTest
public class RadioCheckboxFieldResourceIntTest {


    private static final Boolean DEFAULT_RADIO_CHECKBOX = false;
    private static final Boolean UPDATED_RADIO_CHECKBOX = true;

    @Inject
    private RadioCheckboxFieldRepository radioCheckboxFieldRepository;

    @Inject
    private RadioCheckboxFieldSearchRepository radioCheckboxFieldSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restRadioCheckboxFieldMockMvc;

    private RadioCheckboxField radioCheckboxField;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RadioCheckboxFieldResource radioCheckboxFieldResource = new RadioCheckboxFieldResource();
        ReflectionTestUtils.setField(radioCheckboxFieldResource, "radioCheckboxFieldSearchRepository", radioCheckboxFieldSearchRepository);
        ReflectionTestUtils.setField(radioCheckboxFieldResource, "radioCheckboxFieldRepository", radioCheckboxFieldRepository);
        this.restRadioCheckboxFieldMockMvc = MockMvcBuilders.standaloneSetup(radioCheckboxFieldResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        radioCheckboxFieldSearchRepository.deleteAll();
        radioCheckboxField = new RadioCheckboxField();
        radioCheckboxField.setRadioCheckbox(DEFAULT_RADIO_CHECKBOX);
    }

    @Test
    @Transactional
    public void createRadioCheckboxField() throws Exception {
        int databaseSizeBeforeCreate = radioCheckboxFieldRepository.findAll().size();

        // Create the RadioCheckboxField

        restRadioCheckboxFieldMockMvc.perform(post("/api/radio-checkbox-fields")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(radioCheckboxField)))
                .andExpect(status().isCreated());

        // Validate the RadioCheckboxField in the database
        List<RadioCheckboxField> radioCheckboxFields = radioCheckboxFieldRepository.findAll();
        assertThat(radioCheckboxFields).hasSize(databaseSizeBeforeCreate + 1);
        RadioCheckboxField testRadioCheckboxField = radioCheckboxFields.get(radioCheckboxFields.size() - 1);
        assertThat(testRadioCheckboxField.isRadioCheckbox()).isEqualTo(DEFAULT_RADIO_CHECKBOX);

        // Validate the RadioCheckboxField in ElasticSearch
        RadioCheckboxField radioCheckboxFieldEs = radioCheckboxFieldSearchRepository.findOne(testRadioCheckboxField.getId());
        assertThat(radioCheckboxFieldEs).isEqualToComparingFieldByField(testRadioCheckboxField);
    }

    @Test
    @Transactional
    public void checkRadioCheckboxIsRequired() throws Exception {
        int databaseSizeBeforeTest = radioCheckboxFieldRepository.findAll().size();
        // set the field null
        radioCheckboxField.setRadioCheckbox(null);

        // Create the RadioCheckboxField, which fails.

        restRadioCheckboxFieldMockMvc.perform(post("/api/radio-checkbox-fields")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(radioCheckboxField)))
                .andExpect(status().isBadRequest());

        List<RadioCheckboxField> radioCheckboxFields = radioCheckboxFieldRepository.findAll();
        assertThat(radioCheckboxFields).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRadioCheckboxFields() throws Exception {
        // Initialize the database
        radioCheckboxFieldRepository.saveAndFlush(radioCheckboxField);

        // Get all the radioCheckboxFields
        restRadioCheckboxFieldMockMvc.perform(get("/api/radio-checkbox-fields?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(radioCheckboxField.getId().intValue())))
                .andExpect(jsonPath("$.[*].radioCheckbox").value(hasItem(DEFAULT_RADIO_CHECKBOX.booleanValue())));
    }

    @Test
    @Transactional
    public void getRadioCheckboxField() throws Exception {
        // Initialize the database
        radioCheckboxFieldRepository.saveAndFlush(radioCheckboxField);

        // Get the radioCheckboxField
        restRadioCheckboxFieldMockMvc.perform(get("/api/radio-checkbox-fields/{id}", radioCheckboxField.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(radioCheckboxField.getId().intValue()))
            .andExpect(jsonPath("$.radioCheckbox").value(DEFAULT_RADIO_CHECKBOX.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingRadioCheckboxField() throws Exception {
        // Get the radioCheckboxField
        restRadioCheckboxFieldMockMvc.perform(get("/api/radio-checkbox-fields/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRadioCheckboxField() throws Exception {
        // Initialize the database
        radioCheckboxFieldRepository.saveAndFlush(radioCheckboxField);
        radioCheckboxFieldSearchRepository.save(radioCheckboxField);
        int databaseSizeBeforeUpdate = radioCheckboxFieldRepository.findAll().size();

        // Update the radioCheckboxField
        RadioCheckboxField updatedRadioCheckboxField = new RadioCheckboxField();
        updatedRadioCheckboxField.setId(radioCheckboxField.getId());
        updatedRadioCheckboxField.setRadioCheckbox(UPDATED_RADIO_CHECKBOX);

        restRadioCheckboxFieldMockMvc.perform(put("/api/radio-checkbox-fields")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedRadioCheckboxField)))
                .andExpect(status().isOk());

        // Validate the RadioCheckboxField in the database
        List<RadioCheckboxField> radioCheckboxFields = radioCheckboxFieldRepository.findAll();
        assertThat(radioCheckboxFields).hasSize(databaseSizeBeforeUpdate);
        RadioCheckboxField testRadioCheckboxField = radioCheckboxFields.get(radioCheckboxFields.size() - 1);
        assertThat(testRadioCheckboxField.isRadioCheckbox()).isEqualTo(UPDATED_RADIO_CHECKBOX);

        // Validate the RadioCheckboxField in ElasticSearch
        RadioCheckboxField radioCheckboxFieldEs = radioCheckboxFieldSearchRepository.findOne(testRadioCheckboxField.getId());
        assertThat(radioCheckboxFieldEs).isEqualToComparingFieldByField(testRadioCheckboxField);
    }

    @Test
    @Transactional
    public void deleteRadioCheckboxField() throws Exception {
        // Initialize the database
        radioCheckboxFieldRepository.saveAndFlush(radioCheckboxField);
        radioCheckboxFieldSearchRepository.save(radioCheckboxField);
        int databaseSizeBeforeDelete = radioCheckboxFieldRepository.findAll().size();

        // Get the radioCheckboxField
        restRadioCheckboxFieldMockMvc.perform(delete("/api/radio-checkbox-fields/{id}", radioCheckboxField.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean radioCheckboxFieldExistsInEs = radioCheckboxFieldSearchRepository.exists(radioCheckboxField.getId());
        assertThat(radioCheckboxFieldExistsInEs).isFalse();

        // Validate the database is empty
        List<RadioCheckboxField> radioCheckboxFields = radioCheckboxFieldRepository.findAll();
        assertThat(radioCheckboxFields).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchRadioCheckboxField() throws Exception {
        // Initialize the database
        radioCheckboxFieldRepository.saveAndFlush(radioCheckboxField);
        radioCheckboxFieldSearchRepository.save(radioCheckboxField);

        // Search the radioCheckboxField
        restRadioCheckboxFieldMockMvc.perform(get("/api/_search/radio-checkbox-fields?query=id:" + radioCheckboxField.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(radioCheckboxField.getId().intValue())))
            .andExpect(jsonPath("$.[*].radioCheckbox").value(hasItem(DEFAULT_RADIO_CHECKBOX.booleanValue())));
    }
}
