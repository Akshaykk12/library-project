package com.capgemini.library_project.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.capgemini.library_project.entities.User;
import com.capgemini.library_project.exceptions.UserAlreadyExistsException;
import com.capgemini.library_project.exceptions.UserNotFoundException;
import com.capgemini.library_project.repositories.UserRepository;

@Service
public class UserServicesImpl implements UserServices{
	

	 private static final Logger logger = LoggerFactory.getLogger(UserServicesImpl.class);
	    private final UserRepository userRepository;
	    private final String UPLOAD_DIR = "uploads/";

	    @Autowired
	    public UserServicesImpl(UserRepository userRepository) {
	        this.userRepository = userRepository;
	    }

	    @Override
	    public List<User> getAllUsers() {
	        logger.info("Fetching all users");
	        return userRepository.findAll();
	    }

	    @Override
	    public User getUserById(Long userId) {
	        logger.info("Fetching user by ID: {}", userId);
	        return userRepository.findById(userId)
	                .orElseThrow(() -> {
	                    logger.error("User with id {} not found", userId);
	                    return new UserNotFoundException("User with id : " + userId + " not found.");
	                });
	    }

      @Override
	public User createUser(User user) {
		if (userRepository.findByUserEmail(user.getUserEmail()).isPresent()|| userRepository.findByUserEmail(user.getUserName()).isPresent()) {
			logger.info("User Already exists",user.getUserName(),user.getUserEmail());
	        throw new UserAlreadyExistsException("User Already Exists");
	    }
		 logger.info("Creating new user: {}", user.getUserName());
		return userRepository.save(user);
	}

	    @Override
	    public User updateUser(Long userId, User user) {
	        logger.info("Updating user with ID: {}", userId);
	        User existing = userRepository.findById(userId)
	                .orElseThrow(() -> {
	                    logger.error("User with id {} not found for update", userId);
	                    return new UserNotFoundException("User with id : " + userId + " not found.");
	                });

	        existing.setUserName(user.getUserName());
	        existing.setUserEmail(user.getUserEmail());
	        existing.setUserContact(user.getUserContact());
	        return userRepository.save(existing);
	    }

	    @Override
	    public boolean deleteUser(Long userId) {
	        logger.info("Deleting user with ID: {}", userId);
	        if (!userRepository.existsById(userId)) {
	            logger.error("User with id {} not found for deletion", userId);
	            throw new UserNotFoundException("User with id : " + userId + " not found.");
	        }
	        userRepository.deleteById(userId);
	        return true;
	    }

	    @Override
	    public User updateImage(Long userId, MultipartFile image) throws IOException {
	        logger.info("Updating image for user ID: {}", userId);
	        Files.createDirectories(Paths.get(UPLOAD_DIR));
	        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
	        Path filePath = Paths.get(UPLOAD_DIR, fileName);
	        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

	        User user = userRepository.findById(userId)
	                .orElseThrow(() -> {
	                    logger.error("User with id {} not found for image update", userId);
	                    return new UserNotFoundException("User with id " + userId + " not found");
	                });

	        user.setUserImage(fileName);
	        return userRepository.save(user);
	    }

	    @Override
	    public User getImage(Long userId) {
	        logger.info("Getting image for user ID: {}", userId);
	        return userRepository.findById(userId)
	                .orElseThrow(() -> {
	                    logger.error("User with id {} not found when retrieving image", userId);
	                    return new UserNotFoundException("User not found");
	                });
	    }

		@Override
		public User findByUserNameOrUserEmail(String name, String email) {
			// TODO Auto-generated method stub
			return userRepository.findByUserNameOrUserEmail(name, email).orElseThrow(() -> new UserNotFoundException("User not found with given eamil: "+email));
		}

		@Override
		public boolean existsByUserEmail(String email) {
			// TODO Auto-generated method stub
			return userRepository.existsByUserEmail(email);
		}
}