package com.capgemini.library_project.controllers;

import com.capgemini.library_project.dto.LoginDto;
import com.capgemini.library_project.entities.User;
import com.capgemini.library_project.exceptions.UserNotFoundException;
import com.capgemini.library_project.security.JwtUtils;
import com.capgemini.library_project.services.UserServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private UserServices userService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtUtils jwtService;

	@InjectMocks
	private AuthController authController;

	private User user;
	private LoginDto loginDto;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		user = new User();
		user.setUserId(1L);
		user.setUserName("testuser");
		user.setUserEmail("test@example.com");
		user.setUserPassword("encodedPassword");
		user.setUserType("Member");

		loginDto = new LoginDto();
		loginDto.setUserName("testuser");
		loginDto.setPassword("password");
	}

	@Test
	void testRegisterUser_Success() {

		when(userService.existsByUserName(user.getUserName())).thenReturn(false);
		when(userService.existsByUserEmail(user.getUserEmail())).thenReturn(false);
		when(passwordEncoder.encode(user.getUserPassword())).thenReturn("encodedPassword");
		when(userService.createUser(user)).thenReturn(user);

		ResponseEntity<User> response = authController.registerUser(user);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("testuser", response.getBody().getUserName());
		verify(passwordEncoder).encode(user.getUserPassword());
		verify(userService).createUser(user);
	}

	@Test
	void testRegisterUser_UsernameExists() {

		when(userService.existsByUserName(user.getUserName())).thenReturn(true);

		assertThrows(UserNotFoundException.class, () -> {
			authController.registerUser(user);
		});
	}

	@Test
	void testRegisterUser_EmailExists() {

		when(userService.existsByUserName(user.getUserName())).thenReturn(false);
		when(userService.existsByUserEmail(user.getUserEmail())).thenReturn(true);

		assertThrows(UserNotFoundException.class, () -> {
			authController.registerUser(user);
		});
	}

	@Test
	void testAuthenticateUser_Success() {

		Authentication authentication = mock(Authentication.class);
		when(authentication.isAuthenticated()).thenReturn(true);
		when(authenticationManager.authenticate(any())).thenReturn(authentication);
		when(userService.findByUserNameOrUserEmail(loginDto.getUserName(), loginDto.getUserName())).thenReturn(user);

		Map<String, Object> claims = new HashMap<>();
		claims.put("username", user.getUserName());
		claims.put("email", user.getUserEmail());
		claims.put("name", user.getUserName());
		claims.put("userid", user.getUserId());
		claims.put("usertype", user.getUserType());
		claims.put("phone", user.getUserContact());

		when(jwtService.generateToken(loginDto.getUserName(), claims)).thenReturn("testToken");

		ResponseEntity<Map<String, String>> response = authController.authenticateUser(loginDto);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("testToken", response.getBody().get("token"));
		verify(authenticationManager).authenticate(any());
		verify(jwtService).generateToken(loginDto.getUserName(), claims);
	}

	@Test
	void testAuthenticateUser_Unauthorized() {

		Authentication authentication = mock(Authentication.class);
		when(authentication.isAuthenticated()).thenReturn(false);
		when(authenticationManager.authenticate(any())).thenReturn(authentication);

		ResponseEntity<Map<String, String>> response = authController.authenticateUser(loginDto);

		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("You are not Authorized !!", response.getBody().get("error"));
	}
}
