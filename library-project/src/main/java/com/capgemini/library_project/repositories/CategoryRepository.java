package com.capgemini.library_project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capgemini.library_project.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{

}
