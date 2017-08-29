package com.olympics.tokyo.competitionservice.exception;

public class InvalidDatesException extends Exception {

	private static final long serialVersionUID = -4424326876693929575L;

	public static enum Reason {
		END_LOWER_THAN_INIT, LESS_THAN_TIME_LIMIT;
	}

	private Reason reason;

	public InvalidDatesException(Reason reason) {
		this.reason = reason;
	}

	public Reason getReason() {
		return reason;
	}

}
