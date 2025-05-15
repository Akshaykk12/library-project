package com.capgemini.library_project.controllers;

import java.util.List;

import com.capgemini.library_project.entities.Category;
import com.capgemini.library_project.services.CategoryServices;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryServices categoryServices;

    @Autowired
    public CategoryController(CategoryServices categoryServices) {
        this.categoryServices = categoryServices;
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategory() {
        logger.info("GET request received: fetch all categories");
        return ResponseEntity.ok(categoryServices.getAllCategory());
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long categoryId) {
        logger.info("GET request received: fetch category with ID {}", categoryId);
        return ResponseEntity.ok(categoryServices.getCategoryById(categoryId));
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        logger.info("POST request received: create category {}", category.getCategoryName());
        return ResponseEntity.ok(categoryServices.createCategory(category));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long categoryId, @RequestBody Category updatedCategory) {
        logger.info("PUT request received: update category with ID {}", categoryId);
        Category category = categoryServices.updateCategoryById(categoryId, updatedCategory);
        if (category == null) {
            logger.warn("Category with ID {} not found for update", categoryId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(category);
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<Category> patchCategory(@PathVariable Long categoryId, @RequestBody Category patchedCategory) {
        logger.info("PATCH request received: patch category with ID {}", categoryId);
        Category category = categoryServices.patchCategory(categoryId, patchedCategory);
        if (category != null) {
            return ResponseEntity.ok(category);
        } else {
            logger.warn("Category with ID {} not found for patching", categoryId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Category> deleteCategory(@PathVariable Long categoryId) {
        logger.info("DELETE request received: delete category with ID {}", categoryId);
        boolean deleted = categoryServices.deleteCategory(categoryId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            logger.warn("Category with ID {} not found for deletion", categoryId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
