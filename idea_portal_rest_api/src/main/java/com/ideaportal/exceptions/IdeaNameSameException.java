package com.ideaportal.exceptions;

public class IdeaNameSameException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String errorMessage;

	public String getErrorMessage() {
		return errorMessage;
	}

	public IdeaNameSameException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}
}
