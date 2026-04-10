package com.hasjay.reppido.category.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hasjay.reppido.category.model.CategoryStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CategoryResponse {

    private Integer id;
    private String name;
    private CategoryStatus status;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private List<CategoryResponse> subcategories;
}
