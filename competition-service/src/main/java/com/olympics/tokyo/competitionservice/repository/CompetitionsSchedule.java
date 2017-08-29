package com.olympics.tokyo.competitionservice.repository;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.olympics.tokyo.competitionservice.model.Competition;
import com.olympics.tokyo.competitionservice.model.Local;
import com.olympics.tokyo.competitionservice.model.Modality;

public interface CompetitionsSchedule extends CrudRepository<Competition, Serializable> {

	@Query("SELECT c FROM Competition c WHERE c.modality.description = ?1")
	public Iterable<Competition> findByModality(String modality);

	@Query("SELECT c FROM Competition c WHERE (c.initDate <= ?2 AND c.endDate >= ?1) and c.local = ?3")
	public List<Competition> competitionsByTimeAndLocal(Calendar initDate, Calendar endDate, Local local);

	@Query("SELECT c FROM Competition c WHERE (c.initDate <= ?2 AND c.endDate >= ?1) and c.local = ?3 and c.modality = ?4")
	public List<Competition> competitionsByTimeAndLocalAndModality(Calendar initDate, Calendar endDate, Local local,
			Modality modality);

}
