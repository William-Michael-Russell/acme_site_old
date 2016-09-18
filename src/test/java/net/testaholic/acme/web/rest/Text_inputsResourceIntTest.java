package net.testaholic.acme.web.rest;

import net.testaholic.acme.AcmeSiteApp;
import net.testaholic.acme.domain.Text_inputs;
import net.testaholic.acme.repository.Text_inputsRepository;
import net.testaholic.acme.repository.search.Text_inputsSearchRepository;

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
 * Test class for the Text_inputsResource REST controller.
 *
 * @see Text_inputsResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AcmeSiteApp.class)
@WebAppConfiguration
@IntegrationTest
public class Text_inputsResourceIntTest {


    @Inject
    private Text_inputsRepository text_inputsRepository;

    @Inject
    private Text_inputsSearchRepository text_inputsSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restText_inputsMockMvc;

    private Text_inputs text_inputs;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Text_inputsResource text_inputsResource = new Text_inputsResource();
        ReflectionTestUtils.setField(text_inputsResource, "text_inputsSearchRepository", text_inputsSearchRepository);
        ReflectionTestUtils.setField(text_inputsResource, "text_inputsRepository", text_inputsRepository);
        this.restText_inputsMockMvc = MockMvcBuilders.standaloneSetup(text_inputsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        text_inputsSearchRepository.deleteAll();
        text_inputs = new Text_inputs();
    }

    @Test
    @Transactional
    public void createText_inputs() throws Exception {
        int databaseSizeBeforeCreate = text_inputsRepository.findAll().size();

        // Create the Text_inputs

        restText_inputsMockMvc.perform(post("/api/text-inputs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(text_inputs)))
                .andExpect(status().isCreated());

        // Validate the Text_inputs in the database
        List<Text_inputs> text_inputs = text_inputsRepository.findAll();
        assertThat(text_inputs).hasSize(databaseSizeBeforeCreate + 1);
        Text_inputs testText_inputs = text_inputs.get(text_inputs.size() - 1);

        // Validate the Text_inputs in ElasticSearch
        Text_inputs text_inputsEs = text_inputsSearchRepository.findOne(testText_inputs.getId());
        assertThat(text_inputsEs).isEqualToComparingFieldByField(testText_inputs);
    }

    @Test
    @Transactional
    public void getAllText_inputs() throws Exception {
        // Initialize the database
        text_inputsRepository.saveAndFlush(text_inputs);

        // Get all the text_inputs
        restText_inputsMockMvc.perform(get("/api/text-inputs?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(text_inputs.getId().intValue())));
    }

    @Test
    @Transactional
    public void getText_inputs() throws Exception {
        // Initialize the database
        text_inputsRepository.saveAndFlush(text_inputs);

        // Get the text_inputs
        restText_inputsMockMvc.perform(get("/api/text-inputs/{id}", text_inputs.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(text_inputs.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingText_inputs() throws Exception {
        // Get the text_inputs
        restText_inputsMockMvc.perform(get("/api/text-inputs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateText_inputs() throws Exception {
        // Initialize the database
        text_inputsRepository.saveAndFlush(text_inputs);
        text_inputsSearchRepository.save(text_inputs);
        int databaseSizeBeforeUpdate = text_inputsRepository.findAll().size();

        // Update the text_inputs
        Text_inputs updatedText_inputs = new Text_inputs();
        updatedText_inputs.setId(text_inputs.getId());

        restText_inputsMockMvc.perform(put("/api/text-inputs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedText_inputs)))
                .andExpect(status().isOk());

        // Validate the Text_inputs in the database
        List<Text_inputs> text_inputs = text_inputsRepository.findAll();
        assertThat(text_inputs).hasSize(databaseSizeBeforeUpdate);
        Text_inputs testText_inputs = text_inputs.get(text_inputs.size() - 1);

        // Validate the Text_inputs in ElasticSearch
        Text_inputs text_inputsEs = text_inputsSearchRepository.findOne(testText_inputs.getId());
        assertThat(text_inputsEs).isEqualToComparingFieldByField(testText_inputs);
    }

    @Test
    @Transactional
    public void deleteText_inputs() throws Exception {
        // Initialize the database
        text_inputsRepository.saveAndFlush(text_inputs);
        text_inputsSearchRepository.save(text_inputs);
        int databaseSizeBeforeDelete = text_inputsRepository.findAll().size();

        // Get the text_inputs
        restText_inputsMockMvc.perform(delete("/api/text-inputs/{id}", text_inputs.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean text_inputsExistsInEs = text_inputsSearchRepository.exists(text_inputs.getId());
        assertThat(text_inputsExistsInEs).isFalse();

        // Validate the database is empty
        List<Text_inputs> text_inputs = text_inputsRepository.findAll();
        assertThat(text_inputs).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchText_inputs() throws Exception {
        // Initialize the database
        text_inputsRepository.saveAndFlush(text_inputs);
        text_inputsSearchRepository.save(text_inputs);

        // Search the text_inputs
        restText_inputsMockMvc.perform(get("/api/_search/text-inputs?query=id:" + text_inputs.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(text_inputs.getId().intValue())));
    }
}
