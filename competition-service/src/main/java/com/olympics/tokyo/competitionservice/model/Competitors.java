package com.olympics.tokyo.competitionservice.model;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class Competitors {

	@Enumerated(EnumType.STRING)
	private CountryEnum competitor1;
	@Enumerated(EnumType.STRING)
	private CountryEnum competitor2;

	public CountryEnum getCompetitor1() {
		return competitor1;
	}

	public void setCompetitor1(CountryEnum competitor1) {
		this.competitor1 = competitor1;
	}

	public CountryEnum getCompetitor2() {
		return competitor2;
	}

	public void setCompetitor2(CountryEnum competitor2) {
		this.competitor2 = competitor2;
	}

	public void validateCompetitors(PhaseEnum phase) throws Exception {
		if (competitor1 == null || competitor2 == null)
			throw new Exception();

		if (competitor1.equals(competitor2) && !phase.allowSameCompetitor())
			throw new Exception();

	}

}
