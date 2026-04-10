package com.hasjay.reppido.category.dto;

import com.hasjay.reppido.category.model.CategoryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private CategoryStatus status;
}
