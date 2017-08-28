package com.olympics.tokyo.competitionservice.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Repository;

import com.olympics.tokyo.competitionservice.model.Country;
import com.olympics.tokyo.competitionservice.model.CountryEnum;

@Repository
public class ParticipantsByEnumImpl implements Participants {

	@Autowired
	private MessageSource source;

	@Override
	public List<Country> findAllParticipants() {
		List<Country> countries = fillParticipants();
		return countries;
	}

	private List<Country> fillParticipants() {
		List<Country> countries = new ArrayList<Country>();

		Locale locale = LocaleContextHolder.getLocale();
		for (CountryEnum label : CountryEnum.values()) {
			countries.add(new Country(label.name(), source.getMessage(label.name(), null, locale)));
		}
		return countries;
	}

}
