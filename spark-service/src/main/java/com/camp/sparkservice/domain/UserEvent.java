package com.camp.sparkservice.domain;

import java.util.Date;

public class UserEvent {
	// applicationId parameter was skipped because we don't need it in spark
	// processing
	private Date createdAt;
	private String userId;
	private String id;
	private String category;
	private String content;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "UserEvent [createdAt=" + createdAt + ", userId=" + userId + ", id=" + id + ", category=" + category
				+ ", content=" + content + "]";
	}

	

}
