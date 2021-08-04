package com.ideaportal.models;

import java.util.Date;

import javax.persistence.*;

@Entity
public class Comments 
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "comment_id")
	private long commentID;
	
	@Column(columnDefinition = "LONGTEXT", nullable = false)
	private String commentValue;

	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date commentDate;
	
	@ManyToOne
	@JoinColumn(name = "idea_id")
	private Ideas idea;
	
	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	public Comments()
	{
		
	}

	public Comments(long commentID, String commentValue, Ideas ideas, User user) 
	{
		super();
		this.commentID = commentID;
		this.commentValue = commentValue;
		this.idea = ideas;
		this.user = user;
	}

	public long getCommentID() {
		return commentID;
	}

	public void setCommentID(long commentID) {
		this.commentID = commentID;
	}

	public String getCommentValue() {
		return commentValue;
	}

	public void setCommentValue(String commentValue) {
		this.commentValue = commentValue;
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
	
	public Date getCommentDate() {
		return commentDate;
	}

	public void setCommentDate(Date commentDate) {
		this.commentDate = commentDate;
	}

	@Override
	public String toString() {
		return "Comments [commentID=" + commentID + ", commentValue=" + commentValue + ", idea=" + idea + ", user="
				+ user + "]";
	}

	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (commentID ^ (commentID >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Comments other = (Comments) obj;
		return commentID == other.commentID;
	}
}
