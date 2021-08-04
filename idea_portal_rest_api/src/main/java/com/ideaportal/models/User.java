package com.ideaportal.models;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class User 
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "user_id")
	private long userID;
	
	@Column(nullable = false, columnDefinition = "TEXT")
	private String userPassword;
	
	@Column(nullable = false, columnDefinition = "TEXT", unique = true)
	private String userName;
	
	@Column(nullable =false, columnDefinition = "TEXT", unique = true)
	private String userEmail;
	
	@Column(columnDefinition = "TEXT")
	private String userCompany;				//Only for Client Partner and Product Manager
	
	
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false, name = "signup_date")
	private Date signUpDate;
	
	@OneToOne
	@JoinColumn(name = "role_id")
	private Roles role;

	public User(){}

	
	
	public User(long userID, String userPassword, String userName, String userEmail, String userCompany,
			Date signUpDate, Roles role) {
		super();
		this.userID = userID;
		this.userPassword = userPassword;
		this.userName = userName;
		this.userEmail = userEmail;
		this.userCompany = userCompany;
		this.signUpDate = signUpDate;
		this.role = role;
	}



	public long getUserID() {
		return userID;
	}

	public void setUserID(long userID) {
		this.userID = userID;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserCompany() {
		return userCompany;
	}

	public void setUserCompany(String userCompany) {
		this.userCompany = userCompany;
	}

	public Roles getRole() {
		return role;
	}

	public void setRole(Roles role) {
		this.role = role;
	}
	
	public Date getSignUpDate() {
		return signUpDate;
	}

	public void setSignUpDate(Date signUpDate) {
		this.signUpDate = signUpDate;
	}


	@Override
	public String toString() {
		return "User [userID=" + userID + ", userPassword=" + userPassword + ", userName=" + userName + ", userEmail="
				+ userEmail + ", userCompany=" + userCompany + ", roles=" + role + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (userID ^ (userID >>> 32));
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
		User other = (User) obj;
		return userID != other.userID;
	}

		
	
	
}
