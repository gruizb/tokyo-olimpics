package com.olympics.tokyo.competitionservice.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Repository;

import com.olympics.tokyo.competitionservice.model.Phase;
import com.olympics.tokyo.competitionservice.model.PhaseEnum;

@Repository
public class PhasesByEnumImpl implements Phases {

	@Autowired
	private MessageSource source;

	@Override
	public List<Phase> findAllPhases() {
		List<Phase> phases = fillPhases();
		return phases;
	}

	private List<Phase> fillPhases() {
		List<Phase> phases = new ArrayList<Phase>();

		Locale locale = LocaleContextHolder.getLocale();
		for (PhaseEnum label : PhaseEnum.values()) {
			phases.add(new Phase(label.name(), source.getMessage(label.name(), null, locale)));
		}
		return phases;
	}

}
