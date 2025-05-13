package com.capgemini.library_project.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capgemini.library_project.entities.Category;
import com.capgemini.library_project.repositories.CategoryRepository;

@Service
public class CategoryServicesImpl implements CategoryServices{
	
	CategoryRepository categoryRepository; 
	
	@Autowired
	public CategoryServicesImpl(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@Override
	public List<Category> getAllCategory() {
		return categoryRepository.findAll();
	}

	@Override
	public Category getCategoryById(Long categoryId) {
		return categoryRepository.findById(categoryId).orElseThrow(()-> new RuntimeException("Category with ID " + categoryId + " Not Found !!"));
	}

	@Override
	public Category createCategory(Category category) {
		return categoryRepository.save(category);
	}

	@Override
	public Category updateCategoryById(Long categoryId, Category updatedCategory) {
		Optional<Category> optional = categoryRepository.findById(categoryId);
		if(optional.isPresent()) {
			Category category = optional.get();
			category.setCategoryName(updatedCategory.getCategoryName());
			category.setCategoryDescription(updatedCategory.getCategoryDescription());
			return categoryRepository.save(category);
		}
		return null;
	}

	@Override
	public Category patchCategory(Long categoryId, Category patchedCategory) {
		Optional<Category> optional = categoryRepository.findById(categoryId);
		if(optional.isPresent()) {
			Category category = optional.get();
			if(patchedCategory.getCategoryName() != null) {
				category.setCategoryName(patchedCategory.getCategoryName());
			}
			if(patchedCategory.getCategoryDescription() != null) {
				category.setCategoryDescription(patchedCategory.getCategoryDescription());
			}
			return categoryRepository.save(category);
		}
		return null;
	}

	@Override
	public boolean deleteCategory(Long categoryId) {
		if(categoryRepository.existsById(categoryId)) {
			categoryRepository.deleteById(categoryId);
			return true;
		}
		return false;
	}


}
