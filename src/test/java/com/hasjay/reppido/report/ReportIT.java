package com.hasjay.reppido.report;

import com.hasjay.reppido.report.dto.CreateReportRequest;
import com.hasjay.reppido.report.repository.ReportRepository;
import org.junit.jupiter.api.AfterEach;
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

    @AfterEach
    void tearDown() {
        reportRepository.deleteAll();
    }

    @Test
    void testCreateReport() throws Exception {
        CreateReportRequest request = new CreateReportRequest(
                "Pothole",
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
                .andExpect(jsonPath("$.category").value("Pothole"))
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
                "Garbage",
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
                .andExpect(jsonPath("$.category").value("Garbage"));

        assertEquals(1L, reportRepository.count());
    }

    @Test
    void testCreateReportValidationFailure() throws Exception {
        CreateReportRequest request = new CreateReportRequest(
                "",
                "Some description",
                null,
                null,
                null
        );

        mockMvc.perform(post("/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.category").exists())
                .andExpect(jsonPath("$.location").exists());

        assertEquals(0L, reportRepository.count());
    }
}
