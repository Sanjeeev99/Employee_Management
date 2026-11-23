package com.example.employee.exception;
import org.springframework.http.HttpStatus;



public class GuardrailViolationException extends RuntimeException{
	
	private final HttpStatus status;
	
	public GuardrailViolationException(HttpStatus status, String message) {
		super(message);
		this.status=status;
	}
	
	public HttpStatus getStatus() {
		return status;
	}

	
	
}
