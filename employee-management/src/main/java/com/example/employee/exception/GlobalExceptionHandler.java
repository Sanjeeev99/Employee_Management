package com.example.employee.exception;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.employee.dto.ErrorResponse;


@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(GuardrailViolationException.class)
	public ResponseEntity<ErrorResponse> handleGuardrailViolation(GuardrailViolationException ex,HttpServletRequest request){
		
		HttpStatus status = ex.getStatus();
		String errorTitle = (status == HttpStatus.FORBIDDEN) ? "Guardrail Security Policy Violation": "Guardrail Validation Failed";
		
		ErrorResponse body =  new ErrorResponse(LocalDateTime.now(),
				status.value(),
				errorTitle,
				ex.getMessage(),
				request.getRequestURI()
				);
		return new ResponseEntity<>(body, status);
				
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request){
		
		ErrorResponse body =new ErrorResponse(LocalDateTime.now(),
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"INTERNAL Server Error",
				ex.getMessage(),
				request.getRequestURI()
				); 
	return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
