package com.ideaportal.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class ParticipationResponsesTest {

    ParticipationResponses participationResponses = new ParticipationResponses();

    @BeforeEach
    public void init(){

        Themes themes = new Themes();
        themes.setThemeID(1);

        Ideas ideas = new Ideas();
        ideas.setIdeaID(1);

        User user = new User();
        user.setUserID(1);

        ParticipantRoles participantRoles = new ParticipantRoles();
        participantRoles.setParticipantRoleID(1);

        participationResponses.setResponseID(1);
        participationResponses.setParticipationDate(null);
        participationResponses.setUser(user);
        participationResponses.setIdea(ideas);
        participationResponses.setTheme(themes);
        participationResponses.setParticipantRoles(participantRoles);
    }

    @Test
    void getParticipantRoles() {
        assertEquals(1, participationResponses.getParticipantRoles().getParticipantRoleID());
    }

    @Test
    void setParticipantRoles() {
        ParticipantRoles participantRoles = new ParticipantRoles();
        participantRoles.setParticipantRoleID(2);
        participationResponses.setParticipantRoles(participantRoles);
        assertEquals(2, participationResponses.getParticipantRoles().getParticipantRoleID());
    }

    @Test
    void getResponseID() {
        assertEquals(1, participationResponses.getResponseID());
    }

    @Test
    void setResponseID() {
        participationResponses.setResponseID(2);
        assertEquals(2, participationResponses.getResponseID());
    }

    @Test
    void getIdea() {
        assertEquals(1, participationResponses.getIdea().getIdeaID());

    }

    @Test
    void setIdea() {
        Ideas ideas = new Ideas();
        ideas.setIdeaID(2);
        participationResponses.setIdea(ideas);
        assertEquals(2, participationResponses.getIdea().getIdeaID());
    }

    @Test
    void getTheme() {
        assertEquals(1, participationResponses.getTheme().getThemeID());

    }

    @Test
    void setTheme() {
        Themes themes = new Themes();
        themes.setThemeID(2);
        participationResponses.setTheme(themes);
        assertEquals(2, participationResponses.getTheme().getThemeID());

    }

    @Test
    void getUser() {
        assertEquals(1, participationResponses.getUser().getUserID());
    }

    @Test
    void setUser() {
        User user = new User();
        user.setUserID(2);
        participationResponses.setUser(user);
        assertEquals(2, participationResponses.getUser().getUserID());
    }

    @Test
    void getParticipationDate() {
        assertNull(participationResponses.getParticipationDate());
    }

    @Test
    void setParticipationDate() {
        Date date = new Date();
        participationResponses.setParticipationDate(date);
        assertEquals(date, participationResponses.getParticipationDate());
    }

    @Test
    void testEquals() {
        assertTrue(participationResponses.equals(participationResponses));
    }

    @Test
    void testEquals_objectNull() {
        assertFalse(participationResponses.equals(null));
    }

    @Test
    void testHashCode() {
        assertEquals(Objects.hash(participationResponses.getResponseID(), participationResponses.getParticipationDate(),
                participationResponses.getIdea(), participationResponses.getTheme(), participationResponses.getUser(),
                participationResponses.getParticipantRoles()),
                participationResponses.hashCode());
    }

    @Test
    void testToString() {
        String result = "ParticipationResponses{" +
                "responseID=" + participationResponses.getResponseID() +
                ", participationDate=" + participationResponses.getParticipationDate() +
                ", idea=" + participationResponses.getIdea() +
                ", theme=" + participationResponses.getTheme() +
                ", user=" + participationResponses.getUser() +
                ", participantRoles=" + participationResponses.getParticipantRoles() +
                '}';
        assertEquals(result, participationResponses.toString());
    }
}