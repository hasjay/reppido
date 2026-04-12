package com.hasjay.reppido.report;

import com.hasjay.reppido.category.model.Category;
import com.hasjay.reppido.category.model.CategoryStatus;
import com.hasjay.reppido.category.repository.CategoryRepository;
import com.hasjay.reppido.report.dto.CreateReportRequest;
import com.hasjay.reppido.report.repository.ReportRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReportIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Integer subcategoryId;
    private Integer mainCategoryId;

    @BeforeEach
    void setUp() {
        Category mainCategory = categoryRepository.save(Category.builder()
                .name("Road")
                .status(CategoryStatus.ACTIVE)
                .build());
        mainCategoryId = mainCategory.getId();

        Category subcategory = categoryRepository.save(Category.builder()
                .mainCategory(mainCategory)
                .name("Pothole")
                .status(CategoryStatus.ACTIVE)
                .build());
        subcategoryId = subcategory.getId();
    }

    @AfterEach
    void tearDown() {
        reportRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void testCreateReport() throws Exception {
        CreateReportRequest request = new CreateReportRequest(
                subcategoryId,
                "Large pothole on main street",
                "Main Street",
                79.8612,
                6.9271
        );

        mockMvc.perform(post("/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.category.id").value(subcategoryId))
                .andExpect(jsonPath("$.category.name").value("Pothole"))
                .andExpect(jsonPath("$.description").value("Large pothole on main street"))
                .andExpect(jsonPath("$.location").value("Main Street"))
                .andExpect(jsonPath("$.longitude").value(79.8612))
                .andExpect(jsonPath("$.latitude").value(6.9271))
                .andExpect(jsonPath("$.createdOn").isNotEmpty());

        assertEquals(1L, reportRepository.count());
    }

    @Test
    void testCreateReportWithoutDescription() throws Exception {
        CreateReportRequest request = new CreateReportRequest(
                subcategoryId,
                null,
                "Park Avenue",
                80.0000,
                7.0000
        );

        mockMvc.perform(post("/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.category.name").value("Pothole"));

        assertEquals(1L, reportRepository.count());
    }

    @Test
    void testCreateReportValidationFailure() throws Exception {
        CreateReportRequest request = new CreateReportRequest(
                null,
                "Some description",
                null,
                null,
                null
        );

        mockMvc.perform(post("/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.categoryId").exists())
                .andExpect(jsonPath("$.location").exists());

        assertEquals(0L, reportRepository.count());
    }

    @Test
    void testCreateReportCategoryNotFound() throws Exception {
        CreateReportRequest request = new CreateReportRequest(
                9999,
                null,
                "Main Street",
                79.8612,
                6.9271
        );

        mockMvc.perform(post("/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());

        assertEquals(0L, reportRepository.count());
    }

    @Test
    void testCreateReportWithMainCategory() throws Exception {
        CreateReportRequest request = new CreateReportRequest(
                mainCategoryId,
                null,
                "Main Street",
                79.8612,
                6.9271
        );

        mockMvc.perform(post("/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Report category must be a subcategory"));

        assertEquals(0L, reportRepository.count());
    }
}
