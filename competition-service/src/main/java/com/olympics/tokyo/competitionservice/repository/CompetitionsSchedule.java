package com.olympics.tokyo.competitionservice.repository;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;

import com.olympics.tokyo.competitionservice.model.Competition;

public interface CompetitionsSchedule extends CrudRepository<Competition, Serializable> {

}
