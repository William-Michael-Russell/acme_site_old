package net.testaholic.acme.web.rest;

import net.testaholic.acme.AcmeSiteApp;
import net.testaholic.acme.domain.ImageUpload;
import net.testaholic.acme.repository.ImageUploadRepository;
import net.testaholic.acme.repository.search.ImageUploadSearchRepository;

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
 * Test class for the ImageUploadResource REST controller.
 *
 * @see ImageUploadResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AcmeSiteApp.class)
@WebAppConfiguration
@IntegrationTest
public class ImageUploadResourceIntTest {


    private static final byte[] DEFAULT_IMAGE_FIELD = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE_FIELD = TestUtil.createByteArray(5000000, "1");
    private static final String DEFAULT_IMAGE_FIELD_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_FIELD_CONTENT_TYPE = "image/png";

    @Inject
    private ImageUploadRepository imageUploadRepository;

    @Inject
    private ImageUploadSearchRepository imageUploadSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restImageUploadMockMvc;

    private ImageUpload imageUpload;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ImageUploadResource imageUploadResource = new ImageUploadResource();
        ReflectionTestUtils.setField(imageUploadResource, "imageUploadSearchRepository", imageUploadSearchRepository);
        ReflectionTestUtils.setField(imageUploadResource, "imageUploadRepository", imageUploadRepository);
        this.restImageUploadMockMvc = MockMvcBuilders.standaloneSetup(imageUploadResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        imageUploadSearchRepository.deleteAll();
        imageUpload = new ImageUpload();
        imageUpload.setImageField(DEFAULT_IMAGE_FIELD);
        imageUpload.setImageFieldContentType(DEFAULT_IMAGE_FIELD_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createImageUpload() throws Exception {
        int databaseSizeBeforeCreate = imageUploadRepository.findAll().size();

        // Create the ImageUpload

        restImageUploadMockMvc.perform(post("/api/image-uploads")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(imageUpload)))
                .andExpect(status().isCreated());

        // Validate the ImageUpload in the database
        List<ImageUpload> imageUploads = imageUploadRepository.findAll();
        assertThat(imageUploads).hasSize(databaseSizeBeforeCreate + 1);
        ImageUpload testImageUpload = imageUploads.get(imageUploads.size() - 1);
        assertThat(testImageUpload.getImageField()).isEqualTo(DEFAULT_IMAGE_FIELD);
        assertThat(testImageUpload.getImageFieldContentType()).isEqualTo(DEFAULT_IMAGE_FIELD_CONTENT_TYPE);

        // Validate the ImageUpload in ElasticSearch
        ImageUpload imageUploadEs = imageUploadSearchRepository.findOne(testImageUpload.getId());
        assertThat(imageUploadEs).isEqualToComparingFieldByField(testImageUpload);
    }

    @Test
    @Transactional
    public void checkImageFieldIsRequired() throws Exception {
        int databaseSizeBeforeTest = imageUploadRepository.findAll().size();
        // set the field null
        imageUpload.setImageField(null);

        // Create the ImageUpload, which fails.

        restImageUploadMockMvc.perform(post("/api/image-uploads")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(imageUpload)))
                .andExpect(status().isBadRequest());

        List<ImageUpload> imageUploads = imageUploadRepository.findAll();
        assertThat(imageUploads).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllImageUploads() throws Exception {
        // Initialize the database
        imageUploadRepository.saveAndFlush(imageUpload);

        // Get all the imageUploads
        restImageUploadMockMvc.perform(get("/api/image-uploads?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(imageUpload.getId().intValue())))
                .andExpect(jsonPath("$.[*].imageFieldContentType").value(hasItem(DEFAULT_IMAGE_FIELD_CONTENT_TYPE)))
                .andExpect(jsonPath("$.[*].imageField").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE_FIELD))));
    }

    @Test
    @Transactional
    public void getImageUpload() throws Exception {
        // Initialize the database
        imageUploadRepository.saveAndFlush(imageUpload);

        // Get the imageUpload
        restImageUploadMockMvc.perform(get("/api/image-uploads/{id}", imageUpload.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(imageUpload.getId().intValue()))
            .andExpect(jsonPath("$.imageFieldContentType").value(DEFAULT_IMAGE_FIELD_CONTENT_TYPE))
            .andExpect(jsonPath("$.imageField").value(Base64Utils.encodeToString(DEFAULT_IMAGE_FIELD)));
    }

    @Test
    @Transactional
    public void getNonExistingImageUpload() throws Exception {
        // Get the imageUpload
        restImageUploadMockMvc.perform(get("/api/image-uploads/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateImageUpload() throws Exception {
        // Initialize the database
        imageUploadRepository.saveAndFlush(imageUpload);
        imageUploadSearchRepository.save(imageUpload);
        int databaseSizeBeforeUpdate = imageUploadRepository.findAll().size();

        // Update the imageUpload
        ImageUpload updatedImageUpload = new ImageUpload();
        updatedImageUpload.setId(imageUpload.getId());
        updatedImageUpload.setImageField(UPDATED_IMAGE_FIELD);
        updatedImageUpload.setImageFieldContentType(UPDATED_IMAGE_FIELD_CONTENT_TYPE);

        restImageUploadMockMvc.perform(put("/api/image-uploads")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedImageUpload)))
                .andExpect(status().isOk());

        // Validate the ImageUpload in the database
        List<ImageUpload> imageUploads = imageUploadRepository.findAll();
        assertThat(imageUploads).hasSize(databaseSizeBeforeUpdate);
        ImageUpload testImageUpload = imageUploads.get(imageUploads.size() - 1);
        assertThat(testImageUpload.getImageField()).isEqualTo(UPDATED_IMAGE_FIELD);
        assertThat(testImageUpload.getImageFieldContentType()).isEqualTo(UPDATED_IMAGE_FIELD_CONTENT_TYPE);

        // Validate the ImageUpload in ElasticSearch
        ImageUpload imageUploadEs = imageUploadSearchRepository.findOne(testImageUpload.getId());
        assertThat(imageUploadEs).isEqualToComparingFieldByField(testImageUpload);
    }

    @Test
    @Transactional
    public void deleteImageUpload() throws Exception {
        // Initialize the database
        imageUploadRepository.saveAndFlush(imageUpload);
        imageUploadSearchRepository.save(imageUpload);
        int databaseSizeBeforeDelete = imageUploadRepository.findAll().size();

        // Get the imageUpload
        restImageUploadMockMvc.perform(delete("/api/image-uploads/{id}", imageUpload.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean imageUploadExistsInEs = imageUploadSearchRepository.exists(imageUpload.getId());
        assertThat(imageUploadExistsInEs).isFalse();

        // Validate the database is empty
        List<ImageUpload> imageUploads = imageUploadRepository.findAll();
        assertThat(imageUploads).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchImageUpload() throws Exception {
        // Initialize the database
        imageUploadRepository.saveAndFlush(imageUpload);
        imageUploadSearchRepository.save(imageUpload);

        // Search the imageUpload
        restImageUploadMockMvc.perform(get("/api/_search/image-uploads?query=id:" + imageUpload.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(imageUpload.getId().intValue())))
            .andExpect(jsonPath("$.[*].imageFieldContentType").value(hasItem(DEFAULT_IMAGE_FIELD_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].imageField").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE_FIELD))));
    }
}
