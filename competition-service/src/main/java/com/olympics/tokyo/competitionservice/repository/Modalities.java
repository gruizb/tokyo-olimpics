package com.olympics.tokyo.competitionservice.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.olympics.tokyo.competitionservice.model.Modality;

public interface Modalities extends CrudRepository<Modality, Serializable> {

	public List<Modality> findByDescription(String description);

}
