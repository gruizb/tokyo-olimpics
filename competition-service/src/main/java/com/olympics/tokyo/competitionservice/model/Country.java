package com.olympics.tokyo.competitionservice.model;

public class Country {

	private String code;
	private String description;

	public Country(String code, String description) {
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