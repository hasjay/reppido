package com.hasjay.reppido.report.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReportCommentResponse {

    private Integer id;
    private Integer reportId;
    private String comment;
    private LocalDateTime createdDate;
}
