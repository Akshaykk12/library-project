package com.capgemini.library_project.controllers;

import com.capgemini.library_project.entities.Category;
import com.capgemini.library_project.services.CategoryServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CategoryControllerTest {

	@Mock
	private CategoryServices categoryServices;

	@InjectMocks
	private CategoryController categoryController;

	private Category category;

	@Mock
	private BindingResult bindingResult;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		category = new Category();
		category.setCategoryId(1L);
		category.setCategoryName("Fiction");
	}

	@Test
	void testGetAllCategory() {
		when(categoryServices.getAllCategory()).thenReturn(Arrays.asList(category));
		ResponseEntity<List<Category>> response = categoryController.getAllCategory();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(1, response.getBody().size());
	}

	@Test
	void testGetCategoryById() {
		when(categoryServices.getCategoryById(1L)).thenReturn(category);
		ResponseEntity<Category> response = categoryController.getCategoryById(1L);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void testCreateCategory() {
		when(bindingResult.hasErrors()).thenReturn(false);
		when(categoryServices.createCategory(category)).thenReturn(category);
		ResponseEntity<Category> response = categoryController.createCategory(category, bindingResult);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void testUpdateCategory_Success() {
		when(bindingResult.hasErrors()).thenReturn(false);
		when(categoryServices.updateCategoryById(1L, category)).thenReturn(category);
		ResponseEntity<Category> response = categoryController.updateCategory(1L, category, bindingResult);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void testUpdateCategory_NotFound() {
		when(bindingResult.hasErrors()).thenReturn(false);
		when(categoryServices.updateCategoryById(1L, category)).thenReturn(null);
		ResponseEntity<Category> response = categoryController.updateCategory(1L, category, bindingResult);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	void testPatchCategory_Success() {
		when(bindingResult.hasErrors()).thenReturn(false);
		when(categoryServices.patchCategory(1L, category)).thenReturn(category);
		ResponseEntity<Category> response = categoryController.patchCategory(1L, category, bindingResult);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void testPatchCategory_NotFound() {
		when(bindingResult.hasErrors()).thenReturn(false);
		when(categoryServices.patchCategory(1L, category)).thenReturn(null);
		ResponseEntity<Category> response = categoryController.patchCategory(1L, category, bindingResult);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	void testDeleteCategory_Success() {
		when(categoryServices.deleteCategory(1L)).thenReturn(true);
		ResponseEntity<Category> response = categoryController.deleteCategory(1L);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}

	@Test
	void testDeleteCategory_NotFound() {
		when(categoryServices.deleteCategory(1L)).thenReturn(false);
		ResponseEntity<Category> response = categoryController.deleteCategory(1L);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
}
