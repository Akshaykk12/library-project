package com.capgemini.library_project.services;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.capgemini.library_project.entities.User;


public interface UserServices {

	List<User> getAllUsers();

	User getUserById(Long userId);

	User createUser(User user);

	User updateUser(Long userId, User user);

	boolean deleteUser(Long userId);
	
	User updateImage(Long userId, MultipartFile image) throws IOException;
	
	public User getImage(Long userId);

	User findByUserNameOrUserEmail(String name, String email);

	boolean existsByUserEmail(String email);
	
	boolean existsByUserName(String name);
}
