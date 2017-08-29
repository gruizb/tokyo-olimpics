package com.olympics.tokyo.competitionservice.exception;

public class IncompleteCompetitionException extends Exception {

	private static final long serialVersionUID = -636555937436807406L;

	private String fields;

	public IncompleteCompetitionException(String fields) {
		this.fields = fields;
	}

	public String getFields() {
		return fields;
	}

}
