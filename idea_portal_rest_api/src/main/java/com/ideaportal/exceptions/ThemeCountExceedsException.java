package com.ideaportal.exceptions;

public class ThemeCountExceedsException extends RuntimeException 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String errorMessage;

	public ThemeCountExceedsException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	
	

}
