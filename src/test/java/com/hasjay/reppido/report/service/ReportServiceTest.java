package com.hasjay.reppido.report.service;

import com.hasjay.reppido.category.model.Category;
import com.hasjay.reppido.category.model.CategoryStatus;
import com.hasjay.reppido.category.repository.CategoryRepository;
import com.hasjay.reppido.exception.CategoryNotFoundException;
import com.hasjay.reppido.exception.InvalidCategoryException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ReportService reportService;

    private CreateReportRequest request;
    private Category mainCategory;
    private Category subcategory;
    private Report savedReport;

    @BeforeEach
    void setUp() {
        request = new CreateReportRequest(
                2,
                "Large pothole on main street",
                "Main Street",
                79.8612,
                6.9271
        );

        mainCategory = Category.builder()
                .id(1)
                .name("Road")
                .status(CategoryStatus.ACTIVE)
                .build();

        subcategory = Category.builder()
                .id(2)
                .mainCategory(mainCategory)
                .name("Pothole")
                .status(CategoryStatus.ACTIVE)
                .build();

        savedReport = Report.builder()
                .id(1)
                .category(subcategory)
                .description("Large pothole on main street")
                .location("Main Street")
                .longitude(79.8612)
                .latitude(6.9271)
                .createdOn(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateReport() {
        when(categoryRepository.findById(2)).thenReturn(Optional.of(subcategory));
        when(reportRepository.save(any(Report.class))).thenReturn(savedReport);

        ReportResponse response = reportService.createReport(request);

        verify(categoryRepository).findById(2);
        verify(reportRepository).save(any(Report.class));
        assertEquals(1, response.getId());
        assertEquals(2, response.getCategory().getId());
        assertEquals("Pothole", response.getCategory().getName());
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
                .category(subcategory)
                .description(null)
                .location("Main Street")
                .longitude(79.8612)
                .latitude(6.9271)
                .createdOn(LocalDateTime.now())
                .build();

        when(categoryRepository.findById(2)).thenReturn(Optional.of(subcategory));
        when(reportRepository.save(any(Report.class))).thenReturn(savedReport);

        ReportResponse response = reportService.createReport(request);

        verify(reportRepository).save(any(Report.class));
        assertNull(response.getDescription());
    }

    @Test
    void testCreateReportCategoryNotFound() {
        when(categoryRepository.findById(99)).thenReturn(Optional.empty());
        request = new CreateReportRequest(99, "desc", "Main Street", 79.8612, 6.9271);

        assertThrows(CategoryNotFoundException.class, () -> reportService.createReport(request));
    }

    @Test
    void testCreateReportWithMainCategory() {
        when(categoryRepository.findById(1)).thenReturn(Optional.of(mainCategory));
        request = new CreateReportRequest(1, "desc", "Main Street", 79.8612, 6.9271);

        assertThrows(InvalidCategoryException.class, () -> reportService.createReport(request));
    }
}
