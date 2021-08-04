package com.ideaportal.controllers;

import java.util.NoSuchElementException;


import com.ideaportal.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.ideaportal.models.ErrorResponse;


@ControllerAdvice
public class ExceptionControllerAdvice 
{
	@ExceptionHandler(UserAuthException.class)
	public ResponseEntity<String> userAuthExceptionHandler(UserAuthException exception)
	{
		ErrorResponse errorResponse =new ErrorResponse();
		errorResponse.setErrorCode(HttpStatus.UNAUTHORIZED.value());
		errorResponse.setMessage(exception.getErrorMessage());
		return new ResponseEntity<>(errorResponse.toString(), HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> serverExceptionHandler(Exception exception)
	{
		ErrorResponse errorResponse=new ErrorResponse();
		errorResponse.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		errorResponse.setMessage(exception.getMessage());
		return new ResponseEntity<>(errorResponse.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(IdeaAlreadyLikedException.class)
	public ResponseEntity<String> ideaAlreadyLikedExceptionHandler(IdeaAlreadyLikedException exception)
	{
		ErrorResponse errorResponse =new ErrorResponse();
		errorResponse.setErrorCode(HttpStatus.FORBIDDEN.value());
		errorResponse.setMessage(exception.getErrorMessage());
		return new ResponseEntity<>(errorResponse.toString(), HttpStatus.FORBIDDEN);
	}
	
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<String> noSuchElementExceptionHandler(NoSuchElementException exception)
	{
		ErrorResponse errorResponse=new ErrorResponse();
		errorResponse.setErrorCode(HttpStatus.NOT_FOUND.value());
		errorResponse.setMessage(exception.getMessage());
		return new ResponseEntity<>(errorResponse.toString(), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(ThemeNameSameException.class)
	public ResponseEntity<String> themeNameSameExceptionHandler(ThemeNameSameException exception)
	{
		ErrorResponse errorResponse=new ErrorResponse();
		errorResponse.setErrorCode(HttpStatus.FORBIDDEN.value());
		errorResponse.setMessage(exception.getErrorMessage());
		return new ResponseEntity<>(errorResponse.toString(), HttpStatus.FORBIDDEN);
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<String> userNotFoundExceptionHandler(UserNotFoundException exception)
	{
		ErrorResponse errorResponse=new ErrorResponse();
		errorResponse.setErrorCode(HttpStatus.NOT_FOUND.value());
		errorResponse.setMessage(exception.getErrorMessage());
		return new ResponseEntity<>(errorResponse.toString(), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(InvalidRoleException.class)
	public ResponseEntity<String> invalidRoleExceptionHandler(InvalidRoleException exception)
	{
		ErrorResponse errorResponse=new ErrorResponse();
		errorResponse.setErrorCode(HttpStatus.UNAUTHORIZED.value());
		errorResponse.setMessage(exception.getErrorMessage());
		return new ResponseEntity<>(errorResponse.toString(), HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(MailServerException.class)
	public ResponseEntity<String> mailServerExceptionHandler(MailServerException exception)
	{
		ErrorResponse errorResponse=new ErrorResponse();
		errorResponse.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		errorResponse.setMessage(exception.getErrorMessage());
		return new ResponseEntity<>(errorResponse.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ThemeNotFoundException.class)
	public ResponseEntity<String> themeNotFoundExceptionHandler(ThemeNotFoundException exception)
	{
		ErrorResponse errorResponse=new ErrorResponse();
		errorResponse.setErrorCode(HttpStatus.NOT_FOUND.value());
		errorResponse.setMessage(exception.getErrorMessage());
		return new ResponseEntity<>(errorResponse.toString(), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(IdeaNotPresentInThemeException.class)
	public ResponseEntity<String> ideaNotPresentInThemeExceptionHandler(IdeaNotPresentInThemeException exception)
	{
		ErrorResponse errorResponse=new ErrorResponse();
		errorResponse.setErrorCode(HttpStatus.NOT_FOUND.value());
		errorResponse.setMessage(exception.getErrorMessage());
		return new ResponseEntity<>(errorResponse.toString(), HttpStatus.NOT_FOUND);
	}
	@ExceptionHandler(IdeaAlreadyParticipatedException.class)
	public ResponseEntity<String> ideaAlreadyParticipatedExceptionHandler(IdeaAlreadyParticipatedException exception)
	{
		ErrorResponse errorResponse=new ErrorResponse();
		errorResponse.setErrorCode(HttpStatus.UNAUTHORIZED.value());
		errorResponse.setMessage(exception.getErrorMessage());
		return new ResponseEntity<>(errorResponse.toString(), HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(IdeaNameSameException.class)
	public ResponseEntity<String> ideaNameSameExceptionHandler(IdeaNameSameException exception)
	{
		ErrorResponse errorResponse=new ErrorResponse();
		errorResponse.setErrorCode(HttpStatus.FORBIDDEN.value());
		errorResponse.setMessage(exception.getErrorMessage());
		return new ResponseEntity<>(errorResponse.toString(), HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(RequestFailedException.class)
	public ResponseEntity<String> requestFailedException(RequestFailedException exception)
	{
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setErrorCode(HttpStatus.SERVICE_UNAVAILABLE.value());
		errorResponse.setMessage(exception.getErrorMessage());
		return new ResponseEntity<>(errorResponse.toString(), HttpStatus.SERVICE_UNAVAILABLE);
	}
	
}
