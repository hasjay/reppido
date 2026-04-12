package com.hasjay.reppido.report.service;

import com.hasjay.reppido.category.dto.CategoryResponse;
import com.hasjay.reppido.category.model.Category;
import com.hasjay.reppido.category.repository.CategoryRepository;
import com.hasjay.reppido.exception.CategoryNotFoundException;
import com.hasjay.reppido.exception.InvalidCategoryException;
import com.hasjay.reppido.report.dto.CreateReportRequest;
import com.hasjay.reppido.report.dto.ReportResponse;
import com.hasjay.reppido.report.model.Report;
import com.hasjay.reppido.report.repository.ReportRepository;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final CategoryRepository categoryRepository;

    public ReportService(ReportRepository reportRepository, CategoryRepository categoryRepository) {
        this.reportRepository = reportRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public ReportResponse createReport(CreateReportRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));

        if (category.getMainCategory() == null) {
            throw new InvalidCategoryException("Report category must be a subcategory");
        }

        Report report = Report.builder()
                .category(category)
                .description(request.getDescription())
                .location(request.getLocation())
                .longitude(request.getLongitude())
                .latitude(request.getLatitude())
                .createdOn(LocalDateTime.now())
                .build();

        Report saved = reportRepository.save(report);

        CategoryResponse categoryResponse = CategoryResponse.builder()
                .id(saved.getCategory().getId())
                .name(saved.getCategory().getName())
                .status(saved.getCategory().getStatus())
                .build();

        return ReportResponse.builder()
                .id(saved.getId())
                .category(categoryResponse)
                .description(saved.getDescription())
                .location(saved.getLocation())
                .longitude(saved.getLongitude())
                .latitude(saved.getLatitude())
                .createdOn(saved.getCreatedOn())
                .build();
    }
}
