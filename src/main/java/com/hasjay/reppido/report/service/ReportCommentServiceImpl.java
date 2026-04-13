package com.hasjay.reppido.report.service;

import com.hasjay.reppido.exception.ReportNotFoundException;
import com.hasjay.reppido.report.dto.CreateReportCommentRequest;
import com.hasjay.reppido.report.dto.ReportCommentResponse;
import com.hasjay.reppido.report.model.Report;
import com.hasjay.reppido.report.model.ReportComment;
import com.hasjay.reppido.report.repository.ReportCommentRepository;
import com.hasjay.reppido.report.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportCommentServiceImpl implements ReportCommentService{

    private final ReportRepository reportRepository;
    private final ReportCommentRepository reportCommentRepository;

    public ReportCommentServiceImpl(ReportRepository reportRepository,
                                ReportCommentRepository reportCommentRepository) {
        this.reportRepository = reportRepository;
        this.reportCommentRepository = reportCommentRepository;
    }

    @Transactional
    public ReportCommentResponse addComment(Integer reportId, CreateReportCommentRequest request) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));

        ReportComment comment = ReportComment.builder()
                .report(report)
                .comment(request.getComment())
                .user(null)
                .createdDate(LocalDateTime.now())
                .build();

        ReportComment saved = reportCommentRepository.save(comment);

        return ReportCommentResponse.builder()
                .id(saved.getId())
                .reportId(saved.getReport().getId())
                .comment(saved.getComment())
                .createdDate(saved.getCreatedDate())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ReportCommentResponse> getCommentsByReport(Integer reportId) {
        reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));

        return reportCommentRepository.findByReportId(reportId)
                .stream()
                .map(c -> ReportCommentResponse.builder()
                        .id(c.getId())
                        .reportId(reportId)
                        .comment(c.getComment())
                        .createdDate(c.getCreatedDate())
                        .build())
                .toList();
    }
}
