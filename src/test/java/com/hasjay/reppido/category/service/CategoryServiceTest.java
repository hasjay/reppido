package com.hasjay.reppido.category.service;

import com.hasjay.reppido.category.dto.*;
import com.hasjay.reppido.category.model.Category;
import com.hasjay.reppido.category.model.CategoryStatus;
import com.hasjay.reppido.category.repository.CategoryRepository;
import com.hasjay.reppido.exception.CategoryNotFoundException;
import com.hasjay.reppido.exception.SubcategoryConstraintException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void testCreateCategory_withSubcategories() {
        CategoryRequest subRequest = new CategoryRequest(null, "Pot Holes");
        CreateCategoryRequest request = new CreateCategoryRequest("Road", List.of(subRequest));

        Category savedSub = Category.builder().id(2).name("Pot Holes").status(CategoryStatus.ACTIVE).build();
        Category savedMain = Category.builder()
                .id(1).name("Road").status(CategoryStatus.ACTIVE)
                .subcategories(new ArrayList<>(List.of(savedSub)))
                .build();

        when(categoryRepository.save(any(Category.class))).thenReturn(savedMain);

        CategoryResponse response = categoryService.createCategory(request);

        verify(categoryRepository).save(any(Category.class));
        assertEquals(1, response.getId());
        assertEquals("Road", response.getName());
        assertEquals(CategoryStatus.ACTIVE, response.getStatus());
        assertEquals(1, response.getSubcategories().size());
        assertEquals("Pot Holes", response.getSubcategories().get(0).getName());
    }

    @Test
    void testCreateCategory_withoutSubcategories() {
        CreateCategoryRequest request = new CreateCategoryRequest("Road", new ArrayList<>());

        Category savedMain = Category.builder()
                .id(1).name("Road").status(CategoryStatus.ACTIVE)
                .subcategories(new ArrayList<>())
                .build();

        when(categoryRepository.save(any(Category.class))).thenReturn(savedMain);

        CategoryResponse response = categoryService.createCategory(request);

        assertEquals("Road", response.getName());
        assertTrue(response.getSubcategories().isEmpty());
    }

    @Test
    void testUpdateCategory_updatesNameAndAddsSub() {
        Category existingSub = Category.builder().id(2).name("Pot Holes").status(CategoryStatus.ACTIVE).build();
        Category main = Category.builder()
                .id(1).name("Road").status(CategoryStatus.ACTIVE)
                .subcategories(new ArrayList<>(List.of(existingSub)))
                .build();
        existingSub.setMainCategory(main);

        when(categoryRepository.findById(1)).thenReturn(Optional.of(main));
        when(categoryRepository.save(any(Category.class))).thenReturn(main);

        UpdateCategoryRequest request = new UpdateCategoryRequest("Roads",
                List.of(new CategoryRequest(2, "Pot Holes"), new CategoryRequest(null, "Cracks")));

        CategoryResponse response = categoryService.updateCategory(1, request);

        verify(categoryRepository).save(main);
        assertEquals("Roads", response.getName());
    }

    @Test
    void testUpdateCategory_removesSub() {
        Category sub1 = Category.builder().id(2).name("Pot Holes").status(CategoryStatus.ACTIVE).build();
        Category sub2 = Category.builder().id(3).name("Cracks").status(CategoryStatus.ACTIVE).build();
        Category main = Category.builder()
                .id(1).name("Road").status(CategoryStatus.ACTIVE)
                .subcategories(new ArrayList<>(List.of(sub1, sub2)))
                .build();

        when(categoryRepository.findById(1)).thenReturn(Optional.of(main));
        when(categoryRepository.save(any(Category.class))).thenReturn(main);

        UpdateCategoryRequest request = new UpdateCategoryRequest("Road",
                List.of(new CategoryRequest(2, "Pot Holes")));

        categoryService.updateCategory(1, request);

        assertEquals(1, main.getSubcategories().size());
        assertEquals("Pot Holes", main.getSubcategories().get(0).getName());
    }

    @Test
    void testUpdateCategory_throwsWhenSubIdNotBelongToParent() {
        Category main = Category.builder()
                .id(1).name("Road").status(CategoryStatus.ACTIVE)
                .subcategories(new ArrayList<>())
                .build();

        when(categoryRepository.findById(1)).thenReturn(Optional.of(main));

        UpdateCategoryRequest request = new UpdateCategoryRequest("Road",
                List.of(new CategoryRequest(99, "Unknown")));

        assertThrows(SubcategoryConstraintException.class, () -> categoryService.updateCategory(1, request));
    }

    @Test
    void testUpdateCategory_throwsWhenCategoryNotFound() {
        when(categoryRepository.findById(99)).thenReturn(Optional.empty());

        UpdateCategoryRequest request = new UpdateCategoryRequest("Road", new ArrayList<>());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(99, request));
    }

    @Test
    void testUpdateCategory_throwsWhenUpdatingSubcategoryAsMain() {
        Category parent = Category.builder().id(1).name("Road").status(CategoryStatus.ACTIVE).build();
        Category sub = Category.builder()
                .id(2).name("Pot Holes").status(CategoryStatus.ACTIVE)
                .mainCategory(parent)
                .subcategories(new ArrayList<>())
                .build();

        when(categoryRepository.findById(2)).thenReturn(Optional.of(sub));

        UpdateCategoryRequest request = new UpdateCategoryRequest("New Name", new ArrayList<>());

        assertThrows(SubcategoryConstraintException.class, () -> categoryService.updateCategory(2, request));
    }

    @Test
    void testDeleteCategory_callsRepositoryDelete() {
        Category main = Category.builder()
                .id(1).name("Road").status(CategoryStatus.ACTIVE)
                .subcategories(new ArrayList<>())
                .build();

        when(categoryRepository.findById(1)).thenReturn(Optional.of(main));

        categoryService.deleteCategory(1);

        verify(categoryRepository).delete(main);
    }

    @Test
    void testDeleteCategory_throwsWhenNotFound() {
        when(categoryRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(99));
    }

    @Test
    void testDeleteCategory_throwsWhenDeletingSubcategory() {
        Category parent = Category.builder().id(1).name("Road").status(CategoryStatus.ACTIVE).build();
        Category sub = Category.builder()
                .id(2).name("Pot Holes").status(CategoryStatus.ACTIVE)
                .mainCategory(parent)
                .build();

        when(categoryRepository.findById(2)).thenReturn(Optional.of(sub));

        assertThrows(SubcategoryConstraintException.class, () -> categoryService.deleteCategory(2));
    }

    @Test
    void testGetActiveMainCategories() {
        Category main = Category.builder()
                .id(1).name("Road").status(CategoryStatus.ACTIVE)
                .subcategories(new ArrayList<>())
                .build();

        when(categoryRepository.findByMainCategoryIsNullAndStatus(CategoryStatus.ACTIVE))
                .thenReturn(List.of(main));

        List<CategoryResponse> result = categoryService.getActiveMainCategories();

        assertEquals(1, result.size());
        assertEquals("Road", result.get(0).getName());
    }

    @Test
    void testGetActiveSubcategories_throwsWhenParentNotFound() {
        when(categoryRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.getActiveSubcategories(99));
    }

    @Test
    void testGetActiveSubcategories_returnsActiveSubs() {
        Category main = Category.builder().id(1).name("Road").status(CategoryStatus.ACTIVE).build();
        Category sub = Category.builder().id(2).name("Pot Holes").status(CategoryStatus.ACTIVE).mainCategory(main).build();

        when(categoryRepository.findById(1)).thenReturn(Optional.of(main));
        when(categoryRepository.findByMainCategoryIdAndStatus(1, CategoryStatus.ACTIVE)).thenReturn(List.of(sub));

        List<CategoryResponse> result = categoryService.getActiveSubcategories(1);

        assertEquals(1, result.size());
        assertEquals("Pot Holes", result.get(0).getName());
    }

    @Test
    void testGetAllCategories_returnsMainCategoriesOnly() {
        Category sub = Category.builder().id(2).name("Pot Holes").status(CategoryStatus.ACTIVE).build();
        Category main = Category.builder()
                .id(1).name("Road").status(CategoryStatus.ACTIVE)
                .subcategories(new ArrayList<>(List.of(sub)))
                .build();
        sub.setMainCategory(main);

        when(categoryRepository.findAll()).thenReturn(List.of(main, sub));

        List<CategoryResponse> result = categoryService.getAllCategories();

        assertEquals(1, result.size());
        assertEquals("Road", result.get(0).getName());
        assertEquals(1, result.get(0).getSubcategories().size());
    }

    @Test
    void testUpdateCategoryStatus_updatesAndReturnsResponse() {
        Category category = Category.builder()
                .id(1).name("Road").status(CategoryStatus.ACTIVE)
                .subcategories(new ArrayList<>())
                .build();

        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryStatusUpdateRequest request = new CategoryStatusUpdateRequest(CategoryStatus.INACTIVE);
        CategoryResponse response = categoryService.updateCategoryStatus(1, request);

        assertEquals(CategoryStatus.INACTIVE, response.getStatus());
        verify(categoryRepository).save(category);
    }

    @Test
    void testUpdateCategoryStatus_throwsWhenNotFound() {
        when(categoryRepository.findById(99)).thenReturn(Optional.empty());

        CategoryStatusUpdateRequest request = new CategoryStatusUpdateRequest(CategoryStatus.INACTIVE);

        assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategoryStatus(99, request));
    }
}
