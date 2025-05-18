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
	
	List<Category> getByCategoryName(String name);

    List<Category> getByCategoryNameContaining(String fragment);

    List<Category> searchByDescription(String keyword);

    List<Object[]> countBooksPerCategory();

    List<Category> findWithAvailableBooks();

    List<Category> getAllOrderByBookCountDesc();



}
