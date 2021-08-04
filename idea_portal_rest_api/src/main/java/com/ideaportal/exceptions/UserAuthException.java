package com.ideaportal.exceptions;

//Exception generated if user entered some invalid fields while signup
//Exception generated if user entered invalid credentials while login
//@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserAuthException extends RuntimeException{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String errorMessage;

	public String getErrorMessage() {
		return errorMessage;
	}
	
	public UserAuthException(String errorMessage) {
        super(errorMessage);
        this.errorMessage=errorMessage;
    }
}
