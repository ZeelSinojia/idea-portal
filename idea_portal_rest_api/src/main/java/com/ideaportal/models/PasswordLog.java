package com.ideaportal.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class PasswordLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long passwordLogID;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String oldPassword;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String newPassword;

    public PasswordLog(long passwordLogID, User user, String oldPassword, String newPassword) {
        this.passwordLogID = passwordLogID;
        this.user = user;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public PasswordLog() {
    }

    public long getPasswordLogID() {
        return passwordLogID;
    }

    public void setPasswordLogID(long passwordLogID) {
        this.passwordLogID = passwordLogID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordLog that = (PasswordLog) o;
        return passwordLogID == that.passwordLogID && user.equals(that.user) && oldPassword.equals(that.oldPassword) && newPassword.equals(that.newPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(passwordLogID, user, oldPassword, newPassword);
    }

    @Override
    public String toString() {
        return "PasswordLog{" +
                "passwordLogID=" + passwordLogID +
                ", user=" + user +
                ", oldPassword='" + oldPassword + '\'' +
                ", newPassword='" + newPassword + '\'' +
                '}';
    }
}
