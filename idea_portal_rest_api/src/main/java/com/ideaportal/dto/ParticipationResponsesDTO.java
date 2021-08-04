package com.ideaportal.dto;

import com.ideaportal.models.Ideas;
import com.ideaportal.models.ParticipantRoles;
import com.ideaportal.models.Themes;
import com.ideaportal.models.User;

import java.sql.Date;

public class ParticipationResponsesDTO {

    private long responseID;

    private Date participationDate;

    private Ideas idea;

    private Themes theme;

    private User user;

    private ParticipantRoles participantRoles;

    public ParticipantRoles getParticipantRoles() {
        return participantRoles;
    }

    public void setParticipantRoles(ParticipantRoles participantRoles) {
        this.participantRoles = participantRoles;
    }

    public long getResponseID() {
        return responseID;
    }

    public void setResponseID(long responseID) {
        this.responseID = responseID;
    }

    public Date getParticipationDate() {
        return participationDate;
    }

    public void setParticipationDate(Date participationDate) {
        this.participationDate = participationDate;
    }

    public Ideas getIdea() {
        return idea;
    }

    public void setIdea(Ideas idea) {
        this.idea = idea;
    }

    public Themes getTheme() {
        return theme;
    }

    public void setTheme(Themes theme) {
        this.theme = theme;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
