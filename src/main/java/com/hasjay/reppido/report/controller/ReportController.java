package com.hasjay.reppido.report.controller;

import com.hasjay.reppido.report.dto.CreateReportRequest;
import com.hasjay.reppido.report.dto.ReportResponse;
import com.hasjay.reppido.report.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<ReportResponse> createReport(@Valid @RequestBody CreateReportRequest request) {
        ReportResponse response = reportService.createReport(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/any")
    public ResponseEntity<String> getReport() {
        return ResponseEntity.status(HttpStatus.OK).body("Report : 800");
    }
}
