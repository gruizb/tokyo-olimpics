package com.olympics.tokyo.competitionservice.repository;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;

import com.olympics.tokyo.competitionservice.model.Modality;

public interface Modalities extends CrudRepository<Modality, Serializable>{

}
