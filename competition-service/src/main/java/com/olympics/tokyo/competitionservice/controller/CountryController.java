package com.olympics.tokyo.competitionservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.olympics.tokyo.competitionservice.model.Country;
import com.olympics.tokyo.competitionservice.repository.Participants;

@RestController
public class CountryController {

	@Autowired
	private Participants participants;

	@RequestMapping(method = RequestMethod.GET, path = "/country")
	public List<Country> findCountries() {
		return participants.findAllParticipants();
	}

}
