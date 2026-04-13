package com.hasjay.reppido.category.service;

import com.hasjay.reppido.category.dto.*;
import com.hasjay.reppido.category.model.Category;
import com.hasjay.reppido.category.model.CategoryStatus;
import com.hasjay.reppido.category.repository.CategoryRepository;
import com.hasjay.reppido.exception.CategoryNotFoundException;
import com.hasjay.reppido.exception.SubcategoryConstraintException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        Category main = Category.builder()
                .name(request.getName())
                .status(CategoryStatus.ACTIVE)
                .build();

        for (CategoryRequest subRequest : request.getSubcategories()) {
            Category sub = Category.builder()
                    .name(subRequest.getName())
                    .status(CategoryStatus.ACTIVE)
                    .mainCategory(main)
                    .build();
            main.getSubcategories().add(sub);
        }

        Category saved = categoryRepository.save(main);
        return toMainCategoryResponse(saved);
    }

    @Transactional
    public CategoryResponse updateCategory(Integer id, UpdateCategoryRequest request) {
        Category main = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (main.getMainCategory() != null) {
            throw new SubcategoryConstraintException("Cannot update a subcategory as a main category");
        }

        main.setName(request.getName());

        Set<Integer> existingIds = main.getSubcategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet());

        Set<Integer> incomingIds = request.getSubcategories().stream()
                .filter(s -> s.getId() != null)
                .map(CategoryRequest::getId)
                .collect(Collectors.toSet());

        // Validate all incoming IDs belong to this parent before making changes
        for (Integer incomingId : incomingIds) {
            if (!existingIds.contains(incomingId)) {
                throw new SubcategoryConstraintException(
                        "Subcategory id " + incomingId + " does not belong to category id " + id);
            }
        }

        // Remove subcategories not present in the incoming list
        main.getSubcategories().removeIf(existing -> !incomingIds.contains(existing.getId()));

        for (CategoryRequest subRequest : request.getSubcategories()) {
            if (subRequest.getId() == null) {
                Category newSub = Category.builder()
                        .name(subRequest.getName())
                        .status(CategoryStatus.ACTIVE)
                        .mainCategory(main)
                        .build();
                main.getSubcategories().add(newSub);
            } else {
                main.getSubcategories().stream()
                        .filter(s -> s.getId().equals(subRequest.getId()))
                        .findFirst()
                        .ifPresent(s -> s.setName(subRequest.getName()));
            }
        }

        Category saved = categoryRepository.save(main);
        return toMainCategoryResponse(saved);
    }

    @Transactional
    public void deleteCategory(Integer id) {
        Category main = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (main.getMainCategory() != null) {
            throw new SubcategoryConstraintException("Cannot delete a subcategory directly. Delete the main category instead.");
        }

        categoryRepository.delete(main);
    }

    public List<CategoryResponse> getActiveMainCategories() {
        return categoryRepository.findByMainCategoryIsNullAndStatus(CategoryStatus.ACTIVE)
                .stream()
                .map(this::toSubCategoryResponse).toList();
    }

    public List<CategoryResponse> getActiveSubcategories(Integer mainId) {
        categoryRepository.findById(mainId)
                .orElseThrow(() -> new CategoryNotFoundException(mainId));

        return categoryRepository.findByMainCategoryIdAndStatus(mainId, CategoryStatus.ACTIVE)
                .stream()
                .map(this::toSubCategoryResponse).toList();
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .filter(c -> c.getMainCategory() == null)
                .map(this::toMainCategoryResponse).toList();
    }

    @Transactional
    public CategoryResponse updateCategoryStatus(Integer id, CategoryStatusUpdateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        category.setStatus(request.getStatus());
        Category saved = categoryRepository.save(category);
        return toMainCategoryResponse(saved);
    }

    private CategoryResponse toMainCategoryResponse(Category category) {
        List<CategoryResponse> subs = category.getSubcategories()
                .stream()
                .map(this::toSubCategoryResponse).toList();

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .status(category.getStatus())
                .subcategories(subs)
                .build();
    }

    private CategoryResponse toSubCategoryResponse(Category sub) {
        return CategoryResponse.builder()
                .id(sub.getId())
                .name(sub.getName())
                .status(sub.getStatus())
                .build();
    }
}
