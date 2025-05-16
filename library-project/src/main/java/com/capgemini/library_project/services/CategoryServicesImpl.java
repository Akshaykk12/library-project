package com.capgemini.library_project.services;

import java.util.List;
import java.util.Optional;

import com.capgemini.library_project.entities.Category;
import com.capgemini.library_project.exceptions.CategoryNotFoundException;
import com.capgemini.library_project.repositories.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServicesImpl implements CategoryServices {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServicesImpl.class);

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServicesImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllCategory() {
        logger.info("Fetching all categories");
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        logger.info("Fetching category by ID: {}", categoryId);
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    logger.error("Category with ID {} not found", categoryId);
                    return  new CategoryNotFoundException("Category with ID " + categoryId + " Not Found !!");
                });
    }

    @Override
    public Category createCategory(Category category) {
        logger.info("Creating new category: {}", category.getCategoryName());
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategoryById(Long categoryId, Category updatedCategory) {
        logger.info("Updating category with ID: {}", categoryId);
        Optional<Category> optional = categoryRepository.findById(categoryId);
        		
        if (optional.isPresent()) {
            Category category = optional.get();
            category.setCategoryName(updatedCategory.getCategoryName());
            category.setCategoryDescription(updatedCategory.getCategoryDescription());
            return categoryRepository.save(category);
        } else {
            logger.warn("Category with ID {} not found for update", categoryId);
            return null;
        }
    }

    @Override
    public Category patchCategory(Long categoryId, Category patchedCategory) {
        logger.info("Patching category with ID: {}", categoryId);
        Optional<Category> optional = categoryRepository.findById(categoryId);
        if (optional.isPresent()) {
            Category category = optional.get();
            if (patchedCategory.getCategoryName() != null) {
                category.setCategoryName(patchedCategory.getCategoryName());
            }
            if (patchedCategory.getCategoryDescription() != null) {
                category.setCategoryDescription(patchedCategory.getCategoryDescription());
            }
            return categoryRepository.save(category);
        } else {
            logger.warn("Category with ID {} not found for patching", categoryId);
            return null;
        }
    }

    @Override
    public boolean deleteCategory(Long categoryId) {
        logger.info("Deleting category with ID: {}", categoryId);
        if (categoryRepository.existsById(categoryId)) {
            categoryRepository.deleteById(categoryId);
            logger.info("Category with ID {} successfully deleted", categoryId);
            return true;
        } else {
            logger.warn("Category with ID {} not found for deletion", categoryId);
            return false;
        }
    }
}
