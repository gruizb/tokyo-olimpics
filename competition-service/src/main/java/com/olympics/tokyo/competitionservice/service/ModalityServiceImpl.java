package com.olympics.tokyo.competitionservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.olympics.tokyo.competitionservice.exception.InvalidLocalException;
import com.olympics.tokyo.competitionservice.exception.InvalidModalityException;
import com.olympics.tokyo.competitionservice.model.Modality;
import com.olympics.tokyo.competitionservice.repository.Modalities;

@Service
public class ModalityServiceImpl implements ModalityService {
	@Autowired
	private Modalities modalities;

	@Override
	public Modality validateModality(Modality modality) throws InvalidLocalException, InvalidModalityException {
		Modality newModality = validateModalityByID(modality);
		if (newModality == null) {
			newModality = validateModalityByDescription(modality);
		}
		return newModality;
	}

	private Modality validateModalityByDescription(Modality modality) throws InvalidModalityException {
		if (modality != null && modality.getDescription() != null && !modality.getDescription().isEmpty()) {
			List<Modality> modalityByDesc = modalities.findByDescription(modality.getDescription());
			if (modalityByDesc != null && !modalityByDesc.isEmpty()) {
				return modalityByDesc.get(0);
			} else {
				throw new InvalidModalityException();
			}
		} else {
			throw new InvalidModalityException();

		}
	}

	private Modality validateModalityByID(Modality modality) throws InvalidModalityException {
		if (modality != null && modality.getId() != null) {
			modality = modalities.findOne(modality.getId());
			if (modality != null) {
				return modality;
			} else {
				throw new InvalidModalityException();
			}
		}
		return null;
	}


}
