package com.hasjay.reppido.report.service;

import com.hasjay.reppido.report.dto.CreateReportRequest;
import com.hasjay.reppido.report.dto.ReportResponse;
import com.hasjay.reppido.report.model.Report;
import com.hasjay.reppido.report.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportService reportService;

    private CreateReportRequest request;
    private Report savedReport;

    @BeforeEach
    void setUp() {
        request = new CreateReportRequest(
                "Pothole",
                "Large pothole on main street",
                "Main Street",
                79.8612,
                6.9271
        );

        savedReport = Report.builder()
                .id(1)
                .category("Pothole")
                .description("Large pothole on main street")
                .location("Main Street")
                .longitude(79.8612)
                .latitude(6.9271)
                .createdOn(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateReport() {
        when(reportRepository.save(any(Report.class))).thenReturn(savedReport);

        ReportResponse response = reportService.createReport(request);

        verify(reportRepository).save(any(Report.class));
        assertEquals(1, response.getId());
        assertEquals("Pothole", response.getCategory());
        assertEquals("Large pothole on main street", response.getDescription());
        assertEquals("Main Street", response.getLocation());
        assertEquals(79.8612, response.getLongitude());
        assertEquals(6.9271, response.getLatitude());
        assertNotNull(response.getCreatedOn());
    }

    @Test
    void testCreateReportWithoutDescription() {
        request.setDescription(null);
        savedReport = Report.builder()
                .id(2)
                .category("Pothole")
                .description(null)
                .location("Main Street")
                .longitude(79.8612)
                .latitude(6.9271)
                .createdOn(LocalDateTime.now())
                .build();

        when(reportRepository.save(any(Report.class))).thenReturn(savedReport);

        ReportResponse response = reportService.createReport(request);

        verify(reportRepository).save(any(Report.class));
        assertNull(response.getDescription());
    }
}
