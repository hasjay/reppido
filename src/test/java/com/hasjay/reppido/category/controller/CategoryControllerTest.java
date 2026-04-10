package com.hasjay.reppido.category.controller;

import com.hasjay.reppido.category.dto.*;
import com.hasjay.reppido.category.model.CategoryStatus;
import com.hasjay.reppido.category.service.CategoryService;
import com.hasjay.reppido.exception.CategoryNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    private CategoryResponse buildCategoryResponse() {
    	CategoryResponse sub = CategoryResponse.builder()
                .id(2).name("Pot Holes").status(CategoryStatus.ACTIVE).build();
        return CategoryResponse.builder()
                .id(1).name("Road").status(CategoryStatus.ACTIVE)
                .subcategories(List.of(sub))
                .build();
    }

    @Test
    void testCreateCategory_returns201() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest("Road",
                List.of(new CategoryRequest(null, "Pot Holes")));

        when(categoryService.createCategory(any(CreateCategoryRequest.class))).thenReturn(buildCategoryResponse());

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Road"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.subcategories[0].name").value("Pot Holes"));
    }

    @Test
    void testCreateCategory_blankName_returns400() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest("", new ArrayList<>());

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void testUpdateCategory_returns200() throws Exception {
        UpdateCategoryRequest request = new UpdateCategoryRequest("Roads", new ArrayList<>());

        when(categoryService.updateCategory(eq(1), any(UpdateCategoryRequest.class))).thenReturn(buildCategoryResponse());

        mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testUpdateCategory_blankName_returns400() throws Exception {
        UpdateCategoryRequest request = new UpdateCategoryRequest("", new ArrayList<>());

        mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void testDeleteCategory_returns204() throws Exception {
        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteCategory_notFound_returns404() throws Exception {
        doThrow(new CategoryNotFoundException(99)).when(categoryService).deleteCategory(99);

        mockMvc.perform(delete("/categories/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testGetActiveMainCategories_returns200() throws Exception {
        when(categoryService.getActiveMainCategories()).thenReturn(List.of(buildCategoryResponse()));

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Road"));
    }

    @Test
    void testGetActiveSubcategories_returns200() throws Exception {
    	CategoryResponse sub = CategoryResponse.builder()
                .id(2).name("Pot Holes").status(CategoryStatus.ACTIVE).build();

        when(categoryService.getActiveSubcategories(1)).thenReturn(List.of(sub));

        mockMvc.perform(get("/categories/1/subcategories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Pot Holes"));
    }

    @Test
    void testGetAllCategories_returns200() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(List.of(buildCategoryResponse()));

        mockMvc.perform(get("/categories/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testUpdateCategoryStatus_returns200() throws Exception {
        CategoryStatusUpdateRequest request = new CategoryStatusUpdateRequest(CategoryStatus.INACTIVE);
        CategoryResponse response = CategoryResponse.builder()
                .id(1).name("Road").status(CategoryStatus.INACTIVE).subcategories(new ArrayList<>()).build();

        when(categoryService.updateCategoryStatus(eq(1), any(CategoryStatusUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/categories/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void testUpdateCategoryStatus_nullStatus_returns400() throws Exception {
        CategoryStatusUpdateRequest request = new CategoryStatusUpdateRequest(null);

        mockMvc.perform(patch("/categories/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists());
    }
}
