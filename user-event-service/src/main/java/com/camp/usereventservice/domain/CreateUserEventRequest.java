package com.camp.usereventservice.domain;

public class CreateUserEventRequest {

	private String category;
	private String content;
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
		return "CreateUserEventRequest [category=" + category + ", content=" + content + "]";
	}
	
	
}
