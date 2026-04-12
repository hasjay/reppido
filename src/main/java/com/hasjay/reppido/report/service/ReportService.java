package com.hasjay.reppido.report.service;


import java.util.List;

import com.hasjay.reppido.report.dto.CreateReportRequest;
import com.hasjay.reppido.report.dto.ReportResponse;

public interface ReportService {
	ReportResponse createReport(CreateReportRequest request);
	
	List<ReportResponse> getReports();
}
