package com.olympics.tokyo.competitionservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.olympics.tokyo.competitionservice.model.Modality;
import com.olympics.tokyo.competitionservice.repository.Modalities;

@RestController
public class ModalityService {

	@Autowired
	private Modalities modalities;

	@RequestMapping(method = RequestMethod.GET, path = "/modality")
	public Iterable<Modality> findModalities() {
		return modalities.findAll();
	}

}
