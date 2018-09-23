package com.camp.applicationuserservice.domain;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

public class ApplicationUserUpdateRequest {

	@NotNull
	@Length(min = 3, max = 40)
	private String firebaseToken;

	public String getFirebaseToken() {
		return firebaseToken;
	}

	public void setFirebaseToken(String firebaseToken) {
		this.firebaseToken = firebaseToken;
	}

	@Override
	public String toString() {
		return "ApplicationUserUpdateRequest [firebaseToken=" + firebaseToken + "]";
	}

}
