package com.ideaportal.models;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginTest {

    Login login = new Login();

    @BeforeEach
    public void init(){
        login.setUserName("username");
        login.setUserPassword("password");
    }

    @Test
    void getUserName() {
        assertEquals("username", login.getUserName());
    }

    @Test
    void setUserName() {
        login.setUserName("updated");
        assertEquals("updated", login.getUserName());

    }

    @Test
    void getUserPassword() {
        assertEquals("password", login.getUserPassword());

    }

    @Test
    void setUserPassword() {
        login.setUserPassword("updated");
        assertEquals("updated", login.getUserPassword());

    }
}