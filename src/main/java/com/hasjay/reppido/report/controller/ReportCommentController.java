package com.hasjay.reppido.report.controller;

import com.hasjay.reppido.report.dto.CreateReportCommentRequest;
import com.hasjay.reppido.report.dto.ReportCommentResponse;
import com.hasjay.reppido.report.service.ReportCommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportCommentController {

    private final ReportCommentService reportCommentService;

    public ReportCommentController(ReportCommentService reportCommentService) {
        this.reportCommentService = reportCommentService;
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<ReportCommentResponse> addComment(
            @PathVariable Integer id,
            @Valid @RequestBody CreateReportCommentRequest request) {
        ReportCommentResponse response = reportCommentService.addComment(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<ReportCommentResponse>> getComments(@PathVariable Integer id) {
        return ResponseEntity.ok(reportCommentService.getCommentsByReport(id));
    }
}
