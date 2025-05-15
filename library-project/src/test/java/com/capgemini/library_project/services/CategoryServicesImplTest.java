package com.capgemini.library_project.services;

import com.capgemini.library_project.entities.Category;
import com.capgemini.library_project.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServicesImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServicesImpl categoryServices;

    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Fiction");
        category.setCategoryDescription("Books");
    }

    @Test
    void testGetAllCategory() {
        when(categoryRepository.findAll()).thenReturn(Collections.singletonList(category));
        List<Category> result = categoryServices.getAllCategory();
        assertEquals(1, result.size());
    }

    @Test
    void testGetCategoryById() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Category found = categoryServices.getCategoryById(1L);
        assertEquals("Fiction", found.getCategoryName());
    }

    @Test
    void testCreateCategory() {
        when(categoryRepository.save(category)).thenReturn(category);
        Category result = categoryServices.createCategory(category);
        assertEquals("Fiction", result.getCategoryName());
    }

    @Test
    void testUpdateCategoryById_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category updated = new Category();
        updated.setCategoryName("New Name");
        updated.setCategoryDescription("New Desc");

        Category result = categoryServices.updateCategoryById(1L, updated);
        assertEquals("New Name", result.getCategoryName());
    }

    @Test
    void testUpdateCategoryById_NotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        Category result = categoryServices.updateCategoryById(1L, category);
        assertNull(result);
    }

    @Test
    void testPatchCategory_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category patch = new Category();
        patch.setCategoryDescription("Only Description");

        Category result = categoryServices.patchCategory(1L, patch);
        assertEquals("Only Description", result.getCategoryDescription());
    }

    @Test
    void testPatchCategory_NotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        Category result = categoryServices.patchCategory(1L, category);
        assertNull(result);
    }

    @Test
    void testDeleteCategory_Success() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        boolean result = categoryServices.deleteCategory(1L);
        assertTrue(result);
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void testDeleteCategory_NotFound() {
        when(categoryRepository.existsById(1L)).thenReturn(false);
        boolean result = categoryServices.deleteCategory(1L);
        assertFalse(result);
    }
}
