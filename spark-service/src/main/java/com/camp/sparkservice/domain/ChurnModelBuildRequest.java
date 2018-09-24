package com.camp.sparkservice.domain;

import java.util.Date;

public class ChurnModelBuildRequest {
	private String applicationId;
	private Date startDate;
	private Date endDate;
	private String signInEventCategory;
	public String getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getSignInEventCategory() {
		return signInEventCategory;
	}
	public void setSignInEventCategory(String signInEventCategory) {
		this.signInEventCategory = signInEventCategory;
	}
	
	
}
