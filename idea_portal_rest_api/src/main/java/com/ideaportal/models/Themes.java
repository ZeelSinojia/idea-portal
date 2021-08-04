package com.ideaportal.models;

import java.util.Date;

import java.util.List;
import java.util.Objects;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Themes 
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "theme_id")
	private long themeID;
	
	@Column(nullable = false, columnDefinition = "TEXT")
	private String themeName;
	
	@Column(nullable = false, columnDefinition = "TEXT")
	private String themeDescription;
	
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="creation_date" , nullable=false)
	private Date themeDate;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@OneToMany(mappedBy = "theme")
	@JsonManagedReference (value = "theme_artifacts")
    private List<Artifacts> artifacts;
	
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modification_date")
	private Date themeModificationDate;
	
	
	@ManyToOne
	@JoinColumn(name = "modified_by")
	private User modifiedBy;

	@Enumerated
	private IsDeleted isDeleted;

	@OneToOne
	@JoinColumn(name = "theme_category")
	private ThemesCategory themesCategory;


	public Themes() {}


	public Themes(long themeID, String themeName, String themeDescription, Date themeDate, User user, List<Artifacts> artifacts, Date themeModificationDate, User modifiedBy, IsDeleted isDeleted, ThemesCategory themesCategory) {
		this.themeID = themeID;
		this.themeName = themeName;
		this.themeDescription = themeDescription;
		this.themeDate = themeDate;
		this.user = user;
		this.artifacts = artifacts;
		this.themeModificationDate = themeModificationDate;
		this.modifiedBy = modifiedBy;
		this.isDeleted = isDeleted;
		this.themesCategory = themesCategory;
	}

	public IsDeleted getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(IsDeleted isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Themes(String themeName, String themesDesc, String toString, Date date, User user)
	{
		
	}

	public Date getThemeDate() {
		return themeDate;
	}

	public void setThemeDate(Date themeDate) {
		this.themeDate = themeDate;
	}

	
	public long getThemeID() {
		return themeID;
	}

	public void setThemeID(long themeID) {
		this.themeID = themeID;
	}

	public String getThemeName() {
		return themeName;
	}

	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}

	public String getThemeDescription() {
		return themeDescription;
	}

	public void setThemeDescription(String themeDescription) {
		this.themeDescription = themeDescription;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getThemeModificationDate() {
		return themeModificationDate;
	}

	public void setThemeModificationDate(Date themeModificationDate) {
		this.themeModificationDate = themeModificationDate;
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

	public ThemesCategory getThemesCategory() {
		return themesCategory;
	}

	public void setThemesCategory(ThemesCategory themesCategory) {
		this.themesCategory = themesCategory;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Themes themes = (Themes) o;
		return themeID == themes.themeID && themeName.equals(themes.themeName) && themeDescription.equals(themes.themeDescription) && themeDate.equals(themes.themeDate) && user.equals(themes.user) && artifacts.equals(themes.artifacts) && themeModificationDate.equals(themes.themeModificationDate) && modifiedBy.equals(themes.modifiedBy) && isDeleted == themes.isDeleted && themesCategory.equals(themes.themesCategory);
	}

	@Override
	public int hashCode() {
		return Objects.hash(themeID, themeName, themeDescription, themeDate, user, artifacts, themeModificationDate, modifiedBy, isDeleted, themesCategory);
	}

	@Override
	public String toString() {
		return "Themes{" +
				"themeID=" + themeID +
				", themeName='" + themeName + '\'' +
				", themeDescription='" + themeDescription + '\'' +
				", themeDate=" + themeDate +
				", user=" + user +
				", artifacts=" + artifacts +
				", themeModificationDate=" + themeModificationDate +
				", modifiedBy=" + modifiedBy +
				", isDeleted=" + isDeleted +
				", themesCategory=" + themesCategory +
				'}';
	}
}
