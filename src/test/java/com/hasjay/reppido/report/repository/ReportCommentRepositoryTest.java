package com.hasjay.reppido.report.repository;

import com.hasjay.reppido.category.model.Category;
import com.hasjay.reppido.category.model.CategoryStatus;
import com.hasjay.reppido.category.repository.CategoryRepository;
import com.hasjay.reppido.report.model.Report;
import com.hasjay.reppido.report.model.ReportComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ReportCommentRepositoryTest {

    @Autowired
    private ReportCommentRepository reportCommentRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Report reportA;
    private Report reportB;

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

        reportA = reportRepository.save(Report.builder()
                .category(subcategory)
                .location("Main Street")
                .longitude(79.8612)
                .latitude(6.9271)
                .createdOn(LocalDateTime.now())
                .build());

        reportB = reportRepository.save(Report.builder()
                .category(subcategory)
                .location("Park Avenue")
                .longitude(80.0000)
                .latitude(7.0000)
                .createdOn(LocalDateTime.now())
                .build());
    }

    @Test
    void testSaveComment() {
        ReportComment comment = ReportComment.builder()
                .report(reportA)
                .comment("This is a test comment")
                .createdDate(LocalDateTime.now())
                .build();

        ReportComment saved = reportCommentRepository.save(comment);

        assertNotNull(saved.getId());
        assertEquals("This is a test comment", saved.getComment());
        assertNull(saved.getUser());
        assertNotNull(saved.getCreatedDate());
    }

    @Test
    void testFindByReportIdReturnsCorrectSubset() {
        reportCommentRepository.save(ReportComment.builder()
                .report(reportA).comment("Comment on A - 1").createdDate(LocalDateTime.now()).build());
        reportCommentRepository.save(ReportComment.builder()
                .report(reportA).comment("Comment on A - 2").createdDate(LocalDateTime.now()).build());
        reportCommentRepository.save(ReportComment.builder()
                .report(reportB).comment("Comment on B - 1").createdDate(LocalDateTime.now()).build());

        List<ReportComment> commentsForA = reportCommentRepository.findByReportId(reportA.getId());

        assertEquals(2, commentsForA.size());
        assertTrue(commentsForA.stream().allMatch(c -> c.getReport().getId().equals(reportA.getId())));
    }

    @Test
    void testFindByReportIdReturnsEmptyList() {
        List<ReportComment> comments = reportCommentRepository.findByReportId(reportA.getId());

        assertTrue(comments.isEmpty());
    }
}
