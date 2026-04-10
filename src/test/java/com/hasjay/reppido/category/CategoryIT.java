package com.hasjay.reppido.category;

import com.hasjay.reppido.category.dto.*;
import com.hasjay.reppido.category.model.Category;
import com.hasjay.reppido.category.model.CategoryStatus;
import com.hasjay.reppido.category.repository.CategoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CategoryIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @AfterEach
    void tearDown() {
        categoryRepository.deleteAll();
    }

    @Test
    void testCreateMainCategoryWithSubcategories_returns201() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest("Road",
                List.of(new CategoryRequest(null, "Pot Holes"), new CategoryRequest(null, "Cracks")));

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Road"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.subcategories.length()").value(2));

        assertEquals(3L, categoryRepository.count());
    }

    @Test
    void testCreateCategoryWithoutSubcategories_returns201() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest("Park", new ArrayList<>());

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Park"))
                .andExpect(jsonPath("$.subcategories.length()").value(0));

        assertEquals(1L, categoryRepository.count());
    }

    @Test
    void testCreateCategory_blankName_returns400() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest("", new ArrayList<>());

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());

        assertEquals(0L, categoryRepository.count());
    }

    @Test
    void testUpdateCategory_addsAndRemovesSubs_returns200() throws Exception {
        // Create main category with two subs
        CreateCategoryRequest createRequest = new CreateCategoryRequest("Road",
                List.of(new CategoryRequest(null, "Pot Holes"), new CategoryRequest(null, "Cracks")));

        MvcResult createResult = mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryResponse created = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), CategoryResponse.class);
        Integer mainId = created.getId();
        Integer subId = created.getSubcategories().get(0).getId();

        // Update: keep one sub, remove the other, add a new one
        UpdateCategoryRequest updateRequest = new UpdateCategoryRequest("Roads",
                List.of(new CategoryRequest(subId, "Pot Holes Updated"), new CategoryRequest(null, "Speed Bumps")));

        mockMvc.perform(put("/categories/" + mainId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Roads"))
                .andExpect(jsonPath("$.subcategories.length()").value(2));

        // Total: 1 main + 2 subs (one removed, one added)
        assertEquals(3L, categoryRepository.count());
    }

    @Test
    void testUpdateCategory_notFound_returns404() throws Exception {
        UpdateCategoryRequest request = new UpdateCategoryRequest("Road", new ArrayList<>());

        mockMvc.perform(put("/categories/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testDeleteCategory_returns204AndRemovesFromDb() throws Exception {
        CreateCategoryRequest createRequest = new CreateCategoryRequest("Road",
                List.of(new CategoryRequest(null, "Pot Holes")));

        MvcResult result = mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryResponse created = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryResponse.class);

        mockMvc.perform(delete("/categories/" + created.getId()))
                .andExpect(status().isNoContent());

        assertEquals(0L, categoryRepository.count());
    }

    @Test
    void testDeleteCategory_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/categories/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testGetActiveMainCategories_returnsOnlyActive() throws Exception {
        // Create ACTIVE category
        CreateCategoryRequest activeRequest = new CreateCategoryRequest("Road", new ArrayList<>());
        MvcResult result = mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(activeRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        CategoryResponse active = objectMapper.readValue(result.getResponse().getContentAsString(), CategoryResponse.class);

        // Set INACTIVE via patch
        CreateCategoryRequest inactiveRequest = new CreateCategoryRequest("Park", new ArrayList<>());
        MvcResult result2 = mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inactiveRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        CategoryResponse inactive = objectMapper.readValue(result2.getResponse().getContentAsString(), CategoryResponse.class);

        mockMvc.perform(patch("/categories/" + inactive.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoryStatusUpdateRequest(CategoryStatus.INACTIVE))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Road"));
    }

    @Test
    void testGetActiveSubcategories_returnsOnlyActiveSubs() throws Exception {
        CreateCategoryRequest createRequest = new CreateCategoryRequest("Road",
                List.of(new CategoryRequest(null, "Pot Holes"), new CategoryRequest(null, "Cracks")));

        MvcResult result = mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryResponse created = objectMapper.readValue(result.getResponse().getContentAsString(), CategoryResponse.class);
        Integer mainId = created.getId();
        Integer subId = created.getSubcategories().get(0).getId();

        // Deactivate one sub
        mockMvc.perform(patch("/categories/" + subId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoryStatusUpdateRequest(CategoryStatus.INACTIVE))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/categories/" + mainId + "/subcategories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetAllCategories_returnsAllStatuses() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest("Road",
                List.of(new CategoryRequest(null, "Pot Holes")));

        MvcResult result = mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryResponse created = objectMapper.readValue(result.getResponse().getContentAsString(), CategoryResponse.class);

        // Deactivate main category
        mockMvc.perform(patch("/categories/" + created.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoryStatusUpdateRequest(CategoryStatus.INACTIVE))))
                .andExpect(status().isOk());

        // GET /all should still return the category
        mockMvc.perform(get("/categories/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("INACTIVE"));
    }

    @Test
    void testUpdateCategoryStatus_persistsNewStatus() throws Exception {
        CreateCategoryRequest createRequest = new CreateCategoryRequest("Road", new ArrayList<>());

        MvcResult result = mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryResponse created = objectMapper.readValue(result.getResponse().getContentAsString(), CategoryResponse.class);

        mockMvc.perform(patch("/categories/" + created.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoryStatusUpdateRequest(CategoryStatus.INACTIVE))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));

        Category found = categoryRepository.findById(created.getId()).orElseThrow();
        assertEquals(CategoryStatus.INACTIVE, found.getStatus());
    }
}
