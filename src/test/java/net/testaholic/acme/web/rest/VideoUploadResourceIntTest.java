package net.testaholic.acme.web.rest;

import net.testaholic.acme.AcmeSiteApp;
import net.testaholic.acme.domain.VideoUpload;
import net.testaholic.acme.repository.VideoUploadRepository;
import net.testaholic.acme.repository.search.VideoUploadSearchRepository;

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
 * Test class for the VideoUploadResource REST controller.
 *
 * @see VideoUploadResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AcmeSiteApp.class)
@WebAppConfiguration
@IntegrationTest
public class VideoUploadResourceIntTest {


    private static final byte[] DEFAULT_VIDEO_FIELD = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_VIDEO_FIELD = TestUtil.createByteArray(5000000, "1");
    private static final String DEFAULT_VIDEO_FIELD_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_VIDEO_FIELD_CONTENT_TYPE = "image/png";

    @Inject
    private VideoUploadRepository videoUploadRepository;

    @Inject
    private VideoUploadSearchRepository videoUploadSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restVideoUploadMockMvc;

    private VideoUpload videoUpload;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        VideoUploadResource videoUploadResource = new VideoUploadResource();
        ReflectionTestUtils.setField(videoUploadResource, "videoUploadSearchRepository", videoUploadSearchRepository);
        ReflectionTestUtils.setField(videoUploadResource, "videoUploadRepository", videoUploadRepository);
        this.restVideoUploadMockMvc = MockMvcBuilders.standaloneSetup(videoUploadResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        videoUploadSearchRepository.deleteAll();
        videoUpload = new VideoUpload();
        videoUpload.setVideoField(DEFAULT_VIDEO_FIELD);
        videoUpload.setVideoFieldContentType(DEFAULT_VIDEO_FIELD_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createVideoUpload() throws Exception {
        int databaseSizeBeforeCreate = videoUploadRepository.findAll().size();

        // Create the VideoUpload

        restVideoUploadMockMvc.perform(post("/api/video-uploads")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(videoUpload)))
                .andExpect(status().isCreated());

        // Validate the VideoUpload in the database
        List<VideoUpload> videoUploads = videoUploadRepository.findAll();
        assertThat(videoUploads).hasSize(databaseSizeBeforeCreate + 1);
        VideoUpload testVideoUpload = videoUploads.get(videoUploads.size() - 1);
        assertThat(testVideoUpload.getVideoField()).isEqualTo(DEFAULT_VIDEO_FIELD);
        assertThat(testVideoUpload.getVideoFieldContentType()).isEqualTo(DEFAULT_VIDEO_FIELD_CONTENT_TYPE);

        // Validate the VideoUpload in ElasticSearch
        VideoUpload videoUploadEs = videoUploadSearchRepository.findOne(testVideoUpload.getId());
        assertThat(videoUploadEs).isEqualToComparingFieldByField(testVideoUpload);
    }

    @Test
    @Transactional
    public void checkVideoFieldIsRequired() throws Exception {
        int databaseSizeBeforeTest = videoUploadRepository.findAll().size();
        // set the field null
        videoUpload.setVideoField(null);

        // Create the VideoUpload, which fails.

        restVideoUploadMockMvc.perform(post("/api/video-uploads")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(videoUpload)))
                .andExpect(status().isBadRequest());

        List<VideoUpload> videoUploads = videoUploadRepository.findAll();
        assertThat(videoUploads).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllVideoUploads() throws Exception {
        // Initialize the database
        videoUploadRepository.saveAndFlush(videoUpload);

        // Get all the videoUploads
        restVideoUploadMockMvc.perform(get("/api/video-uploads?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(videoUpload.getId().intValue())))
                .andExpect(jsonPath("$.[*].videoFieldContentType").value(hasItem(DEFAULT_VIDEO_FIELD_CONTENT_TYPE)))
                .andExpect(jsonPath("$.[*].videoField").value(hasItem(Base64Utils.encodeToString(DEFAULT_VIDEO_FIELD))));
    }

    @Test
    @Transactional
    public void getVideoUpload() throws Exception {
        // Initialize the database
        videoUploadRepository.saveAndFlush(videoUpload);

        // Get the videoUpload
        restVideoUploadMockMvc.perform(get("/api/video-uploads/{id}", videoUpload.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(videoUpload.getId().intValue()))
            .andExpect(jsonPath("$.videoFieldContentType").value(DEFAULT_VIDEO_FIELD_CONTENT_TYPE))
            .andExpect(jsonPath("$.videoField").value(Base64Utils.encodeToString(DEFAULT_VIDEO_FIELD)));
    }

    @Test
    @Transactional
    public void getNonExistingVideoUpload() throws Exception {
        // Get the videoUpload
        restVideoUploadMockMvc.perform(get("/api/video-uploads/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateVideoUpload() throws Exception {
        // Initialize the database
        videoUploadRepository.saveAndFlush(videoUpload);
        videoUploadSearchRepository.save(videoUpload);
        int databaseSizeBeforeUpdate = videoUploadRepository.findAll().size();

        // Update the videoUpload
        VideoUpload updatedVideoUpload = new VideoUpload();
        updatedVideoUpload.setId(videoUpload.getId());
        updatedVideoUpload.setVideoField(UPDATED_VIDEO_FIELD);
        updatedVideoUpload.setVideoFieldContentType(UPDATED_VIDEO_FIELD_CONTENT_TYPE);

        restVideoUploadMockMvc.perform(put("/api/video-uploads")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedVideoUpload)))
                .andExpect(status().isOk());

        // Validate the VideoUpload in the database
        List<VideoUpload> videoUploads = videoUploadRepository.findAll();
        assertThat(videoUploads).hasSize(databaseSizeBeforeUpdate);
        VideoUpload testVideoUpload = videoUploads.get(videoUploads.size() - 1);
        assertThat(testVideoUpload.getVideoField()).isEqualTo(UPDATED_VIDEO_FIELD);
        assertThat(testVideoUpload.getVideoFieldContentType()).isEqualTo(UPDATED_VIDEO_FIELD_CONTENT_TYPE);

        // Validate the VideoUpload in ElasticSearch
        VideoUpload videoUploadEs = videoUploadSearchRepository.findOne(testVideoUpload.getId());
        assertThat(videoUploadEs).isEqualToComparingFieldByField(testVideoUpload);
    }

    @Test
    @Transactional
    public void deleteVideoUpload() throws Exception {
        // Initialize the database
        videoUploadRepository.saveAndFlush(videoUpload);
        videoUploadSearchRepository.save(videoUpload);
        int databaseSizeBeforeDelete = videoUploadRepository.findAll().size();

        // Get the videoUpload
        restVideoUploadMockMvc.perform(delete("/api/video-uploads/{id}", videoUpload.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean videoUploadExistsInEs = videoUploadSearchRepository.exists(videoUpload.getId());
        assertThat(videoUploadExistsInEs).isFalse();

        // Validate the database is empty
        List<VideoUpload> videoUploads = videoUploadRepository.findAll();
        assertThat(videoUploads).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchVideoUpload() throws Exception {
        // Initialize the database
        videoUploadRepository.saveAndFlush(videoUpload);
        videoUploadSearchRepository.save(videoUpload);

        // Search the videoUpload
        restVideoUploadMockMvc.perform(get("/api/_search/video-uploads?query=id:" + videoUpload.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(videoUpload.getId().intValue())))
            .andExpect(jsonPath("$.[*].videoFieldContentType").value(hasItem(DEFAULT_VIDEO_FIELD_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].videoField").value(hasItem(Base64Utils.encodeToString(DEFAULT_VIDEO_FIELD))));
    }
}
