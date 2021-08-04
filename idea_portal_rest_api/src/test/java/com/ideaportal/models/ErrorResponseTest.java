package com.ideaportal.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    ErrorResponse errorResponse = new ErrorResponse();

    @BeforeEach
    public void init(){
        errorResponse.setMessage("demo-error");
        errorResponse.setErrorCode(1);
    }

    @Test
    void getErrorCode() {
        assertEquals(1, errorResponse.getErrorCode());
    }

    @Test
    void setErrorCode() {
        errorResponse.setErrorCode(2);
        assertEquals(2, errorResponse.getErrorCode());
    }

    @Test
    void getMessage() {
        assertEquals("demo-error", errorResponse.getMessage());
    }

    @Test
    void setMessage() {
        errorResponse.setMessage("updated-error");
        assertEquals("updated-error", errorResponse.getMessage());
    }

    @Test
    void testToString() {
        String result = "{\"errorCode\":\""+errorResponse.getErrorCode()+"\",\"message\":\""+errorResponse.getMessage()+"\"}";
        assertEquals(result, errorResponse.toString());
    }
}