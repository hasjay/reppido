package com.hasjay.reppido.report;

import com.hasjay.reppido.category.model.Category;
import com.hasjay.reppido.category.model.CategoryStatus;
import com.hasjay.reppido.category.repository.CategoryRepository;
import com.hasjay.reppido.report.dto.CreateReportCommentRequest;
import com.hasjay.reppido.report.model.Report;
import com.hasjay.reppido.report.model.ReportComment;
import com.hasjay.reppido.report.repository.ReportCommentRepository;
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

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReportCommentIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReportCommentRepository reportCommentRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Integer reportId;

    @BeforeEach
    void setUp() {
        Category mainCategory = categoryRepository.save(Category.builder()
                .name("Road")
                .status(CategoryStatus.ACTIVE)
                .build());

        Category subcategory = categoryRepository.save(Category.builder()
                .mainCategory(mainCategory)
                .name("Pothole")
                .status(CategoryStatus.ACTIVE)
                .build());

        Report report = reportRepository.save(Report.builder()
                .category(subcategory)
                .location("Main Street")
                .longitude(79.8612)
                .latitude(6.9271)
                .createdOn(LocalDateTime.now())
                .build());

        reportId = report.getId();
    }

    @AfterEach
    void tearDown() {
        reportCommentRepository.deleteAll();
        reportRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void testAddCommentReturns201AndPersists() throws Exception {
        CreateReportCommentRequest request = new CreateReportCommentRequest("This is a test comment");

        mockMvc.perform(post("/reports/{id}/comments", reportId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.reportId").value(reportId))
                .andExpect(jsonPath("$.comment").value("This is a test comment"))
                .andExpect(jsonPath("$.createdDate").isNotEmpty());

        assertEquals(1L, reportCommentRepository.count());
    }

    @Test
    void testAddCommentBlankCommentReturns400() throws Exception {
        CreateReportCommentRequest request = new CreateReportCommentRequest("");

        mockMvc.perform(post("/reports/{id}/comments", reportId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.comment").exists());

        assertEquals(0L, reportCommentRepository.count());
    }

    @Test
    void testAddCommentReportNotFoundReturns404() throws Exception {
        CreateReportCommentRequest request = new CreateReportCommentRequest("Some comment");

        mockMvc.perform(post("/reports/9999/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Report not found with id: 9999"));

        assertEquals(0L, reportCommentRepository.count());
    }

    @Test
    void testGetCommentsReturns200WithList() throws Exception {
        reportCommentRepository.save(ReportComment.builder()
                .report(reportRepository.findById(reportId).orElseThrow())
                .comment("First comment")
                .createdDate(LocalDateTime.now())
                .build());
        reportCommentRepository.save(ReportComment.builder()
                .report(reportRepository.findById(reportId).orElseThrow())
                .comment("Second comment")
                .createdDate(LocalDateTime.now())
                .build());

        mockMvc.perform(get("/reports/{id}/comments", reportId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].reportId").value(reportId))
                .andExpect(jsonPath("$[0].comment").value("First comment"))
                .andExpect(jsonPath("$[1].comment").value("Second comment"));
    }

    @Test
    void testGetCommentsEmptyListReturns200() throws Exception {
        mockMvc.perform(get("/reports/{id}/comments", reportId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetCommentsReportNotFoundReturns404() throws Exception {
        mockMvc.perform(get("/reports/9999/comments"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Report not found with id: 9999"));
    }
}
