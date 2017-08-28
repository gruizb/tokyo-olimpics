package com.olympics.tokyo.competitionservice.model;

public enum PhaseEnum {
	EL(false), F8(false), F4(false), SF(true), F(true);
	boolean duplicatedCompetitor;
	
	PhaseEnum(boolean duplicatedCompetitor) {
		this.duplicatedCompetitor = duplicatedCompetitor;
	}

	public boolean allowSameCompetitor() {
		return duplicatedCompetitor;
	}
}