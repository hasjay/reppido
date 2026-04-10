package com.hasjay.reppido.category.repository;

import com.hasjay.reppido.category.model.Category;
import com.hasjay.reppido.category.model.CategoryStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void testSaveMainCategory() {
        Category main = Category.builder()
                .name("Road")
                .status(CategoryStatus.ACTIVE)
                .build();

        Category saved = categoryRepository.save(main);

        assertNotNull(saved.getId());
        assertEquals("Road", saved.getName());
        assertEquals(CategoryStatus.ACTIVE, saved.getStatus());
        assertNull(saved.getMainCategory());
    }

    @Test
    void testSaveSubcategory() {
        Category main = Category.builder()
                .name("Road")
                .status(CategoryStatus.ACTIVE)
                .build();
        Category savedMain = categoryRepository.save(main);

        Category sub = Category.builder()
                .name("Pot Holes")
                .status(CategoryStatus.ACTIVE)
                .mainCategory(savedMain)
                .build();
        Category savedSub = categoryRepository.save(sub);

        assertNotNull(savedSub.getId());
        assertEquals("Pot Holes", savedSub.getName());
        assertEquals(savedMain.getId(), savedSub.getMainCategory().getId());
    }

    @Test
    void testFindByMainCategoryIsNullAndStatus_returnsOnlyActiveMain() {
        Category active = Category.builder().name("Road").status(CategoryStatus.ACTIVE).build();
        Category inactive = Category.builder().name("Park").status(CategoryStatus.INACTIVE).build();
        categoryRepository.save(active);
        categoryRepository.save(inactive);

        List<Category> result = categoryRepository.findByMainCategoryIsNullAndStatus(CategoryStatus.ACTIVE);

        assertEquals(1, result.size());
        assertEquals("Road", result.get(0).getName());
    }

    @Test
    void testFindByMainCategoryIdAndStatus_returnsOnlyActiveSubs() {
        Category main = categoryRepository.save(
                Category.builder().name("Road").status(CategoryStatus.ACTIVE).build());

        Category activeSub = Category.builder()
                .name("Pot Holes").status(CategoryStatus.ACTIVE).mainCategory(main).build();
        Category inactiveSub = Category.builder()
                .name("Cracks").status(CategoryStatus.INACTIVE).mainCategory(main).build();
        categoryRepository.save(activeSub);
        categoryRepository.save(inactiveSub);

        List<Category> result = categoryRepository.findByMainCategoryIdAndStatus(main.getId(), CategoryStatus.ACTIVE);

        assertEquals(1, result.size());
        assertEquals("Pot Holes", result.get(0).getName());
    }

    @Test
    void testDeleteMainCategory_cascadesToSubcategories() {
        Category main = Category.builder().name("Road").status(CategoryStatus.ACTIVE).build();

        Category sub1 = Category.builder()
                .name("Pot Holes").status(CategoryStatus.ACTIVE).mainCategory(main).build();
        Category sub2 = Category.builder()
                .name("Cracks").status(CategoryStatus.ACTIVE).mainCategory(main).build();
        main.getSubcategories().add(sub1);
        main.getSubcategories().add(sub2);

        Category saved = categoryRepository.save(main);
        assertEquals(2, categoryRepository.findByMainCategoryIdAndStatus(saved.getId(), CategoryStatus.ACTIVE).size());

        categoryRepository.delete(saved);

        assertEquals(0, categoryRepository.findAll().size());
    }

    @Test
    void testExistsByIdAndMainCategoryId_returnsTrueForValidPair() {
        Category main = categoryRepository.save(
                Category.builder().name("Road").status(CategoryStatus.ACTIVE).build());
        Category sub = categoryRepository.save(
                Category.builder().name("Pot Holes").status(CategoryStatus.ACTIVE).mainCategory(main).build());

        assertTrue(categoryRepository.existsByIdAndMainCategoryId(sub.getId(), main.getId()));
    }

    @Test
    void testExistsByIdAndMainCategoryId_returnsFalseForWrongParent() {
        Category main1 = categoryRepository.save(
                Category.builder().name("Road").status(CategoryStatus.ACTIVE).build());
        Category main2 = categoryRepository.save(
                Category.builder().name("Park").status(CategoryStatus.ACTIVE).build());
        Category sub = categoryRepository.save(
                Category.builder().name("Pot Holes").status(CategoryStatus.ACTIVE).mainCategory(main1).build());

        assertFalse(categoryRepository.existsByIdAndMainCategoryId(sub.getId(), main2.getId()));
    }
}
