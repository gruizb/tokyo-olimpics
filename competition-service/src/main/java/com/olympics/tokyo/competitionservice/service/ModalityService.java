package com.olympics.tokyo.competitionservice.service;

import com.olympics.tokyo.competitionservice.exception.InvalidLocalException;
import com.olympics.tokyo.competitionservice.exception.InvalidModalityException;
import com.olympics.tokyo.competitionservice.model.Modality;

public interface ModalityService {
	public Modality validateModality(Modality modality) throws InvalidLocalException, InvalidModalityException;

}
