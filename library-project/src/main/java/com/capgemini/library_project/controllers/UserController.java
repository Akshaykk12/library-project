package com.capgemini.library_project.controllers;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.capgemini.library_project.entities.User;
import com.capgemini.library_project.repositories.UserRepository;
import com.capgemini.library_project.services.UserServices;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	private final UserServices userServices;
	private final UserRepository userRepository;

	@Autowired
	public UserController(UserServices userServices, UserRepository userRepository) {
		super();
		this.userServices = userServices;
		this.userRepository = userRepository;
	}

	@GetMapping
	public ResponseEntity<List<User>> getAllUsers() {
		List<User> user = userServices.getAllUsers();
		return ResponseEntity.status(HttpStatus.OK).body(user);
	}

	@GetMapping("/{userId}")
	public ResponseEntity<User> getUserById(@PathVariable("userId") Long userId) {
		User user = userServices.getUserById(userId);
		if (user != null) {
			return ResponseEntity.status(HttpStatus.OK).body(user);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@PostMapping
	public ResponseEntity<User> createUser(@RequestBody User user) {
		User saved = userServices.createUser(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}

	@PutMapping("/{userId}")
	public ResponseEntity<User> updateUser(@PathVariable("userId") Long userId, @RequestBody User user) {
		User updated = userServices.updateUser(userId, user);
		if (updated != null) {
			return ResponseEntity.status(HttpStatus.OK).body(updated);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
		boolean deleted = userServices.deleteUser(userId);
		if (deleted) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/profile")
	public ResponseEntity<User> uploadImage(@RequestParam Long userId, @RequestParam MultipartFile image) throws IOException{
		User saved = userServices.updateImage(userId, image);
		return ResponseEntity.status(HttpStatus.OK).body(saved);
	}
	
	@GetMapping("/images/{image}")
	public ResponseEntity<Resource> getImage(@PathVariable String image) throws IOException {
	    java.nio.file.Path filePath = Paths.get("uploads").resolve(image).normalize();
	    Resource resource = new UrlResource(filePath.toUri());

	    if (resource.exists() && resource.isReadable()) {
	        return ResponseEntity.ok()
	            .contentType(MediaType.IMAGE_JPEG)
	            .body(resource);
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}
	
	@DeleteMapping("/{userId}/remove-image")
	public ResponseEntity<User> deleteProfileImage(@PathVariable Long userId) {
	    User user = userRepository.findById(userId).orElseThrow();
	    user.setUserImage(null);
	    userRepository.save(user);
	    return ResponseEntity.ok().build();  
	}
	
}

