package com.ideaportal.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    User user = new User();

    @BeforeEach
    public void init(){

        Roles roles = new Roles();
        roles.setRoleID(1);

        user.setUserID(1);
        user.setUserPassword("demo");
        user.setUserName("demo");
        user.setUserEmail("demo");
        user.setUserCompany("demo");
        user.setSignUpDate(null);
        user.setRole(roles);
    }

    @Test
    void getUserID() {
        assertEquals(1, user.getUserID());
    }

    @Test
    void setUserID() {
        user.setUserID(2);
        assertEquals(2, user.getUserID());
    }

    @Test
    void getUserPassword() {
        assertEquals("demo", user.getUserPassword());
    }

    @Test
    void setUserPassword() {
        user.setUserPassword("updated");
        assertEquals("updated", user.getUserPassword());
    }

    @Test
    void getUserName() {
        assertEquals("demo", user.getUserName());
    }

    @Test
    void setUserName() {
        user.setUserName("updated");
        assertEquals("updated", user.getUserName());
    }

    @Test
    void getUserEmail() {
        assertEquals("demo", user.getUserEmail());
    }

    @Test
    void setUserEmail() {
        user.setUserEmail("updated");
        assertEquals("updated", user.getUserEmail());
    }

    @Test
    void getUserCompany() {
        assertEquals("demo", user.getUserCompany());
    }

    @Test
    void setUserCompany() {
        user.setUserCompany("updated");
        assertEquals("updated", user.getUserCompany());
    }

    @Test
    void getRole() {
        assertEquals(1, user.getRole().getRoleID());
    }

    @Test
    void setRole() {
        Roles roles = new Roles();
        roles.setRoleID(2);
        user.setRole(roles);
        assertEquals(2, user.getRole().getRoleID());
    }

    @Test
    void getSignUpDate() {
        assertNull(user.getSignUpDate());
    }

    @Test
    void setSignUpDate() {
        Date date = new Date();
        user.setSignUpDate(date);
        assertEquals(date, user.getSignUpDate());
    }

    @Test
    void testToString() {
        String result = "User [userID=" + user.getUserID() + ", userPassword=" + user.getUserPassword() + ", userName=" + user.getUserName() + ", userEmail="
                + user.getUserEmail() + ", userCompany=" + user.getUserCompany() + ", roles=" + user.getRole() + "]";
        assertEquals(result, user.toString());
    }

    @Test
    void testHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (user.getUserID() ^ (user.getUserID() >>> 32));
        assertEquals(result, user.hashCode());
    }

    @Test
    void testEquals() {
        assertTrue(user.equals(user));
    }

    @Test
    void testEquals_objectNull() {
        assertFalse(user.equals(null));
    }
}