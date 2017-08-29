package com.olympics.tokyo.competitionservice.service;

import com.olympics.tokyo.competitionservice.exception.InvalidLocalException;
import com.olympics.tokyo.competitionservice.model.Local;

public interface LocalService {
	public Local validateLocal(Local local) throws InvalidLocalException;
}
