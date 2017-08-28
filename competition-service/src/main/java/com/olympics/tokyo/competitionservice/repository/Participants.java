package com.olympics.tokyo.competitionservice.repository;

import java.util.List;

import com.olympics.tokyo.competitionservice.model.Country;

public interface Participants {

	public List<Country> findAllParticipants();

}
