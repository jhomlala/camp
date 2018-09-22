package com.camp.applicationservice.exception;

public class ApplicationExistsException extends Exception {

	private static final long serialVersionUID = 5924618531285769373L;

	public ApplicationExistsException(String field, String value) {
		super("Application with " + field + " " + value + " exists.");
	}
}
