package com.hasjay.reppido.report.service;

import com.hasjay.reppido.category.model.Category;
import com.hasjay.reppido.category.model.CategoryStatus;
import com.hasjay.reppido.exception.ReportNotFoundException;
import com.hasjay.reppido.report.dto.CreateReportCommentRequest;
import com.hasjay.reppido.report.dto.ReportCommentResponse;
import com.hasjay.reppido.report.model.Report;
import com.hasjay.reppido.report.model.ReportComment;
import com.hasjay.reppido.report.repository.ReportCommentRepository;
import com.hasjay.reppido.report.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportCommentServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ReportCommentRepository reportCommentRepository;

    @InjectMocks
    private ReportCommentServiceImpl reportCommentService;

    private Report report;
    private ReportComment savedComment;

    @BeforeEach
    void setUp() {
        Category mainCategory = Category.builder()
                .id(1)
                .name("Road")
                .status(CategoryStatus.ACTIVE)
                .build();

        Category subcategory = Category.builder()
                .id(2)
                .mainCategory(mainCategory)
                .name("Pothole")
                .status(CategoryStatus.ACTIVE)
                .build();

        report = Report.builder()
                .id(1)
                .category(subcategory)
                .location("Main Street")
                .longitude(79.8612)
                .latitude(6.9271)
                .createdOn(LocalDateTime.now())
                .build();

        savedComment = ReportComment.builder()
                .id(1)
                .report(report)
                .comment("This is a test comment")
                .user(null)
                .createdDate(LocalDateTime.now())
                .build();
    }

    @Test
    void testAddComment() {
        CreateReportCommentRequest request = new CreateReportCommentRequest("This is a test comment");

        when(reportRepository.findById(1)).thenReturn(Optional.of(report));
        when(reportCommentRepository.save(any(ReportComment.class))).thenReturn(savedComment);

        ReportCommentResponse response = reportCommentService.addComment(1, request);

        verify(reportRepository).findById(1);
        verify(reportCommentRepository).save(any(ReportComment.class));
        assertEquals(1, response.getId());
        assertEquals(1, response.getReportId());
        assertEquals("This is a test comment", response.getComment());
        assertNotNull(response.getCreatedDate());
    }

    @Test
    void testAddCommentReportNotFound() {
        CreateReportCommentRequest request = new CreateReportCommentRequest("This is a test comment");

        when(reportRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ReportNotFoundException.class, () -> reportCommentService.addComment(99, request));
    }

    @Test
    void testGetCommentsByReport() {
        ReportComment secondComment = ReportComment.builder()
                .id(2)
                .report(report)
                .comment("Another comment")
                .user(null)
                .createdDate(LocalDateTime.now())
                .build();

        when(reportRepository.findById(1)).thenReturn(Optional.of(report));
        when(reportCommentRepository.findByReportId(1)).thenReturn(List.of(savedComment, secondComment));

        List<ReportCommentResponse> responses = reportCommentService.getCommentsByReport(1);

        verify(reportCommentRepository).findByReportId(1);
        assertEquals(2, responses.size());
        assertEquals("This is a test comment", responses.get(0).getComment());
        assertEquals("Another comment", responses.get(1).getComment());
    }

    @Test
    void testGetCommentsByReportNotFound() {
        when(reportRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ReportNotFoundException.class, () -> reportCommentService.getCommentsByReport(99));
    }

    @Test
    void testGetCommentsByReportEmptyList() {
        when(reportRepository.findById(1)).thenReturn(Optional.of(report));
        when(reportCommentRepository.findByReportId(1)).thenReturn(List.of());

        List<ReportCommentResponse> responses = reportCommentService.getCommentsByReport(1);

        assertTrue(responses.isEmpty());
    }
}
