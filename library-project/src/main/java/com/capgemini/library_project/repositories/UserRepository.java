package com.capgemini.library_project.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.capgemini.library_project.entities.User;

import jakarta.transaction.Transactional;

public interface UserRepository extends JpaRepository<User, Long>{

	@Modifying
	@Transactional
	@Query("UPDATE User u SET u.userImage = :image WHERE u.id = :userId")
	int updateImage(@Param("image") String image, @Param("userId") Long userId);
	
	Optional<User> findByUserEmail(String email);
	
	Optional<User> findByUserNameOrUserEmail(String username, String email);
	
	boolean existsByUserEmail(String email);
	
	boolean existsByUserName(String name);
	
	@Query(value = """
		    SELECT 
		        u.user_id, u.user_name, u.user_contact, 
		        b.book_title, c.category_name 
		    FROM user u
		    LEFT JOIN borrow_record br ON u.user_id = br.user_id
		    LEFT JOIN book b ON br.book_id = b.book_id
		    LEFT JOIN category c ON b.category_id = c.category_id
		    WHERE u.user_id = ?1
		    """, nativeQuery = true)
		List<Object[]> getBorrowedBooksForUser(Long id);
		
		@Query(value = """
			    SELECT 
			        u.user_id, u.user_name AS user_name, u.user_contact,
			        br.book_id, b.book_title, c.category_name, a.author_name
			    FROM user u
			    LEFT JOIN borrow_record br ON u.user_id = br.user_id
			    LEFT JOIN book b ON br.book_id = b.book_id
			    LEFT JOIN category c ON b.category_id = c.category_id
			    LEFT JOIN author a ON a.author_id = b.author_id
			    """, nativeQuery = true)
			List<Object[]> userBorrowRecords();


}