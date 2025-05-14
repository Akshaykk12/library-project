package com.capgemini.library_project.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.capgemini.library_project.entities.User;
import com.capgemini.library_project.repositories.UserRepository;

@Service
public class UserServicesImpl implements UserServices{
	
	private final UserRepository userRepository;
	private final String UPLOAD_DIR = "uploads/";

	@Autowired
	public UserServicesImpl(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public User getUserById(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User with id : " + userId + " not found."));
	}

	@Override
	public User createUser(User user) {
		return userRepository.save(user);
	}

	@Override
	public User updateUser(Long userId, User user) {
		User existing = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User with id : " + userId + " not found."));
		existing.setUserName(user.getUserName());
		existing.setUserEmail(user.getUserEmail());
		existing.setUserContact(user.getUserContact());
		return userRepository.save(existing);
	}

	@Override
	public boolean deleteUser(Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new RuntimeException("User with id : " + userId + " not found.");
		}
		userRepository.deleteById(userId);
		return true;
	}
	
	@Override
	public User updateImage(Long userId, MultipartFile image) throws IOException {
	    // Ensure the upload directory exists
	    Files.createDirectories(Paths.get(UPLOAD_DIR));

	    // Generate a unique file name
	    String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
	    Path filePath = Paths.get(UPLOAD_DIR, fileName);

	    // Save the uploaded image file
	    Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

	    // Find the existing user
	    User user = userRepository.findById(userId)
	                   .orElseThrow(() -> new RuntimeException("User with id " + userId + " not found"));

	    // Update the user's image path
	    user.setUserImage(fileName);

	    // Save and return the updated user
	    return userRepository.save(user);
	}

	@Override
	public User getImage(Long userid) {
		return userRepository.findById(userid).orElseThrow(() -> new RuntimeException());
	}
}