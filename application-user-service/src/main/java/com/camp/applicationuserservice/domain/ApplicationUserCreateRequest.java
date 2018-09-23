package com.camp.applicationuserservice.domain;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

public class ApplicationUserCreateRequest {
	@NotNull
	@Length(min = 3, max = 40)
	private String applicationId;
	@NotNull
	@Length(min = 3, max = 40)
	private String username;
	@NotNull
	@Length(min = 3, max = 40)
	private String firebaseToken;
	public String getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFirebaseToken() {
		return firebaseToken;
	}
	public void setFirebaseToken(String firebaseToken) {
		this.firebaseToken = firebaseToken;
	}
	@Override
	public String toString() {
		return "ApplicationUserCreateRequest [applicationId=" + applicationId + ", username=" + username
				+ ", firebaseToken=" + firebaseToken + "]";
	}
	
}
