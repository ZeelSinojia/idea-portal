package com.ideaportal.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResponseMessageTest {

    ResponseMessage<Object> responseMessage = new ResponseMessage<Object>();

    @BeforeEach
    public void init() {
        responseMessage.setStatus(1);
        responseMessage.setStatusText("demo");
        responseMessage.setResult("result");
        responseMessage.setToken("token");
        responseMessage.setTotalElements(1);
        responseMessage.setObject("object");
    }

    @Test
    void getObject() {
        assertEquals("object", responseMessage.getObject());
    }

    @Test
    void setObject() {
        responseMessage.setObject("updated");
        assertEquals("updated", responseMessage.getObject());

    }

    @Test
    void getToken() {
        assertEquals("token", responseMessage.getToken());
    }

    @Test
    void setToken() {
        responseMessage.setToken("updated");
        assertEquals("updated", responseMessage.getToken());

    }

    @Test
    void getStatus() {
        assertEquals(1, responseMessage.getStatus());
    }

    @Test
    void setStatus() {
        responseMessage.setStatus(2);
        assertEquals(2, responseMessage.getStatus());
    }

    @Test
    void getStatusText() {
        assertEquals("demo", responseMessage.getStatusText());
    }

    @Test
    void setStatusText() {
        responseMessage.setStatusText("updated");
        assertEquals("updated", responseMessage.getStatusText());
    }

    @Test
    void getResult() {
        assertEquals("result", responseMessage.getResult());
    }

    @Test
    void setResult() {
        responseMessage.setResult("updated");
        assertEquals("updated", responseMessage.getResult());
    }

    @Test
    void getTotalElements() {
        assertEquals(1, responseMessage.getTotalElements());
    }

    @Test
    void setTotalElements() {
        responseMessage.setTotalElements(2);
        assertEquals(2, responseMessage.getTotalElements());
    }

    @Test
    void testToString() {
        String result = "ResponseMessage [status=" + responseMessage.getStatus() + ", statusText=" + responseMessage.getStatusText() + ", result=" + responseMessage.getResult() + "]";
        assertEquals(result, result.toString());
    }
}