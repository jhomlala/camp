package com.camp.applicationservice.domain;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.camp.applicationservice.domain.validators.EnumValidator;

public class ApplicationCreateRequest {
	@NotNull
	@Length(min = 3, max = 40)
	private String name;

	@EnumValidator(enumClass = Os.class, ignoreCase = true)
	private String os;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

}
