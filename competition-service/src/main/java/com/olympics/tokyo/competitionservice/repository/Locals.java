package com.olympics.tokyo.competitionservice.repository;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;

import com.olympics.tokyo.competitionservice.model.Local;

public interface Locals extends CrudRepository<Local, Serializable>{

}
