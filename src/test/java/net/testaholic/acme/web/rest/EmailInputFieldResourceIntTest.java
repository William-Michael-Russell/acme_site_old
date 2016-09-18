package net.testaholic.acme.web.rest;

import net.testaholic.acme.AcmeSiteApp;
import net.testaholic.acme.domain.EmailInputField;
import net.testaholic.acme.repository.EmailInputFieldRepository;
import net.testaholic.acme.repository.search.EmailInputFieldSearchRepository;

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
 * Test class for the EmailInputFieldResource REST controller.
 *
 * @see EmailInputFieldResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AcmeSiteApp.class)
@WebAppConfiguration
@IntegrationTest
public class EmailInputFieldResourceIntTest {

    private static final String DEFAULT_EMAIL_FIELD = "AAAAAA";
    private static final String UPDATED_EMAIL_FIELD = "BBBBBB";

    @Inject
    private EmailInputFieldRepository emailInputFieldRepository;

    @Inject
    private EmailInputFieldSearchRepository emailInputFieldSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restEmailInputFieldMockMvc;

    private EmailInputField emailInputField;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        EmailInputFieldResource emailInputFieldResource = new EmailInputFieldResource();
        ReflectionTestUtils.setField(emailInputFieldResource, "emailInputFieldSearchRepository", emailInputFieldSearchRepository);
        ReflectionTestUtils.setField(emailInputFieldResource, "emailInputFieldRepository", emailInputFieldRepository);
        this.restEmailInputFieldMockMvc = MockMvcBuilders.standaloneSetup(emailInputFieldResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        emailInputFieldSearchRepository.deleteAll();
        emailInputField = new EmailInputField();
        emailInputField.setEmailField(DEFAULT_EMAIL_FIELD);
    }

    @Test
    @Transactional
    public void createEmailInputField() throws Exception {
        int databaseSizeBeforeCreate = emailInputFieldRepository.findAll().size();

        // Create the EmailInputField

        restEmailInputFieldMockMvc.perform(post("/api/email-input-fields")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(emailInputField)))
                .andExpect(status().isCreated());

        // Validate the EmailInputField in the database
        List<EmailInputField> emailInputFields = emailInputFieldRepository.findAll();
        assertThat(emailInputFields).hasSize(databaseSizeBeforeCreate + 1);
        EmailInputField testEmailInputField = emailInputFields.get(emailInputFields.size() - 1);
        assertThat(testEmailInputField.getEmailField()).isEqualTo(DEFAULT_EMAIL_FIELD);

        // Validate the EmailInputField in ElasticSearch
        EmailInputField emailInputFieldEs = emailInputFieldSearchRepository.findOne(testEmailInputField.getId());
        assertThat(emailInputFieldEs).isEqualToComparingFieldByField(testEmailInputField);
    }

    @Test
    @Transactional
    public void checkEmailFieldIsRequired() throws Exception {
        int databaseSizeBeforeTest = emailInputFieldRepository.findAll().size();
        // set the field null
        emailInputField.setEmailField(null);

        // Create the EmailInputField, which fails.

        restEmailInputFieldMockMvc.perform(post("/api/email-input-fields")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(emailInputField)))
                .andExpect(status().isBadRequest());

        List<EmailInputField> emailInputFields = emailInputFieldRepository.findAll();
        assertThat(emailInputFields).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllEmailInputFields() throws Exception {
        // Initialize the database
        emailInputFieldRepository.saveAndFlush(emailInputField);

        // Get all the emailInputFields
        restEmailInputFieldMockMvc.perform(get("/api/email-input-fields?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(emailInputField.getId().intValue())))
                .andExpect(jsonPath("$.[*].emailField").value(hasItem(DEFAULT_EMAIL_FIELD.toString())));
    }

    @Test
    @Transactional
    public void getEmailInputField() throws Exception {
        // Initialize the database
        emailInputFieldRepository.saveAndFlush(emailInputField);

        // Get the emailInputField
        restEmailInputFieldMockMvc.perform(get("/api/email-input-fields/{id}", emailInputField.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(emailInputField.getId().intValue()))
            .andExpect(jsonPath("$.emailField").value(DEFAULT_EMAIL_FIELD.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingEmailInputField() throws Exception {
        // Get the emailInputField
        restEmailInputFieldMockMvc.perform(get("/api/email-input-fields/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEmailInputField() throws Exception {
        // Initialize the database
        emailInputFieldRepository.saveAndFlush(emailInputField);
        emailInputFieldSearchRepository.save(emailInputField);
        int databaseSizeBeforeUpdate = emailInputFieldRepository.findAll().size();

        // Update the emailInputField
        EmailInputField updatedEmailInputField = new EmailInputField();
        updatedEmailInputField.setId(emailInputField.getId());
        updatedEmailInputField.setEmailField(UPDATED_EMAIL_FIELD);

        restEmailInputFieldMockMvc.perform(put("/api/email-input-fields")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedEmailInputField)))
                .andExpect(status().isOk());

        // Validate the EmailInputField in the database
        List<EmailInputField> emailInputFields = emailInputFieldRepository.findAll();
        assertThat(emailInputFields).hasSize(databaseSizeBeforeUpdate);
        EmailInputField testEmailInputField = emailInputFields.get(emailInputFields.size() - 1);
        assertThat(testEmailInputField.getEmailField()).isEqualTo(UPDATED_EMAIL_FIELD);

        // Validate the EmailInputField in ElasticSearch
        EmailInputField emailInputFieldEs = emailInputFieldSearchRepository.findOne(testEmailInputField.getId());
        assertThat(emailInputFieldEs).isEqualToComparingFieldByField(testEmailInputField);
    }

    @Test
    @Transactional
    public void deleteEmailInputField() throws Exception {
        // Initialize the database
        emailInputFieldRepository.saveAndFlush(emailInputField);
        emailInputFieldSearchRepository.save(emailInputField);
        int databaseSizeBeforeDelete = emailInputFieldRepository.findAll().size();

        // Get the emailInputField
        restEmailInputFieldMockMvc.perform(delete("/api/email-input-fields/{id}", emailInputField.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean emailInputFieldExistsInEs = emailInputFieldSearchRepository.exists(emailInputField.getId());
        assertThat(emailInputFieldExistsInEs).isFalse();

        // Validate the database is empty
        List<EmailInputField> emailInputFields = emailInputFieldRepository.findAll();
        assertThat(emailInputFields).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchEmailInputField() throws Exception {
        // Initialize the database
        emailInputFieldRepository.saveAndFlush(emailInputField);
        emailInputFieldSearchRepository.save(emailInputField);

        // Search the emailInputField
        restEmailInputFieldMockMvc.perform(get("/api/_search/email-input-fields?query=id:" + emailInputField.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(emailInputField.getId().intValue())))
            .andExpect(jsonPath("$.[*].emailField").value(hasItem(DEFAULT_EMAIL_FIELD.toString())));
    }
}
