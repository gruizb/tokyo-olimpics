package com.olympics.tokyo.competitionservice.service;

import com.olympics.tokyo.competitionservice.exception.IncompleteCompetitionException;
import com.olympics.tokyo.competitionservice.exception.InvalidDatesException;
import com.olympics.tokyo.competitionservice.exception.InvalidLocalException;
import com.olympics.tokyo.competitionservice.exception.InvalidModalityException;
import com.olympics.tokyo.competitionservice.exception.InvalidSameCountryPhaseException;
import com.olympics.tokyo.competitionservice.model.Competition;

public interface CompetitionService {
	public Competition scheduleNewCompetition(Competition competition)
			throws IncompleteCompetitionException, InvalidSameCountryPhaseException, InvalidDatesException,
			InvalidLocalException, InvalidModalityException;
}
