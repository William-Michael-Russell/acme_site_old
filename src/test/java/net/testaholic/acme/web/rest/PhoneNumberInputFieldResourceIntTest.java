package net.testaholic.acme.web.rest;

import net.testaholic.acme.AcmeSiteApp;
import net.testaholic.acme.domain.PhoneNumberInputField;
import net.testaholic.acme.repository.PhoneNumberInputFieldRepository;
import net.testaholic.acme.repository.search.PhoneNumberInputFieldSearchRepository;

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
 * Test class for the PhoneNumberInputFieldResource REST controller.
 *
 * @see PhoneNumberInputFieldResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AcmeSiteApp.class)
@WebAppConfiguration
@IntegrationTest
public class PhoneNumberInputFieldResourceIntTest {

    private static final String DEFAULT_PHONE_NUMBER_FIELD = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER_FIELD = "BBBBBBBBBB";

    @Inject
    private PhoneNumberInputFieldRepository phoneNumberInputFieldRepository;

    @Inject
    private PhoneNumberInputFieldSearchRepository phoneNumberInputFieldSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restPhoneNumberInputFieldMockMvc;

    private PhoneNumberInputField phoneNumberInputField;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PhoneNumberInputFieldResource phoneNumberInputFieldResource = new PhoneNumberInputFieldResource();
        ReflectionTestUtils.setField(phoneNumberInputFieldResource, "phoneNumberInputFieldSearchRepository", phoneNumberInputFieldSearchRepository);
        ReflectionTestUtils.setField(phoneNumberInputFieldResource, "phoneNumberInputFieldRepository", phoneNumberInputFieldRepository);
        this.restPhoneNumberInputFieldMockMvc = MockMvcBuilders.standaloneSetup(phoneNumberInputFieldResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        phoneNumberInputFieldSearchRepository.deleteAll();
        phoneNumberInputField = new PhoneNumberInputField();
        phoneNumberInputField.setPhoneNumberField(DEFAULT_PHONE_NUMBER_FIELD);
    }

    @Test
    @Transactional
    public void createPhoneNumberInputField() throws Exception {
        int databaseSizeBeforeCreate = phoneNumberInputFieldRepository.findAll().size();

        // Create the PhoneNumberInputField

        restPhoneNumberInputFieldMockMvc.perform(post("/api/phone-number-input-fields")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(phoneNumberInputField)))
                .andExpect(status().isCreated());

        // Validate the PhoneNumberInputField in the database
        List<PhoneNumberInputField> phoneNumberInputFields = phoneNumberInputFieldRepository.findAll();
        assertThat(phoneNumberInputFields).hasSize(databaseSizeBeforeCreate + 1);
        PhoneNumberInputField testPhoneNumberInputField = phoneNumberInputFields.get(phoneNumberInputFields.size() - 1);
        assertThat(testPhoneNumberInputField.getPhoneNumberField()).isEqualTo(DEFAULT_PHONE_NUMBER_FIELD);

