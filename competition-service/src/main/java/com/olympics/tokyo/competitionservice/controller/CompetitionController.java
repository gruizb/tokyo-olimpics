package com.olympics.tokyo.competitionservice.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.olympics.tokyo.competitionservice.exception.IncompleteCompetitionException;
import com.olympics.tokyo.competitionservice.exception.InvalidDatesException;
import com.olympics.tokyo.competitionservice.exception.InvalidLocalException;
import com.olympics.tokyo.competitionservice.exception.InvalidModalityException;
import com.olympics.tokyo.competitionservice.exception.InvalidSameCountryPhaseException;
import com.olympics.tokyo.competitionservice.model.Competition;
import com.olympics.tokyo.competitionservice.repository.CompetitionsSchedule;
import com.olympics.tokyo.competitionservice.service.CompetitionService;

/**
 * 
 * Controller responsável por buscar e cadastrar novas competições
 * 
 * @author gruizb
 *
 */
@RestController
public class CompetitionController {

	@Autowired
	private CompetitionsSchedule schedule;

	@Autowired
	private CompetitionService service;
	
	/**
	 * Busca todas as competições ordenadas por data. É possível filtrar por modalidade
	 * @param modality
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/competition")
	public Iterable<Competition> findCompetitions(@RequestParam(required = false) String modality) {
		Iterable<Competition> competitions;
		if (modality != null && !modality.isEmpty()) {
			competitions = schedule.findByModality(modality);
		} else {
			competitions = schedule.findAll();

		}
		List<Competition> listOfCompetitions = new ArrayList<Competition>();
		if (competitions != null) {
			competitions.forEach(c -> listOfCompetitions.add(c));
			Collections.sort(listOfCompetitions, (o1, o2) -> o1.getInitDate().compareTo(o2.getInitDate()));
		}
		return listOfCompetitions;
	}

	/**
	 * Busca uma competição através de seu ID
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/competition/{id}")
	public Competition findCompetition(@PathVariable Long id) {
		return schedule.findOne(id);
	}

	/**
	 * 
	 * Inserção de uma nova competição.
	 * 
	 * Regras de validação:
	 * 
	 * 1. Todos os campos devem estar preenchidos;
	 * 2. Os objetos modality e local podem ter ou o id, ou a description;
	 * 3. A data de início não pode ser maior que a data de fim;
	 * 4. Os competidores só podem pertencer ao mesmo país quando a phase for F ou SF
	 * 5. As modalidades, locais, paises e etapas devem ser válidas
	 * 6. Em um mesmo dia, um local pode ter até 4 eventos
	 * 7. No mesmo momento não pode haver uma competição da mesma modalidade e local
	 * 8. O tempo mínimo de cada evento é de 30 minutos
	 * 
	 * 
	 * Exemplo de uma nova competição:
	 * {
	 *       "modality": {
	 *           "description": "Basketball"
	 *       },
	 *       "local": {
	 *           "id": 1,
	 *           "description": "Enoshima"
	 *       },
	 *       "initDate": "2017-15-01T19:46:00.000+02:00",
	 *       "endDate": "2017-15-01T20:45:00.000+02:00",
	 *       "competitors": {
	 *           "competitor1": "AR",
	 *           "competitor2": "BR"
	 *       },
	 *       "phase": "EL"
	 *   }
	 * 
	 * @param competition
	 * @return
	 * @throws IncompleteCompetitionException Quando faltam campos para preenchimento
	 * @throws InvalidSameCountryPhaseException Quando os competidores são do mesmo pais em uma etapa inválida
	 * @throws InvalidDatesException Quando as datas são inválidas: itens 3 e 8
	 * @throws InvalidLocalException Quando o local está em uso / excedeu o limite / Não existe
	 * @throws InvalidModalityException Quando a modalidade não existe
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/competition", consumes = "application/json")
	public ResponseEntity<?> scheduleNewCompetition(@RequestBody Competition competition)
			throws IncompleteCompetitionException, InvalidSameCountryPhaseException, InvalidDatesException,
			InvalidLocalException, InvalidModalityException {

		Competition newCompetition = service.scheduleNewCompetition(competition);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(newCompetition.getId()).toUri();

		return ResponseEntity.created(location).build();

	}

}