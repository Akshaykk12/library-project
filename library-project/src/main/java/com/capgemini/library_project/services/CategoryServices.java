package com.capgemini.library_project.services;

import java.util.List;

import com.capgemini.library_project.entities.Category;

public interface CategoryServices {
	
	List<Category> getAllCategory();
	
	Category getCategoryById(Long categoryId);
	
	Category createCategory(Category category);
	
	Category updateCategoryById(Long categoryId, Category category);
	
	Category patchCategory(Long categoryId, Category category);
	
	boolean deleteCategory(Long categoryId);


}
