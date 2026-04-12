package com.hasjay.reppido.report.repository;

import com.hasjay.reppido.category.model.Category;
import com.hasjay.reppido.category.model.CategoryStatus;
import com.hasjay.reppido.category.repository.CategoryRepository;
import com.hasjay.reppido.report.model.Report;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ReportRepositoryTest {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category subcategory;

    @BeforeEach
    void setUp() {
        Category mainCategory = categoryRepository.save(Category.builder()
                .name("Road")
                .status(CategoryStatus.ACTIVE)
                .build());

        subcategory = categoryRepository.save(Category.builder()
                .mainCategory(mainCategory)
                .name("Pothole")
                .status(CategoryStatus.ACTIVE)
                .build());
    }

    @Test
    void testSaveReport() {
        Report report = Report.builder()
                .category(subcategory)
                .description("Large pothole on main street")
                .location("Main Street")
                .longitude(79.8612)
                .latitude(6.9271)
                .createdOn(LocalDateTime.now())
                .build();

        Report saved = reportRepository.save(report);

        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedOn());

        Optional<Report> found = reportRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Pothole", found.get().getCategory().getName());
        assertEquals("Main Street", found.get().getLocation());
        assertEquals(79.8612, found.get().getLongitude());
        assertEquals(6.9271, found.get().getLatitude());
    }

    @Test
    void testSaveReportWithoutDescription() {
        Report report = Report.builder()
                .category(subcategory)
                .location("Park Avenue")
                .longitude(80.0000)
                .latitude(7.0000)
                .build();

        Report saved = reportRepository.save(report);

        assertNotNull(saved.getId());
        assertNull(saved.getDescription());
    }
}
