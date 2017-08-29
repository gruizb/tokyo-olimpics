package com.olympics.tokyo.competitionservice.model;

import java.util.Calendar;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.olympics.tokyo.competitionservice.exception.IncompleteCompetitionException;
import com.olympics.tokyo.competitionservice.exception.InvalidDatesException;
import com.olympics.tokyo.competitionservice.exception.InvalidSameCountryPhaseException;

@Entity
public class Competition {

	private static long TIME_LIMIT = 1800000;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	private Modality modality;

	@ManyToOne
	private Local local;

	@Temporal(TemporalType.TIMESTAMP)
	private Calendar initDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar endDate;

	@Embedded
	private Competitors competitors;

	private PhaseEnum phase;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Modality getModality() {
		return modality;
	}

	public void setModality(Modality modality) {
		this.modality = modality;
	}

	public Local getLocal() {
		return local;
	}

	public void setLocal(Local local) {
		this.local = local;
	}

	public Calendar getInitDate() {
		return initDate;
	}

	public void setInitDate(Calendar initDate) {
		this.initDate = initDate;
	}

	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	public Competitors getCompetitors() {
		return competitors;
	}

	public void setCompetitors(Competitors competitors) {
		this.competitors = competitors;
	}

	public PhaseEnum getPhase() {
		return phase;
	}

	public void setPhase(PhaseEnum phase) {
		this.phase = phase;
	}

	public void validateCompetition()
			throws IncompleteCompetitionException, InvalidSameCountryPhaseException, InvalidDatesException {

		String fields = fillIncompleteFields();

		if (fields != null && !fields.isEmpty())
			throw new IncompleteCompetitionException(fields);

		validateDates();

		competitors.validateCompetitors(this.phase);

	}

	private void validateDates() throws InvalidDatesException {
		if (initDate.after(endDate)) {
			throw new InvalidDatesException(InvalidDatesException.Reason.END_LOWER_THAN_INIT);
		}

		long initTime = initDate.getTimeInMillis();
		long endTime = endDate.getTimeInMillis();

		if ((endTime - initTime) < TIME_LIMIT) {
			throw new InvalidDatesException(InvalidDatesException.Reason.LESS_THAN_TIME_LIMIT);

		}

	}

	private String fillIncompleteFields() {
		StringBuilder errors = new StringBuilder();

		if (this.initDate == null) {
			addSeparator(errors);
			errors.append("initDate");
		}
		if (this.endDate == null) {
			addSeparator(errors);
			errors.append("endDate");
		}
		if (this.local == null) {
			addSeparator(errors);
			errors.append("local");
		}
		if (this.modality == null) {
			addSeparator(errors);
			errors.append("modality");
		}
		if (this.phase == null) {
			addSeparator(errors);
			errors.append("phase");
		}
		if (this.competitors == null) {
			addSeparator(errors);
			errors.append("competitors");
		}

		return errors.toString();
	}

	private void addSeparator(StringBuilder errors) {
		if (!errors.toString().isEmpty())
			errors.append(",");
	}

}
