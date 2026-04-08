package com.hasjay.reppido.report.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportRequest {

    @NotBlank(message = "Category is required")
    private String category;

    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    @NotNull(message = "Latitude is required")
    private Double latitude;
}
