package com.ideaportal.models;

import java.util.Date;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class Artifacts 
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "artifact_id")
	private long artifactID;
	
	@ManyToOne
	@JoinColumn(name = "theme_id")
	@JsonBackReference(value = "theme_artifacts")
	Themes theme;
	
	@ManyToOne
	@JoinColumn(name = "idea_id")
	@JsonBackReference(value = "idea_artifacts")
	Ideas idea;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	User user;
	
	@Column(columnDefinition = "LONGTEXT", name="artifact_url")
	private String artifactURL;
	
	private String fileType;
	
	
	@Column(columnDefinition = "TEXT", name="original_file_name")
	private String originalFileName;
	
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="creation_date")
	private Date artifactCreationDate;
	
	@Enumerated
	private IsModified isModified;
	
	public Artifacts(long artifactID, Themes theme, Ideas idea, User user, String artifactURL) 
	{
		super();
		this.artifactID = artifactID;
		this.theme = theme;
		this.idea = idea;
		this.user = user;
		this.artifactURL = artifactURL;
	}

	public Artifacts() 
	{
		
	}

	public IsModified getIsModified() {
		return isModified;
	}

	public void setIsModified(IsModified isModified) {
		this.isModified = isModified;
	}

	public long getArtifactID()
	{
		return artifactID;
	}

	public void setArtifactID(long artifactID) {
		this.artifactID = artifactID;
	}

	public Themes getTheme() {
		return theme;
	}

	public void setTheme(Themes theme) {
		this.theme = theme;
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

	public String getArtifactURL() {
		return artifactURL;
	}

	public void setArtifactURL(String artifactURL) {
		this.artifactURL = artifactURL;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public Date getArtifactCreationDate() {
		return artifactCreationDate;
	}

	public void setArtifactCreationDate(Date artifactCreationDate) {
		this.artifactCreationDate = artifactCreationDate;
	}

	public String getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (artifactID ^ (artifactID >>> 32));
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
		Artifacts other = (Artifacts) obj;
		return artifactID != other.artifactID;
	}

	@Override
	public String toString() {
		return "Artifacts [artifactID=" + artifactID + ", theme=" + theme + ", idea=" + idea + ", user=" + user
				+ ", artifactURL=" + artifactURL + ", fileType=" + fileType + ", artifactCreationDate="
				+ artifactCreationDate + ", artifactModificationDate=" + ", modifiedBy="
				+ "]";
	}
	
	
	
}
