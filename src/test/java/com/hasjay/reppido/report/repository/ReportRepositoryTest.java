package com.hasjay.reppido.report.repository;

import com.hasjay.reppido.report.model.Report;
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

    @Test
    void testSaveReport() {
        Report report = Report.builder()
                .category("Pothole")
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
        assertEquals("Pothole", found.get().getCategory());
        assertEquals("Main Street", found.get().getLocation());
        assertEquals(79.8612, found.get().getLongitude());
        assertEquals(6.9271, found.get().getLatitude());
    }

    @Test
    void testSaveReportWithoutDescription() {
        Report report = Report.builder()
                .category("Garbage")
                .location("Park Avenue")
                .longitude(80.0000)
                .latitude(7.0000)
                .build();

        Report saved = reportRepository.save(report);

        assertNotNull(saved.getId());
        assertNull(saved.getDescription());
    }
}
