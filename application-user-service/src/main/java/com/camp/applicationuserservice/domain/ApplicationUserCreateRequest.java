package com.camp.applicationuserservice.domain;

public class ApplicationUserCreateRequest {
	private String applicationId;
	private String username;
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
