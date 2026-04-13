package com.hasjay.reppido.report.repository;

import com.hasjay.reppido.report.model.Report;


import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Integer> , CustomReportRepository{

}
