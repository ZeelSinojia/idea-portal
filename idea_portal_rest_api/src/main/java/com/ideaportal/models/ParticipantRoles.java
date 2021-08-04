package com.ideaportal.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class ParticipantRoles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_role_id")
    private int participantRoleID;

    @Column(nullable = false, name = "participant_role_name")
    private String participantRoleName;

    public ParticipantRoles() {
    }

    public ParticipantRoles(int participantRoleID, String participantRoleName) {
        this.participantRoleID = participantRoleID;
        this.participantRoleName = participantRoleName;
    }

    public long getParticipantRoleID() {
        return participantRoleID;
    }

    public void setParticipantRoleID(int participantRoleID) {
        this.participantRoleID = participantRoleID;
    }

    public String getParticipantRoleName() {
        return participantRoleName;
    }

    public void setParticipantRoleName(String participantRoleName) {
        this.participantRoleName = participantRoleName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipantRoles that = (ParticipantRoles) o;
        return participantRoleID == that.participantRoleID && participantRoleName.equals(that.participantRoleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(participantRoleID, participantRoleName);
    }

    @Override
    public String toString() {
        return "ParticipantRoles{" +
                "participantRoleID=" + participantRoleID +
                ", participantRoleName='" + participantRoleName + '\'' +
                '}';
    }
}
