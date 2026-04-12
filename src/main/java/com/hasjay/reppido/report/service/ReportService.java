package com.hasjay.reppido.report.service;

import com.hasjay.reppido.report.dto.CreateReportRequest;
import com.hasjay.reppido.report.dto.ReportResponse;

public interface ReportService {
	ReportResponse createReport(CreateReportRequest request);
}
