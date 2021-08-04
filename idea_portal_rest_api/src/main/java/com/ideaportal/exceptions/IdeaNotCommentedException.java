package com.ideaportal.exceptions;

public class IdeaNotCommentedException extends Exception{
    private static final long serialVersionUID = 1L;

    private final String errorMessage;

    public IdeaNotCommentedException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
