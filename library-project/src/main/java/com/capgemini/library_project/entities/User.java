package com.capgemini.library_project.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;
	
	@NotBlank(message = "User Name is required")
	private String userName;
	
	@NotBlank(message = "Email is required")
	private String userEmail;
	
	@NotBlank(message = "Password is required")
	private String userPassword;

	@NotNull(message = "Contact is required")
	@Positive(message= "Contact cannot be negative")
	private Long userContact;
	
	@NotBlank(message = "User Type is required")
	private String userType;
}