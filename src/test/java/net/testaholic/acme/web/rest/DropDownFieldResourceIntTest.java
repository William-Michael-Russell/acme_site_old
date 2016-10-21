package net.testaholic.acme.web.rest;

import net.testaholic.acme.AcmeSiteApp;
import net.testaholic.acme.domain.DropDownField;
import net.testaholic.acme.repository.DropDownFieldRepository;
import net.testaholic.acme.repository.search.DropDownFieldSearchRepository;

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
 * Test class for the DropDownFieldResource REST controller.
 *
 * @see DropDownFieldResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AcmeSiteApp.class)
@WebAppConfiguration
@IntegrationTest
public class DropDownFieldResourceIntTest {

    private static final String DEFAULT_WEAPON_NAME = "AAAAA";
    private static final String UPDATED_WEAPON_NAME = "BBBBB";

    @Inject
    private DropDownFieldRepository dropDownFieldRepository;

    @Inject
    private DropDownFieldSearchRepository dropDownFieldSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restDropDownFieldMockMvc;

    private DropDownField dropDownField;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DropDownFieldResource dropDownFieldResource = new DropDownFieldResource();
        ReflectionTestUtils.setField(dropDownFieldResource, "dropDownFieldSearchRepository", dropDownFieldSearchRepository);
        ReflectionTestUtils.setField(dropDownFieldResource, "dropDownFieldRepository", dropDownFieldRepository);
        this.restDropDownFieldMockMvc = MockMvcBuilders.standaloneSetup(dropDownFieldResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        dropDownFieldSearchRepository.deleteAll();
        dropDownField = new DropDownField();
        dropDownField.setWeaponName(DEFAULT_WEAPON_NAME);
    }

    @Test
    @Transactional
    public void createDropDownField() throws Exception {
        int databaseSizeBeforeCreate = dropDownFieldRepository.findAll().size();

        // Create the DropDownField

        restDropDownFieldMockMvc.perform(post("/api/drop-down-fields")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dropDownField)))
                .andExpect(status().isCreated());

        // Validate the DropDownField in the database
        List<DropDownField> dropDownFields = dropDownFieldRepository.findAll();
        assertThat(dropDownFields).hasSize(databaseSizeBeforeCreate + 1);
        DropDownField testDropDownField = dropDownFields.get(dropDownFields.size() - 1);
        assertThat(testDropDownField.getWeaponName()).isEqualTo(DEFAULT_WEAPON_NAME);

        // Validate the DropDownField in ElasticSearch
        DropDownField dropDownFieldEs = dropDownFieldSearchRepository.findOne(testDropDownField.getId());
        assertThat(dropDownFieldEs).isEqualToComparingFieldByField(testDropDownField);
    }

    @Test
    @Transactional
    public void checkWeaponNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = dropDownFieldRepository.findAll().size();
        // set the field null
        dropDownField.setWeaponName(null);

        // Create the DropDownField, which fails.

        restDropDownFieldMockMvc.perform(post("/api/drop-down-fields")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dropDownField)))
                .andExpect(status().isBadRequest());

        List<DropDownField> dropDownFields = dropDownFieldRepository.findAll();
        assertThat(dropDownFields).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDropDownFields() throws Exception {
        // Initialize the database
        dropDownFieldRepository.saveAndFlush(dropDownField);

        // Get all the dropDownFields
        restDropDownFieldMockMvc.perform(get("/api/drop-down-fields?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(dropDownField.getId().intValue())))
                .andExpect(jsonPath("$.[*].weaponName").value(hasItem(DEFAULT_WEAPON_NAME.toString())));
    }

    @Test
    @Transactional
    public void getDropDownField() throws Exception {
        // Initialize the database
        dropDownFieldRepository.saveAndFlush(dropDownField);

        // Get the dropDownField
        restDropDownFieldMockMvc.perform(get("/api/drop-down-fields/{id}", dropDownField.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(dropDownField.getId().intValue()))
            .andExpect(jsonPath("$.weaponName").value(DEFAULT_WEAPON_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingDropDownField() throws Exception {
        // Get the dropDownField
        restDropDownFieldMockMvc.perform(get("/api/drop-down-fields/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDropDownField() throws Exception {
        // Initialize the database
        dropDownFieldRepository.saveAndFlush(dropDownField);
        dropDownFieldSearchRepository.save(dropDownField);
        int databaseSizeBeforeUpdate = dropDownFieldRepository.findAll().size();

        // Update the dropDownField
        DropDownField updatedDropDownField = new DropDownField();
        updatedDropDownField.setId(dropDownField.getId());
        updatedDropDownField.setWeaponName(UPDATED_WEAPON_NAME);

        restDropDownFieldMockMvc.perform(put("/api/drop-down-fields")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedDropDownField)))
                .andExpect(status().isOk());

        // Validate the DropDownField in the database
        List<DropDownField> dropDownFields = dropDownFieldRepository.findAll();
        assertThat(dropDownFields).hasSize(databaseSizeBeforeUpdate);
        DropDownField testDropDownField = dropDownFields.get(dropDownFields.size() - 1);
        assertThat(testDropDownField.getWeaponName()).isEqualTo(UPDATED_WEAPON_NAME);

        // Validate the DropDownField in ElasticSearch
        DropDownField dropDownFieldEs = dropDownFieldSearchRepository.findOne(testDropDownField.getId());
        assertThat(dropDownFieldEs).isEqualToComparingFieldByField(testDropDownField);
    }

    @Test
    @Transactional
    public void deleteDropDownField() throws Exception {
        // Initialize the database
        dropDownFieldRepository.saveAndFlush(dropDownField);
        dropDownFieldSearchRepository.save(dropDownField);
        int databaseSizeBeforeDelete = dropDownFieldRepository.findAll().size();

        // Get the dropDownField
        restDropDownFieldMockMvc.perform(delete("/api/drop-down-fields/{id}", dropDownField.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean dropDownFieldExistsInEs = dropDownFieldSearchRepository.exists(dropDownField.getId());
        assertThat(dropDownFieldExistsInEs).isFalse();

        // Validate the database is empty
        List<DropDownField> dropDownFields = dropDownFieldRepository.findAll();
        assertThat(dropDownFields).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchDropDownField() throws Exception {
        // Initialize the database
        dropDownFieldRepository.saveAndFlush(dropDownField);
        dropDownFieldSearchRepository.save(dropDownField);

        // Search the dropDownField
        restDropDownFieldMockMvc.perform(get("/api/_search/drop-down-fields?query=id:" + dropDownField.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dropDownField.getId().intValue())))
            .andExpect(jsonPath("$.[*].weaponName").value(hasItem(DEFAULT_WEAPON_NAME.toString())));
    }
}
