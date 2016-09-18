package net.testaholic.acme.web.rest;

import net.testaholic.acme.AcmeSiteApp;
import net.testaholic.acme.domain.PasswordInputField;
import net.testaholic.acme.repository.PasswordInputFieldRepository;
import net.testaholic.acme.repository.search.PasswordInputFieldSearchRepository;

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
 * Test class for the PasswordInputFieldResource REST controller.
 *
 * @see PasswordInputFieldResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AcmeSiteApp.class)
@WebAppConfiguration
@IntegrationTest
public class PasswordInputFieldResourceIntTest {

    private static final String DEFAULT_PASSWORD_FIELD = "AAAAAAAA";
    private static final String UPDATED_PASSWORD_FIELD = "BBBBBBBB";

    @Inject
    private PasswordInputFieldRepository passwordInputFieldRepository;

    @Inject
    private PasswordInputFieldSearchRepository passwordInputFieldSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restPasswordInputFieldMockMvc;

    private PasswordInputField passwordInputField;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PasswordInputFieldResource passwordInputFieldResource = new PasswordInputFieldResource();
        ReflectionTestUtils.setField(passwordInputFieldResource, "passwordInputFieldSearchRepository", passwordInputFieldSearchRepository);
        ReflectionTestUtils.setField(passwordInputFieldResource, "passwordInputFieldRepository", passwordInputFieldRepository);
        this.restPasswordInputFieldMockMvc = MockMvcBuilders.standaloneSetup(passwordInputFieldResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        passwordInputFieldSearchRepository.deleteAll();
        passwordInputField = new PasswordInputField();
        passwordInputField.setPasswordField(DEFAULT_PASSWORD_FIELD);
    }

    @Test
    @Transactional
    public void createPasswordInputField() throws Exception {
        int databaseSizeBeforeCreate = passwordInputFieldRepository.findAll().size();

        // Create the PasswordInputField

        restPasswordInputFieldMockMvc.perform(post("/api/password-input-fields")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(passwordInputField)))
                .andExpect(status().isCreated());

        // Validate the PasswordInputField in the database
        List<PasswordInputField> passwordInputFields = passwordInputFieldRepository.findAll();
        assertThat(passwordInputFields).hasSize(databaseSizeBeforeCreate + 1);
        PasswordInputField testPasswordInputField = passwordInputFields.get(passwordInputFields.size() - 1);
        assertThat(testPasswordInputField.getPasswordField()).isEqualTo(DEFAULT_PASSWORD_FIELD);

        // Validate the PasswordInputField in ElasticSearch
        PasswordInputField passwordInputFieldEs = passwordInputFieldSearchRepository.findOne(testPasswordInputField.getId());
        assertThat(passwordInputFieldEs).isEqualToComparingFieldByField(testPasswordInputField);
    }

    @Test
    @Transactional
    public void checkPasswordFieldIsRequired() throws Exception {
        int databaseSizeBeforeTest = passwordInputFieldRepository.findAll().size();
        // set the field null
        passwordInputField.setPasswordField(null);

        // Create the PasswordInputField, which fails.

        restPasswordInputFieldMockMvc.perform(post("/api/password-input-fields")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(passwordInputField)))
                .andExpect(status().isBadRequest());

        List<PasswordInputField> passwordInputFields = passwordInputFieldRepository.findAll();
        assertThat(passwordInputFields).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPasswordInputFields() throws Exception {
        // Initialize the database
        passwordInputFieldRepository.saveAndFlush(passwordInputField);

        // Get all the passwordInputFields
        restPasswordInputFieldMockMvc.perform(get("/api/password-input-fields?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(passwordInputField.getId().intValue())))
                .andExpect(jsonPath("$.[*].passwordField").value(hasItem(DEFAULT_PASSWORD_FIELD.toString())));
    }

    @Test
    @Transactional
    public void getPasswordInputField() throws Exception {
        // Initialize the database
        passwordInputFieldRepository.saveAndFlush(passwordInputField);

        // Get the passwordInputField
        restPasswordInputFieldMockMvc.perform(get("/api/password-input-fields/{id}", passwordInputField.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(passwordInputField.getId().intValue()))
            .andExpect(jsonPath("$.passwordField").value(DEFAULT_PASSWORD_FIELD.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPasswordInputField() throws Exception {
        // Get the passwordInputField
        restPasswordInputFieldMockMvc.perform(get("/api/password-input-fields/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePasswordInputField() throws Exception {
        // Initialize the database
        passwordInputFieldRepository.saveAndFlush(passwordInputField);
        passwordInputFieldSearchRepository.save(passwordInputField);
        int databaseSizeBeforeUpdate = passwordInputFieldRepository.findAll().size();

        // Update the passwordInputField
        PasswordInputField updatedPasswordInputField = new PasswordInputField();
        updatedPasswordInputField.setId(passwordInputField.getId());
        updatedPasswordInputField.setPasswordField(UPDATED_PASSWORD_FIELD);

        restPasswordInputFieldMockMvc.perform(put("/api/password-input-fields")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedPasswordInputField)))
                .andExpect(status().isOk());

        // Validate the PasswordInputField in the database
        List<PasswordInputField> passwordInputFields = passwordInputFieldRepository.findAll();
        assertThat(passwordInputFields).hasSize(databaseSizeBeforeUpdate);
        PasswordInputField testPasswordInputField = passwordInputFields.get(passwordInputFields.size() - 1);
        assertThat(testPasswordInputField.getPasswordField()).isEqualTo(UPDATED_PASSWORD_FIELD);

        // Validate the PasswordInputField in ElasticSearch
        PasswordInputField passwordInputFieldEs = passwordInputFieldSearchRepository.findOne(testPasswordInputField.getId());
        assertThat(passwordInputFieldEs).isEqualToComparingFieldByField(testPasswordInputField);
    }

    @Test
    @Transactional
    public void deletePasswordInputField() throws Exception {
        // Initialize the database
        passwordInputFieldRepository.saveAndFlush(passwordInputField);
        passwordInputFieldSearchRepository.save(passwordInputField);
        int databaseSizeBeforeDelete = passwordInputFieldRepository.findAll().size();

        // Get the passwordInputField
        restPasswordInputFieldMockMvc.perform(delete("/api/password-input-fields/{id}", passwordInputField.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean passwordInputFieldExistsInEs = passwordInputFieldSearchRepository.exists(passwordInputField.getId());
        assertThat(passwordInputFieldExistsInEs).isFalse();

        // Validate the database is empty
        List<PasswordInputField> passwordInputFields = passwordInputFieldRepository.findAll();
        assertThat(passwordInputFields).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPasswordInputField() throws Exception {
        // Initialize the database
        passwordInputFieldRepository.saveAndFlush(passwordInputField);
        passwordInputFieldSearchRepository.save(passwordInputField);

        // Search the passwordInputField
        restPasswordInputFieldMockMvc.perform(get("/api/_search/password-input-fields?query=id:" + passwordInputField.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(passwordInputField.getId().intValue())))
            .andExpect(jsonPath("$.[*].passwordField").value(hasItem(DEFAULT_PASSWORD_FIELD.toString())));
    }
}
