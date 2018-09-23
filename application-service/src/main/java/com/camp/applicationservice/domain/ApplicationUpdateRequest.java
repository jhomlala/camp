package com.camp.applicationservice.domain;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

public class ApplicationUpdateRequest {

	@NotNull
	@Length(min = 3, max = 40)
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ApplicationUpdateRequest [name=" + name + "]";
	}

}
