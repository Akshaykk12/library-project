package com.capgemini.library_project.services;

import com.capgemini.library_project.entities.User;
import com.capgemini.library_project.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServicesImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServicesImpl userServices;

    private User user;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setUserId(1L);
        user.setUserName("Test User");
        user.setUserEmail("test@example.com");
        user.setUserPassword("password");
        user.setUserContact(1234567890L);
        user.setUserType("Admin");
    }

    @Test
    public void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        assertEquals(1, userServices.getAllUsers().size());
    }

    @Test
    public void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User found = userServices.getUserById(1L);
        assertEquals("Test User", found.getUserName());
    }

    @Test
    public void testCreateUser() {
        when(userRepository.save(user)).thenReturn(user);
        User saved = userServices.createUser(user);
        assertEquals("Test User", saved.getUserName());
    }

    @Test
    public void testUpdateUser() {
        User updatedUser = new User();
        updatedUser.setUserName("Updated Name");
        updatedUser.setUserEmail("updated@example.com");
        updatedUser.setUserContact(9876543210L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userServices.updateUser(1L, updatedUser);
        assertEquals("Updated Name", result.getUserName());
    }

    @Test
    public void testDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);
        assertTrue(userServices.deleteUser(1L));
    }

    @Test
    public void testUpdateImage() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("image.jpg");
        when(file.getInputStream()).thenReturn(mock(InputStream.class));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userServices.updateImage(1L, file);
        assertNotNull(result.getUserImage());
    }

    @Test
    public void testGetImage() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User result = userServices.getImage(1L);
        assertEquals("Test User", result.getUserName());
    }
}