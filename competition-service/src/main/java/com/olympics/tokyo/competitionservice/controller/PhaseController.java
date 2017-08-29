package com.olympics.tokyo.competitionservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.olympics.tokyo.competitionservice.model.Phase;
import com.olympics.tokyo.competitionservice.repository.Phases;

@RestController
public class PhaseController {

	@Autowired
	private Phases phases;

	@RequestMapping(method = RequestMethod.GET, path = "/phase")
	public List<Phase> findPhases() {
		return phases.findAllPhases();
	}

}
