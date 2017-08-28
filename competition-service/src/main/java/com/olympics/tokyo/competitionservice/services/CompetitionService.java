package com.olympics.tokyo.competitionservice.services;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.olympics.tokyo.competitionservice.model.Competition;
import com.olympics.tokyo.competitionservice.repository.CompetitionsSchedule;

@RestController
public class CompetitionService {

	@Autowired
	private CompetitionsSchedule schedule;

	@RequestMapping(method = RequestMethod.GET, path = "/competition")
	public Iterable<Competition> findCompetitions() {
		return schedule.findAll();
	}

	@RequestMapping(method = RequestMethod.POST, path = "/competition", consumes = "application/json")
	public ResponseEntity<?> scheduleNewCompetition(@RequestBody Competition competition) {

		Competition saved = this.schedule.save(competition);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(saved.getId())
				.toUri();

		return ResponseEntity.created(location).build();

	}

}