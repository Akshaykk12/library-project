package com.capgemini.library_project.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.library_project.entities.User;
import com.capgemini.library_project.services.UserServices;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	private final UserServices userServices;

	@Autowired
	public UserController(UserServices userServices) {
		super();
		this.userServices = userServices;
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
}
