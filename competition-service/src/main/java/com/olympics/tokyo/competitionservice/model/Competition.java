package com.olympics.tokyo.competitionservice.model;

import java.util.Calendar;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Competition {

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

	public void validateCompetition() throws Exception {
		competitors.validateCompetitors(this.phase);
		
	}

}
