package net.testaholic.acme.web.rest;

import net.testaholic.acme.AcmeSiteApp;
import net.testaholic.acme.domain.AudioUpload;
import net.testaholic.acme.repository.AudioUploadRepository;
import net.testaholic.acme.repository.search.AudioUploadSearchRepository;

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
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the AudioUploadResource REST controller.
 *
 * @see AudioUploadResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AcmeSiteApp.class)
@WebAppConfiguration
@IntegrationTest
public class AudioUploadResourceIntTest {


    private static final byte[] DEFAULT_AUDIO_FIELD = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_AUDIO_FIELD = TestUtil.createByteArray(5000000, "1");
    private static final String DEFAULT_AUDIO_FIELD_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_AUDIO_FIELD_CONTENT_TYPE = "image/png";

    @Inject
    private AudioUploadRepository audioUploadRepository;

    @Inject
    private AudioUploadSearchRepository audioUploadSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restAudioUploadMockMvc;

    private AudioUpload audioUpload;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AudioUploadResource audioUploadResource = new AudioUploadResource();
        ReflectionTestUtils.setField(audioUploadResource, "audioUploadSearchRepository", audioUploadSearchRepository);
        ReflectionTestUtils.setField(audioUploadResource, "audioUploadRepository", audioUploadRepository);
        this.restAudioUploadMockMvc = MockMvcBuilders.standaloneSetup(audioUploadResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        audioUploadSearchRepository.deleteAll();
        audioUpload = new AudioUpload();
        audioUpload.setAudioField(DEFAULT_AUDIO_FIELD);
        audioUpload.setAudioFieldContentType(DEFAULT_AUDIO_FIELD_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createAudioUpload() throws Exception {
        int databaseSizeBeforeCreate = audioUploadRepository.findAll().size();

        // Create the AudioUpload

        restAudioUploadMockMvc.perform(post("/api/audio-uploads")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(audioUpload)))
                .andExpect(status().isCreated());

        // Validate the AudioUpload in the database
        List<AudioUpload> audioUploads = audioUploadRepository.findAll();
        assertThat(audioUploads).hasSize(databaseSizeBeforeCreate + 1);
        AudioUpload testAudioUpload = audioUploads.get(audioUploads.size() - 1);
        assertThat(testAudioUpload.getAudioField()).isEqualTo(DEFAULT_AUDIO_FIELD);
        assertThat(testAudioUpload.getAudioFieldContentType()).isEqualTo(DEFAULT_AUDIO_FIELD_CONTENT_TYPE);

        // Validate the AudioUpload in ElasticSearch
        AudioUpload audioUploadEs = audioUploadSearchRepository.findOne(testAudioUpload.getId());
        assertThat(audioUploadEs).isEqualToComparingFieldByField(testAudioUpload);
    }

    @Test
    @Transactional
    public void checkAudioFieldIsRequired() throws Exception {
        int databaseSizeBeforeTest = audioUploadRepository.findAll().size();
        // set the field null
        audioUpload.setAudioField(null);

        // Create the AudioUpload, which fails.

        restAudioUploadMockMvc.perform(post("/api/audio-uploads")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(audioUpload)))
                .andExpect(status().isBadRequest());

        List<AudioUpload> audioUploads = audioUploadRepository.findAll();
        assertThat(audioUploads).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAudioUploads() throws Exception {
        // Initialize the database
        audioUploadRepository.saveAndFlush(audioUpload);

        // Get all the audioUploads
        restAudioUploadMockMvc.perform(get("/api/audio-uploads?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(audioUpload.getId().intValue())))
                .andExpect(jsonPath("$.[*].audioFieldContentType").value(hasItem(DEFAULT_AUDIO_FIELD_CONTENT_TYPE)))
                .andExpect(jsonPath("$.[*].audioField").value(hasItem(Base64Utils.encodeToString(DEFAULT_AUDIO_FIELD))));
    }

    @Test
    @Transactional
    public void getAudioUpload() throws Exception {
        // Initialize the database
        audioUploadRepository.saveAndFlush(audioUpload);

        // Get the audioUpload
        restAudioUploadMockMvc.perform(get("/api/audio-uploads/{id}", audioUpload.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(audioUpload.getId().intValue()))
            .andExpect(jsonPath("$.audioFieldContentType").value(DEFAULT_AUDIO_FIELD_CONTENT_TYPE))
            .andExpect(jsonPath("$.audioField").value(Base64Utils.encodeToString(DEFAULT_AUDIO_FIELD)));
    }

    @Test
    @Transactional
    public void getNonExistingAudioUpload() throws Exception {
        // Get the audioUpload
        restAudioUploadMockMvc.perform(get("/api/audio-uploads/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAudioUpload() throws Exception {
        // Initialize the database
        audioUploadRepository.saveAndFlush(audioUpload);
        audioUploadSearchRepository.save(audioUpload);
        int databaseSizeBeforeUpdate = audioUploadRepository.findAll().size();

        // Update the audioUpload
        AudioUpload updatedAudioUpload = new AudioUpload();
        updatedAudioUpload.setId(audioUpload.getId());
        updatedAudioUpload.setAudioField(UPDATED_AUDIO_FIELD);
        updatedAudioUpload.setAudioFieldContentType(UPDATED_AUDIO_FIELD_CONTENT_TYPE);

        restAudioUploadMockMvc.perform(put("/api/audio-uploads")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedAudioUpload)))
                .andExpect(status().isOk());

        // Validate the AudioUpload in the database
        List<AudioUpload> audioUploads = audioUploadRepository.findAll();
        assertThat(audioUploads).hasSize(databaseSizeBeforeUpdate);
        AudioUpload testAudioUpload = audioUploads.get(audioUploads.size() - 1);
        assertThat(testAudioUpload.getAudioField()).isEqualTo(UPDATED_AUDIO_FIELD);
        assertThat(testAudioUpload.getAudioFieldContentType()).isEqualTo(UPDATED_AUDIO_FIELD_CONTENT_TYPE);

        // Validate the AudioUpload in ElasticSearch
        AudioUpload audioUploadEs = audioUploadSearchRepository.findOne(testAudioUpload.getId());
        assertThat(audioUploadEs).isEqualToComparingFieldByField(testAudioUpload);
    }

    @Test
    @Transactional
    public void deleteAudioUpload() throws Exception {
        // Initialize the database
        audioUploadRepository.saveAndFlush(audioUpload);
        audioUploadSearchRepository.save(audioUpload);
        int databaseSizeBeforeDelete = audioUploadRepository.findAll().size();

        // Get the audioUpload
        restAudioUploadMockMvc.perform(delete("/api/audio-uploads/{id}", audioUpload.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean audioUploadExistsInEs = audioUploadSearchRepository.exists(audioUpload.getId());
        assertThat(audioUploadExistsInEs).isFalse();

        // Validate the database is empty
        List<AudioUpload> audioUploads = audioUploadRepository.findAll();
        assertThat(audioUploads).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchAudioUpload() throws Exception {
        // Initialize the database
        audioUploadRepository.saveAndFlush(audioUpload);
        audioUploadSearchRepository.save(audioUpload);

        // Search the audioUpload
        restAudioUploadMockMvc.perform(get("/api/_search/audio-uploads?query=id:" + audioUpload.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(audioUpload.getId().intValue())))
            .andExpect(jsonPath("$.[*].audioFieldContentType").value(hasItem(DEFAULT_AUDIO_FIELD_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].audioField").value(hasItem(Base64Utils.encodeToString(DEFAULT_AUDIO_FIELD))));
    }
}