        // Validate the PhoneNumberInputField in ElasticSearch
        PhoneNumberInputField phoneNumberInputFieldEs = phoneNumberInputFieldSearchRepository.findOne(testPhoneNumberInputField.getId());
        assertThat(phoneNumberInputFieldEs).isEqualToComparingFieldByField(testPhoneNumberInputField);
    }

    @Test
    @Transactional
    public void checkPhoneNumberFieldIsRequired() throws Exception {
        int databaseSizeBeforeTest = phoneNumberInputFieldRepository.findAll().size();
        // set the field null
        phoneNumberInputField.setPhoneNumberField(null);

        // Create the PhoneNumberInputField, which fails.

        restPhoneNumberInputFieldMockMvc.perform(post("/api/phone-number-input-fields")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(phoneNumberInputField)))
                .andExpect(status().isBadRequest());

        List<PhoneNumberInputField> phoneNumberInputFields = phoneNumberInputFieldRepository.findAll();
        assertThat(phoneNumberInputFields).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPhoneNumberInputFields() throws Exception {
        // Initialize the database
        phoneNumberInputFieldRepository.saveAndFlush(phoneNumberInputField);

        // Get all the phoneNumberInputFields
        restPhoneNumberInputFieldMockMvc.perform(get("/api/phone-number-input-fields?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(phoneNumberInputField.getId().intValue())))
                .andExpect(jsonPath("$.[*].phoneNumberField").value(hasItem(DEFAULT_PHONE_NUMBER_FIELD.toString())));
    }

    @Test
    @Transactional
    public void getPhoneNumberInputField() throws Exception {
        // Initialize the database
        phoneNumberInputFieldRepository.saveAndFlush(phoneNumberInputField);

        // Get the phoneNumberInputField
        restPhoneNumberInputFieldMockMvc.perform(get("/api/phone-number-input-fields/{id}", phoneNumberInputField.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(phoneNumberInputField.getId().intValue()))
            .andExpect(jsonPath("$.phoneNumberField").value(DEFAULT_PHONE_NUMBER_FIELD.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPhoneNumberInputField() throws Exception {
        // Get the phoneNumberInputField
        restPhoneNumberInputFieldMockMvc.perform(get("/api/phone-number-input-fields/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePhoneNumberInputField() throws Exception {
        // Initialize the database
        phoneNumberInputFieldRepository.saveAndFlush(phoneNumberInputField);
        phoneNumberInputFieldSearchRepository.save(phoneNumberInputField);
        int databaseSizeBeforeUpdate = phoneNumberInputFieldRepository.findAll().size();

        // Update the phoneNumberInputField
        PhoneNumberInputField updatedPhoneNumberInputField = new PhoneNumberInputField();
        updatedPhoneNumberInputField.setId(phoneNumberInputField.getId());
        updatedPhoneNumberInputField.setPhoneNumberField(UPDATED_PHONE_NUMBER_FIELD);

        restPhoneNumberInputFieldMockMvc.perform(put("/api/phone-number-input-fields")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedPhoneNumberInputField)))
                .andExpect(status().isOk());

        // Validate the PhoneNumberInputField in the database
        List<PhoneNumberInputField> phoneNumberInputFields = phoneNumberInputFieldRepository.findAll();
        assertThat(phoneNumberInputFields).hasSize(databaseSizeBeforeUpdate);
        PhoneNumberInputField testPhoneNumberInputField = phoneNumberInputFields.get(phoneNumberInputFields.size() - 1);
        assertThat(testPhoneNumberInputField.getPhoneNumberField()).isEqualTo(UPDATED_PHONE_NUMBER_FIELD);

        // Validate the PhoneNumberInputField in ElasticSearch
        PhoneNumberInputField phoneNumberInputFieldEs = phoneNumberInputFieldSearchRepository.findOne(testPhoneNumberInputField.getId());
        assertThat(phoneNumberInputFieldEs).isEqualToComparingFieldByField(testPhoneNumberInputField);
    }

    @Test
    @Transactional
    public void deletePhoneNumberInputField() throws Exception {
        // Initialize the database
        phoneNumberInputFieldRepository.saveAndFlush(phoneNumberInputField);
        phoneNumberInputFieldSearchRepository.save(phoneNumberInputField);
        int databaseSizeBeforeDelete = phoneNumberInputFieldRepository.findAll().size();

        // Get the phoneNumberInputField
        restPhoneNumberInputFieldMockMvc.perform(delete("/api/phone-number-input-fields/{id}", phoneNumberInputField.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean phoneNumberInputFieldExistsInEs = phoneNumberInputFieldSearchRepository.exists(phoneNumberInputField.getId());
        assertThat(phoneNumberInputFieldExistsInEs).isFalse();

        // Validate the database is empty
        List<PhoneNumberInputField> phoneNumberInputFields = phoneNumberInputFieldRepository.findAll();
        assertThat(phoneNumberInputFields).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPhoneNumberInputField() throws Exception {
        // Initialize the database
        phoneNumberInputFieldRepository.saveAndFlush(phoneNumberInputField);
        phoneNumberInputFieldSearchRepository.save(phoneNumberInputField);

        // Search the phoneNumberInputField
        restPhoneNumberInputFieldMockMvc.perform(get("/api/_search/phone-number-input-fields?query=id:" + phoneNumberInputField.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(phoneNumberInputField.getId().intValue())))
            .andExpect(jsonPath("$.[*].phoneNumberField").value(hasItem(DEFAULT_PHONE_NUMBER_FIELD.toString())));
    }
}
