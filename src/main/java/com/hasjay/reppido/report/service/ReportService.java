package com.hasjay.reppido.report.service;

import com.hasjay.reppido.report.dto.CreateReportRequest;
import com.hasjay.reppido.report.dto.ReportResponse;
import com.hasjay.reppido.report.model.Report;
import com.hasjay.reppido.report.repository.ReportRepository;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public ReportResponse createReport(CreateReportRequest request) {
        Report report = Report.builder()
                .category(request.getCategory())
                .description(request.getDescription())
                .location(request.getLocation())
                .longitude(request.getLongitude())
                .latitude(request.getLatitude())
                .createdOn(LocalDateTime.now())
                .build();

        Report saved = reportRepository.save(report);

        return ReportResponse.builder()
                .id(saved.getId())
                .category(saved.getCategory())
                .description(saved.getDescription())
                .location(saved.getLocation())
                .longitude(saved.getLongitude())
                .latitude(saved.getLatitude())
                .createdOn(saved.getCreatedOn())
                .build();
    }
}
