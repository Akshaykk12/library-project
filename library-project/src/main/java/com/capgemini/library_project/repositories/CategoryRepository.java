package com.capgemini.library_project.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.capgemini.library_project.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{
	
    List<Category> findByCategoryName(String name);

    List<Category> findByCategoryNameContaining(String fragment);

    @Query("SELECT c FROM Category c WHERE LOWER(c.categoryDescription) LIKE LOWER(CONCAT('%',:kw,'%'))")
    List<Category> searchByDescription(@Param("kw") String keyword);

    @Query("SELECT c, COUNT(b) FROM Category c JOIN c.books b GROUP BY c")
    List<Object[]> countBooksPerCategory();

    @Query("SELECT DISTINCT c FROM Category c JOIN c.books b " +
           "WHERE b.availableCopies > 0")
    List<Category> findWithAvailableBooks();

    @Query("SELECT c FROM Category c LEFT JOIN c.books b " +
           "GROUP BY c ORDER BY COUNT(b) DESC")
    List<Category> findAllOrderByBookCountDesc();


}
