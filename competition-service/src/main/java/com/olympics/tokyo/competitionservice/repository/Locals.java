package com.olympics.tokyo.competitionservice.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.olympics.tokyo.competitionservice.model.Local;

public interface Locals extends CrudRepository<Local, Serializable> {

	public List<Local> findByDescription(String description);

}
