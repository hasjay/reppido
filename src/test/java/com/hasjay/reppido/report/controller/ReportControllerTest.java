package com.hasjay.reppido.report.controller;

import com.hasjay.reppido.category.dto.CategoryResponse;
import com.hasjay.reppido.category.model.CategoryStatus;
import com.hasjay.reppido.exception.CategoryNotFoundException;
import com.hasjay.reppido.exception.InvalidCategoryException;
import com.hasjay.reppido.report.dto.CreateReportRequest;
import com.hasjay.reppido.report.dto.ReportResponse;
import com.hasjay.reppido.report.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReportService reportService;

    @Test
    void testCreateReport() throws Exception {
        CreateReportRequest request = new CreateReportRequest(
                2,
                "Large pothole on main street",
                "Main Street",
                79.8612,
                6.9271
        );

        CategoryResponse categoryResponse = CategoryResponse.builder()
                .id(2)
                .name("Pothole")
                .status(CategoryStatus.ACTIVE)
                .build();

        ReportResponse response = ReportResponse.builder()
                .id(1)
                .category(categoryResponse)
                .description("Large pothole on main street")
                .location("Main Street")
                .longitude(79.8612)
                .latitude(6.9271)
                .createdOn(LocalDateTime.now())
                .build();

        when(reportService.createReport(any(CreateReportRequest.class))).thenReturn(response);

        mockMvc.perform(post("/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.category.id").value(2))
                .andExpect(jsonPath("$.category.name").value("Pothole"))
                .andExpect(jsonPath("$.location").value("Main Street"))
                .andExpect(jsonPath("$.longitude").value(79.8612))
                .andExpect(jsonPath("$.latitude").value(6.9271));
    }

    @Test
    void testCreateReportMissingRequiredFields() throws Exception {
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
                .andExpect(jsonPath("$.location").exists())
                .andExpect(jsonPath("$.longitude").exists())
                .andExpect(jsonPath("$.latitude").exists());
    }

    @Test
    void testCreateReportWithoutDescription() throws Exception {
        CreateReportRequest request = new CreateReportRequest(
                2,
                null,
                "Main Street",
                79.8612,
                6.9271
        );

        CategoryResponse categoryResponse = CategoryResponse.builder()
                .id(2)
                .name("Pothole")
                .status(CategoryStatus.ACTIVE)
                .build();

        ReportResponse response = ReportResponse.builder()
                .id(2)
                .category(categoryResponse)
                .description(null)
                .location("Main Street")
                .longitude(79.8612)
                .latitude(6.9271)
                .createdOn(LocalDateTime.now())
                .build();

        when(reportService.createReport(any(CreateReportRequest.class))).thenReturn(response);

        mockMvc.perform(post("/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void testCreateReportCategoryNotFound() throws Exception {
        CreateReportRequest request = new CreateReportRequest(
                99,
                null,
                "Main Street",
                79.8612,
                6.9271
        );

        when(reportService.createReport(any(CreateReportRequest.class)))
                .thenThrow(new CategoryNotFoundException(99));

        mockMvc.perform(post("/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testCreateReportNotSubcategory() throws Exception {
        CreateReportRequest request = new CreateReportRequest(
                1,
                null,
                "Main Street",
                79.8612,
                6.9271
        );

        when(reportService.createReport(any(CreateReportRequest.class)))
                .thenThrow(new InvalidCategoryException("Report category must be a subcategory"));

        mockMvc.perform(post("/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Report category must be a subcategory"));
    }
}
