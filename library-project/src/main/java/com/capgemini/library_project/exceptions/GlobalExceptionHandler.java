package com.capgemini.library_project.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<String> handleUserAlreadyExistsException(UserNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}
	@ExceptionHandler(BookNotFoundException.class)
	public ResponseEntity<String> handleUserAlreadyExistsException(BookNotFoundException be) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(be.getMessage());
	}
	@ExceptionHandler(BorrowRecordNotFoundException.class)
	public ResponseEntity<String> handleUserAlreadyExistsException(BorrowRecordNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	@ExceptionHandler(AlreadyReturnedException.class)
	public ResponseEntity<String> handleAlreadyReturnedException(AlreadyReturnedException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}
	

	@ExceptionHandler(AuthorAlreadyExistsException.class)
	public ResponseEntity<String> handleAuthorAlreadyExistsException(AuthorAlreadyExistsException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}
	
	@ExceptionHandler(AuthorNotFoundException.class)
	public ResponseEntity<String> handleAuthorNotFoundException(AuthorNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}
	
	
	
	@ExceptionHandler(CategoryNotFoundException.class)
	public ResponseEntity<String> handleCategoryNotFoundException(CategoryNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}
	
	
	@ExceptionHandler(InvalidBorrowDateException.class)
	public ResponseEntity<String> handleInvalidBorrowDateException(InvalidBorrowDateException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}
	
	@ExceptionHandler(InvalidStatusException.class)
	public ResponseEntity<String> handleInvalidStatusException(InvalidStatusException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}
	
}
