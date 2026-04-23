package com.resourcemind.app.api.error;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<Map<String, ApiError>> notFound(NotFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("error", new ApiError("NOT_FOUND", e.getMessage())));
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<Map<String, ApiError>> badRequest(BadRequestException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(Map.of("error", new ApiError("BAD_REQUEST", e.getMessage())));
	}

	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<Map<String, ApiError>> conflict(ConflictException e) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(Map.of("error", new ApiError("CONFLICT", e.getMessage())));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, ApiError>> validation(MethodArgumentNotValidException e) {
		String msg = e.getBindingResult().getFieldErrors().stream()
				.map(err -> err.getField() + ": " + err.getDefaultMessage())
				.findFirst()
				.orElse("Validation failed");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(Map.of("error", new ApiError("VALIDATION", msg)));
	}
}