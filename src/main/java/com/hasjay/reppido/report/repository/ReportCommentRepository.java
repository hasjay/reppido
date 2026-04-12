package com.hasjay.reppido.report.repository;

import com.hasjay.reppido.report.model.ReportComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportCommentRepository extends JpaRepository<ReportComment, Integer> {

    List<ReportComment> findByReportId(Integer reportId);
}
