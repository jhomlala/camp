package com.camp.notificationsservice.domain;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

public class NotificationCreateRequest {
	@NotNull
	@Length(min = 3, max = 40)
	private String applicationId;
	@NotNull
	@Length(min = 3, max = 40)
	private String title;
	@NotNull
	@Length(min = 3, max = 250)
	private String content;
	public String getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	
}
