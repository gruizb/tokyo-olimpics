package com.olympics.tokyo.competitionservice.exception;

import org.springframework.http.HttpStatus;

public class CompetitionException {

	private String message;
	private HttpStatus httpStatus;

	public CompetitionException(String message, HttpStatus httpStatus) {
		super();
		this.message = message;
		this.httpStatus = httpStatus;
	}

	public String getMessage() {
		return message;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

}
