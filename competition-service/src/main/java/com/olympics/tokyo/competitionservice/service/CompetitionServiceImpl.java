package com.olympics.tokyo.competitionservice.service;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.olympics.tokyo.competitionservice.exception.IncompleteCompetitionException;
import com.olympics.tokyo.competitionservice.exception.InvalidDatesException;
import com.olympics.tokyo.competitionservice.exception.InvalidLocalException;
import com.olympics.tokyo.competitionservice.exception.InvalidModalityException;
import com.olympics.tokyo.competitionservice.exception.InvalidSameCountryPhaseException;
import com.olympics.tokyo.competitionservice.model.Competition;
import com.olympics.tokyo.competitionservice.model.Local;
import com.olympics.tokyo.competitionservice.repository.CompetitionsSchedule;

@Service
public class CompetitionServiceImpl implements CompetitionService {

	@Autowired
	private CompetitionsSchedule schedule;

	@Autowired
	private LocalService localService;

	@Autowired
	private ModalityService modalityService;

	public Competition scheduleNewCompetition(Competition competition) throws IncompleteCompetitionException,
			InvalidSameCountryPhaseException, InvalidDatesException, InvalidLocalException, InvalidModalityException {

		validateCompetition(competition);

		Competition newCompetition = this.schedule.save(competition);

		return newCompetition;
	}

	private void validateCompetition(Competition competition) throws IncompleteCompetitionException,
			InvalidSameCountryPhaseException, InvalidDatesException, InvalidLocalException, InvalidModalityException {
		competition.validateCompetition();

		competition.setLocal(localService.validateLocal(competition.getLocal()));

		competition.setModality(modalityService.validateModality(competition.getModality()));

		validateSameTimeEvents(competition);

		validateSameDayEvents(competition);
	}

	private void validateSameDayEvents(Competition competition) throws InvalidLocalException {

		int totalDayOne = countByDateAndLocal(getInitDay(competition.getInitDate()),
				getEndDay(competition.getInitDate()), competition.getLocal());
		int totalDayTwo = countByDateAndLocal(getInitDay(competition.getEndDate()), getEndDay(competition.getEndDate()),
				competition.getLocal());

		if (totalDayOne >= 4 || totalDayTwo >= 4) {
			throw new InvalidLocalException(InvalidLocalException.Reason.LIMIT_EXCEEDED);
		}

	}

	private Calendar getInitDay(Calendar date) {
		Calendar clone = (Calendar) date.clone();
		clone.set(Calendar.HOUR_OF_DAY, 0);
		clone.set(Calendar.MINUTE, 0);
		clone.set(Calendar.SECOND, 0);
		clone.set(Calendar.MILLISECOND, 0);
		return clone;
	}

	private Calendar getEndDay(Calendar date) {
		Calendar clone = (Calendar) date.clone();
		clone.set(Calendar.HOUR_OF_DAY, 23);
		clone.set(Calendar.MINUTE, 59);
		clone.set(Calendar.SECOND, 59);
		clone.set(Calendar.MILLISECOND, 999);
		return clone;
	}

	private int countByDateAndLocal(Calendar initDate, Calendar endDate, Local local) {
		List<Competition> competitionsAtSameTime = schedule.competitionsByTimeAndLocal(initDate, endDate, local);

		return competitionsAtSameTime == null ? 0 : competitionsAtSameTime.size();
	}

	private void validateSameTimeEvents(Competition competition) throws InvalidLocalException {
		List<Competition> competitionsAtSameTime = schedule.competitionsByTimeAndLocalAndModality(
				competition.getInitDate(), competition.getEndDate(), competition.getLocal(), competition.getModality());

		if (competitionsAtSameTime != null && !competitionsAtSameTime.isEmpty())
			throw new InvalidLocalException(InvalidLocalException.Reason.ALREADY_IN_USE);
	}
}
