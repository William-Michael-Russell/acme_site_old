package net.testaholic.acme.web.rest;

import net.testaholic.acme.AcmeSiteApp;
import net.testaholic.acme.domain.FileUpload;
import net.testaholic.acme.repository.FileUploadRepository;
import net.testaholic.acme.repository.search.FileUploadSearchRepository;

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
 * Test class for the FileUploadResource REST controller.
 *
 * @see FileUploadResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AcmeSiteApp.class)
@WebAppConfiguration
@IntegrationTest
public class FileUploadResourceIntTest {


    private static final byte[] DEFAULT_FIELD_FIELD = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_FIELD_FIELD = TestUtil.createByteArray(5000000, "1");
    private static final String DEFAULT_FIELD_FIELD_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_FIELD_FIELD_CONTENT_TYPE = "image/png";

    @Inject
    private FileUploadRepository fileUploadRepository;

    @Inject
    private FileUploadSearchRepository fileUploadSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restFileUploadMockMvc;

    private FileUpload fileUpload;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        FileUploadResource fileUploadResource = new FileUploadResource();
        ReflectionTestUtils.setField(fileUploadResource, "fileUploadSearchRepository", fileUploadSearchRepository);
        ReflectionTestUtils.setField(fileUploadResource, "fileUploadRepository", fileUploadRepository);
        this.restFileUploadMockMvc = MockMvcBuilders.standaloneSetup(fileUploadResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        fileUploadSearchRepository.deleteAll();
        fileUpload = new FileUpload();
        fileUpload.setFieldField(DEFAULT_FIELD_FIELD);
        fileUpload.setFieldFieldContentType(DEFAULT_FIELD_FIELD_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createFileUpload() throws Exception {
        int databaseSizeBeforeCreate = fileUploadRepository.findAll().size();

        // Create the FileUpload

        restFileUploadMockMvc.perform(post("/api/file-uploads")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(fileUpload)))
                .andExpect(status().isCreated());

        // Validate the FileUpload in the database
        List<FileUpload> fileUploads = fileUploadRepository.findAll();
        assertThat(fileUploads).hasSize(databaseSizeBeforeCreate + 1);
        FileUpload testFileUpload = fileUploads.get(fileUploads.size() - 1);
        assertThat(testFileUpload.getFieldField()).isEqualTo(DEFAULT_FIELD_FIELD);
        assertThat(testFileUpload.getFieldFieldContentType()).isEqualTo(DEFAULT_FIELD_FIELD_CONTENT_TYPE);

        // Validate the FileUpload in ElasticSearch
        FileUpload fileUploadEs = fileUploadSearchRepository.findOne(testFileUpload.getId());
        assertThat(fileUploadEs).isEqualToComparingFieldByField(testFileUpload);
    }

    @Test
    @Transactional
    public void checkFieldFieldIsRequired() throws Exception {
        int databaseSizeBeforeTest = fileUploadRepository.findAll().size();
        // set the field null
        fileUpload.setFieldField(null);

        // Create the FileUpload, which fails.

        restFileUploadMockMvc.perform(post("/api/file-uploads")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(fileUpload)))
                .andExpect(status().isBadRequest());

        List<FileUpload> fileUploads = fileUploadRepository.findAll();
        assertThat(fileUploads).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllFileUploads() throws Exception {
        // Initialize the database
        fileUploadRepository.saveAndFlush(fileUpload);

        // Get all the fileUploads
        restFileUploadMockMvc.perform(get("/api/file-uploads?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(fileUpload.getId().intValue())))
                .andExpect(jsonPath("$.[*].fieldFieldContentType").value(hasItem(DEFAULT_FIELD_FIELD_CONTENT_TYPE)))
                .andExpect(jsonPath("$.[*].fieldField").value(hasItem(Base64Utils.encodeToString(DEFAULT_FIELD_FIELD))));
    }

    @Test
    @Transactional
    public void getFileUpload() throws Exception {
        // Initialize the database
        fileUploadRepository.saveAndFlush(fileUpload);

        // Get the fileUpload
        restFileUploadMockMvc.perform(get("/api/file-uploads/{id}", fileUpload.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(fileUpload.getId().intValue()))
            .andExpect(jsonPath("$.fieldFieldContentType").value(DEFAULT_FIELD_FIELD_CONTENT_TYPE))
            .andExpect(jsonPath("$.fieldField").value(Base64Utils.encodeToString(DEFAULT_FIELD_FIELD)));
    }

    @Test
    @Transactional
    public void getNonExistingFileUpload() throws Exception {
        // Get the fileUpload
        restFileUploadMockMvc.perform(get("/api/file-uploads/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFileUpload() throws Exception {
        // Initialize the database
        fileUploadRepository.saveAndFlush(fileUpload);
        fileUploadSearchRepository.save(fileUpload);
        int databaseSizeBeforeUpdate = fileUploadRepository.findAll().size();

        // Update the fileUpload
        FileUpload updatedFileUpload = new FileUpload();
        updatedFileUpload.setId(fileUpload.getId());
        updatedFileUpload.setFieldField(UPDATED_FIELD_FIELD);
        updatedFileUpload.setFieldFieldContentType(UPDATED_FIELD_FIELD_CONTENT_TYPE);

        restFileUploadMockMvc.perform(put("/api/file-uploads")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedFileUpload)))
                .andExpect(status().isOk());

        // Validate the FileUpload in the database
        List<FileUpload> fileUploads = fileUploadRepository.findAll();
        assertThat(fileUploads).hasSize(databaseSizeBeforeUpdate);
        FileUpload testFileUpload = fileUploads.get(fileUploads.size() - 1);
        assertThat(testFileUpload.getFieldField()).isEqualTo(UPDATED_FIELD_FIELD);
        assertThat(testFileUpload.getFieldFieldContentType()).isEqualTo(UPDATED_FIELD_FIELD_CONTENT_TYPE);

        // Validate the FileUpload in ElasticSearch
        FileUpload fileUploadEs = fileUploadSearchRepository.findOne(testFileUpload.getId());
        assertThat(fileUploadEs).isEqualToComparingFieldByField(testFileUpload);
    }

    @Test
    @Transactional
    public void deleteFileUpload() throws Exception {
        // Initialize the database
        fileUploadRepository.saveAndFlush(fileUpload);
        fileUploadSearchRepository.save(fileUpload);
        int databaseSizeBeforeDelete = fileUploadRepository.findAll().size();

        // Get the fileUpload
        restFileUploadMockMvc.perform(delete("/api/file-uploads/{id}", fileUpload.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean fileUploadExistsInEs = fileUploadSearchRepository.exists(fileUpload.getId());
        assertThat(fileUploadExistsInEs).isFalse();

        // Validate the database is empty
        List<FileUpload> fileUploads = fileUploadRepository.findAll();
        assertThat(fileUploads).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchFileUpload() throws Exception {
        // Initialize the database
        fileUploadRepository.saveAndFlush(fileUpload);
        fileUploadSearchRepository.save(fileUpload);

        // Search the fileUpload
        restFileUploadMockMvc.perform(get("/api/_search/file-uploads?query=id:" + fileUpload.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fileUpload.getId().intValue())))
            .andExpect(jsonPath("$.[*].fieldFieldContentType").value(hasItem(DEFAULT_FIELD_FIELD_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].fieldField").value(hasItem(Base64Utils.encodeToString(DEFAULT_FIELD_FIELD))));
    }
}
