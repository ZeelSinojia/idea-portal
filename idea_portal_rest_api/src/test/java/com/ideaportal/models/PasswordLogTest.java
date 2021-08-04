package com.ideaportal.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class PasswordLogTest {

    PasswordLog passwordLog = new PasswordLog();

    @BeforeEach
    public void init() {
        User user = new User();
        user.setUserID(1);

        passwordLog.setPasswordLogID(1);
        passwordLog.setUser(user);
        passwordLog.setOldPassword("old");
        passwordLog.setNewPassword("new");
    }

    @Test
    void getPasswordLogID() {
        assertEquals(1, passwordLog.getPasswordLogID());
    }

    @Test
    void setPasswordLogID() {
        passwordLog.setPasswordLogID(2);
        assertEquals(2, passwordLog.getPasswordLogID());
    }

    @Test
    void getUser() {
        assertEquals(1, passwordLog.getUser().getUserID());
    }

    @Test
    void setUser() {
        User user = new User();
        user.setUserID(2);
        passwordLog.setUser(user);
        assertEquals(2, passwordLog.getUser().getUserID());
    }

    @Test
    void getOldPassword() {
        assertEquals("old", passwordLog.getOldPassword());
    }

    @Test
    void setOldPassword() {
        passwordLog.setOldPassword("updated");
        assertEquals("updated", passwordLog.getOldPassword());
    }

    @Test
    void getNewPassword() {
        assertEquals("new", passwordLog.getNewPassword());
    }

    @Test
    void setNewPassword() {
        passwordLog.setNewPassword("updated");
        assertEquals("updated", passwordLog.getNewPassword());
    }

    @Test
    void testEquals() {
        assertTrue(passwordLog.equals(passwordLog));
    }

    @Test
    void testEquals_objectNull() {
        assertFalse(passwordLog.equals(null));
    }

    @Test
    void testHashCode() {
        assertEquals(Objects.hash(passwordLog.getPasswordLogID(), passwordLog.getUser(), passwordLog.getOldPassword(), passwordLog.getNewPassword()),
                passwordLog.hashCode());
    }

    @Test
    void testToString() {
        String result = "PasswordLog{" +
                "passwordLogID=" + passwordLog.getPasswordLogID() +
                ", user=" + passwordLog.getUser() +
                ", oldPassword='" + passwordLog.getOldPassword() + '\'' +
                ", newPassword='" + passwordLog.getNewPassword() + '\'' +
                '}';

        assertEquals(result, passwordLog.toString());
    }
}