package com.capgemini.library_project.controllers;

import com.capgemini.library_project.entities.User;
import com.capgemini.library_project.repositories.UserRepository;
import com.capgemini.library_project.services.UserServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserServices userServices;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUserId(1L);
        user.setUserName("John Doe");
        user.setUserEmail("john@example.com");
        user.setUserPassword("secret");
        user.setUserContact(1234567890L);
        user.setUserType("Member");
    }

    @Test
    void testGetAllUsers() {
        when(userServices.getAllUsers()).thenReturn(Arrays.asList(user));
        ResponseEntity<List<User>> response = userController.getAllUsers();
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetUserById_Found() {
        when(userServices.getUserById(1L)).thenReturn(user);
        ResponseEntity<User> response = userController.getUserById(1L);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("John Doe", response.getBody().getUserName());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userServices.getUserById(1L)).thenReturn(null);
        ResponseEntity<User> response = userController.getUserById(1L);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testCreateUser() {
        when(userServices.createUser(user)).thenReturn(user);
        ResponseEntity<User> response = userController.createUser(user);
        assertEquals(201, response.getStatusCode().value());
        assertEquals("John Doe", response.getBody().getUserName());
    }

    @Test
    void testUpdateUser_Success() {
        when(userServices.updateUser(1L, user)).thenReturn(user);
        ResponseEntity<User> response = userController.updateUser(1L, user);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testUpdateUser_NotFound() {
        when(userServices.updateUser(1L, user)).thenReturn(null);
        ResponseEntity<User> response = userController.updateUser(1L, user);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testDeleteUser_Success() {
        when(userServices.deleteUser(1L)).thenReturn(true);
        ResponseEntity<Void> response = userController.deleteUser(1L);
        assertEquals(204, response.getStatusCode().value());
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userServices.deleteUser(1L)).thenReturn(false);
        ResponseEntity<Void> response = userController.deleteUser(1L);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testUploadImage() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", "dummy".getBytes());
        when(userServices.updateImage(1L, image)).thenReturn(user);
        ResponseEntity<User> response = userController.uploadImage(1L, image);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testDeleteProfileImage() {
        user.setUserImage("john.jpg");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        ResponseEntity<User> response = userController.deleteProfileImage(1L);
        verify(userRepository, times(1)).save(user);
        assertEquals(200, response.getStatusCode().value());
        assertNull(user.getUserImage());
    }
}
