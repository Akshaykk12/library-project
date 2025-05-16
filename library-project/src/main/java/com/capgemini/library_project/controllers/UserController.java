package com.capgemini.library_project.controllers;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.capgemini.library_project.entities.User;
import com.capgemini.library_project.repositories.UserRepository;
import com.capgemini.library_project.services.UserServices;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	private final UserServices userServices;
	private final UserRepository userRepository;

	@Autowired
	public UserController(UserServices userServices, UserRepository userRepository) {
		this.userServices = userServices;
		this.userRepository = userRepository;
	}

	@GetMapping
	public ResponseEntity<List<User>> getAllUsers() {
		logger.info("Received request to get all users");
		List<User> users = userServices.getAllUsers();
		return ResponseEntity.ok(users);
	}

	@GetMapping("/{userId}")
	public ResponseEntity<User> getUserById(@PathVariable Long userId) {
		logger.info("Received request to get user by ID: {}", userId);
		User user = userServices.getUserById(userId);
		return ResponseEntity.ok(user);
	}

	@PostMapping
	public ResponseEntity<User> createUser(@Valid @RequestBody User user, BindingResult bindingResult) {
		logger.info("Received request to create user: {}", user.getUserName());
		if (bindingResult.hasErrors()) {
			throw new IllegalArgumentException("Invalid Data");
		}
		User savedUser = userServices.createUser(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
	}

	@PutMapping("/{userId}")
	public ResponseEntity<User> updateUser(@PathVariable Long userId, @Valid @RequestBody User user,
			BindingResult bindingResult) {
		logger.info("Received request to update user ID: {}", userId);
		if (bindingResult.hasErrors()) {
			throw new IllegalArgumentException("Invalid Data");
		}
		User updatedUser = userServices.updateUser(userId, user);
		return ResponseEntity.ok(updatedUser);
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
		logger.info("Received request to delete user ID: {}", userId);
		userServices.deleteUser(userId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<User> uploadImage(@RequestParam Long userId, @RequestParam MultipartFile image)
			throws IOException {
		logger.info("Received image upload for user ID: {}", userId);
		User updatedUser = userServices.updateImage(userId, image);
		return ResponseEntity.ok(updatedUser);
	}

	@GetMapping("/images/{image}")
	public ResponseEntity<Resource> getImage(@PathVariable String image) throws IOException {
		logger.info("Request to get image: {}", image);
		java.nio.file.Path filePath = Paths.get("uploads").resolve(image).normalize();
		Resource resource = new UrlResource(filePath.toUri());

		if (resource.exists() && resource.isReadable()) {
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
		} else {
			logger.warn("Image file {} not found", image);
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{userId}/remove-image")
	public ResponseEntity<User> deleteProfileImage(@PathVariable Long userId) {
		logger.info("Request to remove profile image for user ID: {}", userId);
		User user = userRepository.findById(userId).orElseThrow();
		user.setUserImage(null);
		userRepository.save(user);
		return ResponseEntity.ok().build();
	}
}
