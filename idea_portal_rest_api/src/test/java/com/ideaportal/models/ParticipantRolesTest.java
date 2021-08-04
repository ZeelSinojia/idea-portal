package com.ideaportal.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantRolesTest {

    ParticipantRoles participantRoles = new ParticipantRoles();

    @BeforeEach
    public void init(){
        participantRoles.setParticipantRoleID(1);
        participantRoles.setParticipantRoleName("demo");
    }

    @Test
    void getParticipantRoleID() {
        assertEquals(1, participantRoles.getParticipantRoleID());
    }

    @Test
    void setParticipantRoleID() {
        participantRoles.setParticipantRoleID(2);
        assertEquals(2, participantRoles.getParticipantRoleID());

    }

    @Test
    void getParticipantRoleName() {
        assertEquals("demo", participantRoles.getParticipantRoleName());

    }

    @Test
    void setParticipantRoleName() {
        participantRoles.setParticipantRoleName("updated");
        assertEquals("updated", participantRoles.getParticipantRoleName());

    }

    @Test
    void testEquals() {
        assertTrue(participantRoles.equals(participantRoles));
    }

    @Test
    void testEquals_objectNull() {
        assertFalse(participantRoles.equals(null));
    }

    @Test
    void testHashCode() {
        assertEquals(Objects.hash(participantRoles.getParticipantRoleID(), participantRoles.getParticipantRoleName()), participantRoles.hashCode());
    }

    @Test
    void testToString() {
        String result = "ParticipantRoles{" +
                "participantRoleID=" + participantRoles.getParticipantRoleID() +
                ", participantRoleName='" + participantRoles.getParticipantRoleName() + '\'' +
                '}';
        assertEquals(result, participantRoles.toString());
    }
}