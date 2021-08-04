package com.ideaportal.models;

import java.util.Date;
import java.util.Objects;


import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class ParticipationResponses 
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "response_id")
	private long responseID;
	
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date participationDate;
	
	@ManyToOne
	@JoinColumn(name = "idea_id")
	private Ideas idea;
	
	@ManyToOne
	@JoinColumn(name = "theme_id")
	private Themes theme;
	
	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

	@OneToOne
	@JoinColumn(name = "participation_role")
	private ParticipantRoles participantRoles;

	public ParticipantRoles getParticipantRoles() {
		return participantRoles;
	}

	public void setParticipantRoles(ParticipantRoles participantRoles) {
		this.participantRoles = participantRoles;
	}

	public ParticipationResponses()
	{
		
	}

	public ParticipationResponses(long responseID, Date participationDate, Ideas idea, Themes theme, User user, ParticipantRoles participantRoles) {
		this.responseID = responseID;
		this.participationDate = participationDate;
		this.idea = idea;
		this.theme = theme;
		this.user = user;
		this.participantRoles = participantRoles;
	}

	public long getResponseID() {
		return responseID;
	}

	public void setResponseID(long responseID) {
		this.responseID = responseID;
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

	public void setTheme(Themes themes) {
		this.theme = themes;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getParticipationDate() {
		return participationDate;
	}

	public void setParticipationDate(Date participationDate) {
		this.participationDate = participationDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ParticipationResponses that = (ParticipationResponses) o;
		return responseID == that.responseID && participationDate.equals(that.participationDate) && idea.equals(that.idea) && theme.equals(that.theme) && user.equals(that.user) && participantRoles.equals(that.participantRoles);
	}

	@Override
	public int hashCode() {
		return Objects.hash(responseID, participationDate, idea, theme, user, participantRoles);
	}

	@Override
	public String toString() {
		return "ParticipationResponses{" +
				"responseID=" + responseID +
				", participationDate=" + participationDate +
				", idea=" + idea +
				", theme=" + theme +
				", user=" + user +
				", participantRoles=" + participantRoles +
				'}';
	}
}


