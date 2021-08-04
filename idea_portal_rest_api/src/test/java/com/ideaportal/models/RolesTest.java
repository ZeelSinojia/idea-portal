package com.ideaportal.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RolesTest {

    Roles roles = new Roles();

    @BeforeEach
    public void init() {
        roles.setRoleID(1);
        roles.setRoleName("demo");
    }

    @Test
    void getRoleID() {
        assertEquals(1, roles.getRoleID());
    }

    @Test
    void setRoleID() {
        roles.setRoleID(2);
        assertEquals(2, roles.getRoleID());
    }

    @Test
    void getRoleName() {
        assertEquals("demo", roles.getRoleName());
    }

    @Test
    void setRoleName() {
        roles.setRoleName("demo");
        assertEquals("demo", roles.getRoleName());
    }

    @Test
    void testToString() {
        String result = "Roles [roleID=" + roles.getRoleID() + ", roleName=" + roles.getRoleName() + "]";
    }

    @Test
    void testHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + roles.getRoleID();
        assertEquals(result, roles.hashCode());
    }

    @Test
    void testEquals() {
        assertTrue(roles.equals(roles));
    }

    @Test
    void testEquals_objectNull() {
        assertFalse(roles.equals(null));
    }
}