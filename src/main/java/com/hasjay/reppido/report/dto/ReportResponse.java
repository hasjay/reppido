package com.hasjay.reppido.report.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReportResponse {

    private Integer id;
    private String category;
    private String description;
    private String location;
    private Double longitude;
    private Double latitude;
    private LocalDateTime createdOn;
}
