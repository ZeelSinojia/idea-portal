package com.ideaportal.exceptions;

public class RequestFailedException extends Exception {


    private static final long serialVersionUID = 1L;

    private final String errorMessage;

    public RequestFailedException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }




}
