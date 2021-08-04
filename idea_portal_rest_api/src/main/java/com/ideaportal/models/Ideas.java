package com.ideaportal.models;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Ideas 
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "idea_id")
	private long ideaID;
	
	@Column(nullable = false, columnDefinition = "TEXT")
	private String ideaName;
	
	@Column(nullable = false, columnDefinition = "TEXT")
	private String ideaDescription;
	
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false, name = "creation_date")
	private Date ideaDate;
	
	@ManyToOne
	@JoinColumn(name = "theme_id")
	private Themes theme;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@OneToMany(mappedBy = "idea")
	@JsonManagedReference(value = "idea_artifacts")
	List<Artifacts> artifacts;
	
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modification_date")
	private Date ideaModificationDate;
	
	@ManyToOne
	@JoinColumn(name = "modified_by")
	private User modifiedBy;

	@Enumerated
	private IsDeleted isDeleted;
	
	public Ideas()
	{
		
	}
	
	public Ideas(long ideaID, String ideaName, String ideaDescription, Date ideaDate, Themes theme, User user,
			List<Artifacts> artifacts, Date ideaModificationDate, User modifiedBy) {
		super();
		this.ideaID = ideaID;
		this.ideaName = ideaName;
		this.ideaDescription = ideaDescription;
		this.ideaDate = ideaDate;
		this.theme = theme;
		this.user = user;
		this.artifacts = artifacts;
		this.ideaModificationDate = ideaModificationDate;
		this.modifiedBy = modifiedBy;
	}

	public IsDeleted getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(IsDeleted isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Date getIdeaDate() {
		return ideaDate;
	}

	public void setIdeaDate(Date ideaDate) {
		this.ideaDate = ideaDate;
	}

	
	public long getIdeaID() {
		return ideaID;
	}

	public void setIdeaID(long ideaID) {
		this.ideaID = ideaID;
	}

	public String getIdeaName() {
		return ideaName;
	}

	public void setIdeaName(String ideaName) {
		this.ideaName = ideaName;
	}

	public String getIdeaDescription() {
		return ideaDescription;
	}

	public void setIdeaDescription(String ideaDescription) {
		this.ideaDescription = ideaDescription;
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
	
	public Date getIdeaModificationDate() {
		return ideaModificationDate;
	}

	public void setIdeaModificationDate(Date ideaModificationDate) {
		this.ideaModificationDate = ideaModificationDate;
	}

	public User getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(User modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public List<Artifacts> getArtifacts() {
		return artifacts;
	}

	public void setArtifacts(List<Artifacts> artifacts) {
		this.artifacts = artifacts;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (ideaID ^ (ideaID >>> 32));
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
		Ideas other = (Ideas) obj;
		return ideaID == other.ideaID;
	}

	@Override
	public String toString() {
		return "Ideas [ideaID=" + ideaID + ", ideaName=" + ideaName + ", ideaDescription=" + ideaDescription
				+ ", ideaDate=" + ideaDate + ", theme=" + theme + ", user=" + user + ", artifacts=" + artifacts
				+ ", ideaModificationDate=" + ideaModificationDate + ", modifiedBy=" + modifiedBy + "]";
	}

	
	
	
}
