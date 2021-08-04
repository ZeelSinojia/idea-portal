package com.ideaportal.models;

import java.util.Date;

import javax.persistence.*;

@Entity
//@Table(name = "likes")
public class Likes 
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "like_id")
	private long likeID;
	
	@Enumerated
	private LikeValue likeValue;
	
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date likeDate;
	
	@ManyToOne
	@JoinColumn(name = "idea_id")
	private Ideas idea;
	
	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	public Likes()
	{
		
	}

	public Likes(long likeID, LikeValue likeValue, Ideas idea, User user) 
	{
		super();
		this.likeID = likeID;
		this.likeValue = likeValue;
		this.idea = idea;
		this.user = user;
	}

	public long getLikeID() {
		return likeID;
	}

	public void setLikeID(long likeID) {
		this.likeID = likeID;
	}

	public LikeValue getLikeValue() {
		return likeValue;
	}

	public void setLikeValue(LikeValue likeValue) {
		this.likeValue = likeValue;
	}

	public Ideas getIdea() {
		return idea;
	}

	public void setIdea(Ideas idea) {
		this.idea = idea;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getLikeDate() {
		return likeDate;
	}

	public void setLikeDate(Date likeDate) {
		this.likeDate = likeDate;
	}

	@Override
	public String toString() {
		return "Likes [likeID=" + likeID + ", likeValue=" + likeValue + ", idea=" + idea + ", user=" + user + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (likeID ^ (likeID >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Likes other = (Likes) obj;
		return likeID != other.likeID;
	}
	
	
	
}
