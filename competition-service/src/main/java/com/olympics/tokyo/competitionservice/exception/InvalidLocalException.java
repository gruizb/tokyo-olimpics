package com.olympics.tokyo.competitionservice.exception;

public class InvalidLocalException extends Exception {
	private static final long serialVersionUID = -4424326876693929575L;

	public static enum Reason {
		ALREADY_IN_USE, LIMIT_EXCEEDED, INEXISTENT;
	}

	private Reason reason;

	public InvalidLocalException(Reason reason) {
		this.reason = reason;
	}

	public Reason getReason() {
		return reason;
	}
}