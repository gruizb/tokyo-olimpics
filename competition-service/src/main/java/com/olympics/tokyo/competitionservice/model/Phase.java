package com.olympics.tokyo.competitionservice.model;

public class Phase {

	private String code;
	private String description;

	public Phase(String code, String description) {
		super();
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}
}