package com.hasjay.reppido.report.service;

import java.util.List;

import com.hasjay.reppido.report.dto.CreateReportCommentRequest;
import com.hasjay.reppido.report.dto.ReportCommentResponse;

public interface ReportCommentService {
	ReportCommentResponse addComment(Integer reportId, CreateReportCommentRequest request);
	
	List<ReportCommentResponse> getCommentsByReport(Integer reportId);
}
