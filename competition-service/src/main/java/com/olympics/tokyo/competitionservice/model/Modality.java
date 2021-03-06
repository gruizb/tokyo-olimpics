package com.olympics.tokyo.competitionservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Modality {
	@Id
	@GeneratedValue
	private Long id;

	@Column
	private String description;

	public Modality() {

	}

	public Modality(String description) {
		super();
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
