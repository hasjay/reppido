package com.hasjay.reppido.category.repository;

import com.hasjay.reppido.category.model.Category;
import com.hasjay.reppido.category.model.CategoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    List<Category> findByMainCategoryIsNullAndStatus(CategoryStatus status);

    List<Category> findByMainCategoryIdAndStatus(Integer mainCategoryId, CategoryStatus status);

    boolean existsByIdAndMainCategoryId(Integer id, Integer mainCategoryId);
}
