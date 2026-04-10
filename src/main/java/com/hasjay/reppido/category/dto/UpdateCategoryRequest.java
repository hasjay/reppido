package com.hasjay.reppido.category.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCategoryRequest {

    @NotBlank(message = "Category name is required")
    private String name;

    @Valid
    private List<CategoryRequest> subcategories = new ArrayList<>();
}
