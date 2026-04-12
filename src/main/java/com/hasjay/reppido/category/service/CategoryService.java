package com.hasjay.reppido.category.service;

import java.util.List;

import com.hasjay.reppido.category.dto.CategoryResponse;
import com.hasjay.reppido.category.dto.CategoryStatusUpdateRequest;
import com.hasjay.reppido.category.dto.CreateCategoryRequest;
import com.hasjay.reppido.category.dto.UpdateCategoryRequest;

public interface CategoryService {
	CategoryResponse createCategory(CreateCategoryRequest request);
	
	CategoryResponse updateCategory(Integer id, UpdateCategoryRequest request);
	
	void deleteCategory(Integer id);
	
	List<CategoryResponse> getActiveMainCategories();
	
	List<CategoryResponse> getActiveSubcategories(Integer mainId);
	
	List<CategoryResponse> getAllCategories();
	
	CategoryResponse updateCategoryStatus(Integer id, CategoryStatusUpdateRequest request);
}
