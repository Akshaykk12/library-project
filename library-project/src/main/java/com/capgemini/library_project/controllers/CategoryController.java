package com.capgemini.library_project.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.library_project.entities.Category;
import com.capgemini.library_project.services.CategoryServices;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

	private CategoryServices categoryServices;

	@Autowired 
	public CategoryController(CategoryServices categoryServices) {
		this.categoryServices = categoryServices;
	}
	
	@GetMapping
	public ResponseEntity<List<Category>> getAllCategory(){
		return ResponseEntity.status(HttpStatus.OK).body(categoryServices.getAllCategory());
	}
	
	@GetMapping("/{categoryId}")
	public ResponseEntity<Category> getCategoryById(@PathVariable("categoryId") Long categoryId){
		return ResponseEntity.status(HttpStatus.OK).body(categoryServices.getCategoryById(categoryId));
	}
	
	@PostMapping
	public ResponseEntity<Category> createCategory(@RequestBody Category category){
		return ResponseEntity.status(HttpStatus.OK).body(categoryServices.createCategory(category));
	}
	
	@PutMapping("{categoryId}")
	public ResponseEntity<Category> updateCategory(@PathVariable("categoryId") Long categoryId, @RequestBody Category updatedCategory){
		Category category = categoryServices.updateCategoryById(categoryId, updatedCategory);
		if(category == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(HttpStatus.OK).body(category);
	}
	
	@PatchMapping("{categoryId}")
	public ResponseEntity<Category> patchCategory(@PathVariable("categoryId") Long categoryId, @RequestBody Category patchedCategory){
		Category category = categoryServices.patchCategory(categoryId, patchedCategory);
		if(category != null) {
			return ResponseEntity.status(HttpStatus.OK).body(category);
		}
		else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	@DeleteMapping("{categoryId}")
	public ResponseEntity<Category> deleteCategory(@PathVariable("categoryId") Long categoryId){
		boolean deleted = categoryServices.deleteCategory(categoryId);
		if(deleted) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
}
