package com.capgemini.library_project.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.capgemini.library_project.entities.User;
import com.capgemini.library_project.repositories.UserRepository;

public class UserServicesImpl implements UserServices{
	
	private final UserRepository userRepository;

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
}
