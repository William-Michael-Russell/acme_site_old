package net.testaholic.acme.web.rest;

import net.testaholic.acme.AcmeSiteApp;
import net.testaholic.acme.domain.AlertPrompt;
import net.testaholic.acme.repository.AlertPromptRepository;
import net.testaholic.acme.repository.search.AlertPromptSearchRepository;

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
 * Test class for the AlertPromptResource REST controller.
 *
 * @see AlertPromptResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AcmeSiteApp.class)
@WebAppConfiguration
@IntegrationTest
public class AlertPromptResourceIntTest {

    private static final String DEFAULT_ALERT_NAME = "AAAAA";
    private static final String UPDATED_ALERT_NAME = "BBBBB";

    @Inject
    private AlertPromptRepository alertPromptRepository;

    @Inject
    private AlertPromptSearchRepository alertPromptSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restAlertPromptMockMvc;

    private AlertPrompt alertPrompt;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AlertPromptResource alertPromptResource = new AlertPromptResource();
        ReflectionTestUtils.setField(alertPromptResource, "alertPromptSearchRepository", alertPromptSearchRepository);
        ReflectionTestUtils.setField(alertPromptResource, "alertPromptRepository", alertPromptRepository);
        this.restAlertPromptMockMvc = MockMvcBuilders.standaloneSetup(alertPromptResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        alertPromptSearchRepository.deleteAll();
        alertPrompt = new AlertPrompt();
        alertPrompt.setAlertName(DEFAULT_ALERT_NAME);
    }

    @Test
    @Transactional
    public void createAlertPrompt() throws Exception {
        int databaseSizeBeforeCreate = alertPromptRepository.findAll().size();

        // Create the AlertPrompt

        restAlertPromptMockMvc.perform(post("/api/alert-prompts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(alertPrompt)))
                .andExpect(status().isCreated());

        // Validate the AlertPrompt in the database
        List<AlertPrompt> alertPrompts = alertPromptRepository.findAll();
        assertThat(alertPrompts).hasSize(databaseSizeBeforeCreate + 1);
        AlertPrompt testAlertPrompt = alertPrompts.get(alertPrompts.size() - 1);
        assertThat(testAlertPrompt.getAlertName()).isEqualTo(DEFAULT_ALERT_NAME);

        // Validate the AlertPrompt in ElasticSearch
        AlertPrompt alertPromptEs = alertPromptSearchRepository.findOne(testAlertPrompt.getId());
        assertThat(alertPromptEs).isEqualToComparingFieldByField(testAlertPrompt);
    }

    @Test
    @Transactional
    public void checkAlertNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = alertPromptRepository.findAll().size();
        // set the field null
        alertPrompt.setAlertName(null);

        // Create the AlertPrompt, which fails.

        restAlertPromptMockMvc.perform(post("/api/alert-prompts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(alertPrompt)))
                .andExpect(status().isBadRequest());

        List<AlertPrompt> alertPrompts = alertPromptRepository.findAll();
        assertThat(alertPrompts).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAlertPrompts() throws Exception {
        // Initialize the database
        alertPromptRepository.saveAndFlush(alertPrompt);

        // Get all the alertPrompts
        restAlertPromptMockMvc.perform(get("/api/alert-prompts?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(alertPrompt.getId().intValue())))
                .andExpect(jsonPath("$.[*].alertName").value(hasItem(DEFAULT_ALERT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getAlertPrompt() throws Exception {
        // Initialize the database
        alertPromptRepository.saveAndFlush(alertPrompt);

        // Get the alertPrompt
        restAlertPromptMockMvc.perform(get("/api/alert-prompts/{id}", alertPrompt.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(alertPrompt.getId().intValue()))
            .andExpect(jsonPath("$.alertName").value(DEFAULT_ALERT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAlertPrompt() throws Exception {
        // Get the alertPrompt
        restAlertPromptMockMvc.perform(get("/api/alert-prompts/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAlertPrompt() throws Exception {
        // Initialize the database
        alertPromptRepository.saveAndFlush(alertPrompt);
        alertPromptSearchRepository.save(alertPrompt);
        int databaseSizeBeforeUpdate = alertPromptRepository.findAll().size();

        // Update the alertPrompt
        AlertPrompt updatedAlertPrompt = new AlertPrompt();
        updatedAlertPrompt.setId(alertPrompt.getId());
        updatedAlertPrompt.setAlertName(UPDATED_ALERT_NAME);

        restAlertPromptMockMvc.perform(put("/api/alert-prompts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedAlertPrompt)))
                .andExpect(status().isOk());

        // Validate the AlertPrompt in the database
        List<AlertPrompt> alertPrompts = alertPromptRepository.findAll();
        assertThat(alertPrompts).hasSize(databaseSizeBeforeUpdate);
        AlertPrompt testAlertPrompt = alertPrompts.get(alertPrompts.size() - 1);
        assertThat(testAlertPrompt.getAlertName()).isEqualTo(UPDATED_ALERT_NAME);

        // Validate the AlertPrompt in ElasticSearch
        AlertPrompt alertPromptEs = alertPromptSearchRepository.findOne(testAlertPrompt.getId());
        assertThat(alertPromptEs).isEqualToComparingFieldByField(testAlertPrompt);
    }

    @Test
    @Transactional
    public void deleteAlertPrompt() throws Exception {
        // Initialize the database
        alertPromptRepository.saveAndFlush(alertPrompt);
        alertPromptSearchRepository.save(alertPrompt);
        int databaseSizeBeforeDelete = alertPromptRepository.findAll().size();

        // Get the alertPrompt
        restAlertPromptMockMvc.perform(delete("/api/alert-prompts/{id}", alertPrompt.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean alertPromptExistsInEs = alertPromptSearchRepository.exists(alertPrompt.getId());
        assertThat(alertPromptExistsInEs).isFalse();

        // Validate the database is empty
        List<AlertPrompt> alertPrompts = alertPromptRepository.findAll();
        assertThat(alertPrompts).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchAlertPrompt() throws Exception {
        // Initialize the database
        alertPromptRepository.saveAndFlush(alertPrompt);
        alertPromptSearchRepository.save(alertPrompt);

        // Search the alertPrompt
        restAlertPromptMockMvc.perform(get("/api/_search/alert-prompts?query=id:" + alertPrompt.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(alertPrompt.getId().intValue())))
            .andExpect(jsonPath("$.[*].alertName").value(hasItem(DEFAULT_ALERT_NAME.toString())));
    }
}
