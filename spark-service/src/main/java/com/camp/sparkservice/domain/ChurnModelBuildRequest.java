package com.camp.sparkservice.domain;

import java.util.Date;

public class ChurnModelBuildRequest {
	private String applicationId;
	private Date startDate;
	private Date endDate;
	private Long daysActive;
	private String signInEventCategory;
	private String registerEventCategory;

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

	public String getRegisterEventCategory() {
		return registerEventCategory;
	}

	public void setRegisterEventCategory(String registerEventCategory) {
		this.registerEventCategory = registerEventCategory;
	}

	public Long getDaysActive() {
		return daysActive;
	}

	public void setDaysActive(Long daysActive) {
		this.daysActive = daysActive;
	}

	@Override
	public String toString() {
		return "ChurnModelBuildRequest [applicationId=" + applicationId + ", startDate=" + startDate + ", endDate="
				+ endDate + ", daysActive=" + daysActive + ", signInEventCategory=" + signInEventCategory
				+ ", registerEventCategory=" + registerEventCategory + "]";
	}

}
