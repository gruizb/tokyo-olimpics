package com.olympics.tokyo.competitionservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.olympics.tokyo.competitionservice.model.Local;
import com.olympics.tokyo.competitionservice.repository.Locals;

@RestController
public class LocalController {

	@Autowired
	private Locals locals;

	@RequestMapping(method = RequestMethod.GET, path ="/local")
	public Iterable<Local> findLocals() {
		return locals.findAll();
	}

}
