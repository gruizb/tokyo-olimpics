package com.olympics.tokyo.competitionservice.exception;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * 
 * Classe central para trativa de exceptions, garantindo que as mensagens sejam de fácil
 * compreensão
 * 
 * @author gruizb
 *
 */
@RestControllerAdvice
public class CompetitionExceptionHandler {

	@Autowired
	private MessageSource messageSource;

	/**
	 * Quando o local está em uso / excedeu o limite / Não existe
	 * 
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler({ InvalidLocalException.class })
	public ResponseEntity<Object> handleInvalidLocalException(InvalidLocalException ex, WebRequest request) {

		Locale locale = LocaleContextHolder.getLocale();
		String errorMessage = messageSource.getMessage("InvalidLocalException_" + ex.getReason(), null, locale);

		CompetitionException apiError = new CompetitionException(errorMessage, HttpStatus.BAD_REQUEST);
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getHttpStatus());
	}

	/**
	 * 
	 * Quando as datas são inválidas: Data inicio maior que data fim / Tempo do evento
	 * inferior a 30 minutos
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler({ InvalidDatesException.class })
	public ResponseEntity<Object> handleInvalidDatesException(InvalidDatesException ex, WebRequest request) {

		Locale locale = LocaleContextHolder.getLocale();
		String errorMessage = messageSource.getMessage("InvalidDatesException_" + ex.getReason(), null, locale);

		CompetitionException apiError = new CompetitionException(errorMessage, HttpStatus.BAD_REQUEST);
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getHttpStatus());
	}
	/**
	 * Existem atributos da competição sem preencher
	 * 
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler({ IncompleteCompetitionException.class })
	public ResponseEntity<Object> handleIncompleteCompetitionException(IncompleteCompetitionException ex,
			WebRequest request) {
		Object[] args = new Object[1];
		args[0] = ex.getFields();
		Locale locale = LocaleContextHolder.getLocale();
		String errorMessage = messageSource.getMessage("IncompleteCompetitionException", args, locale);

		CompetitionException apiError = new CompetitionException(errorMessage, HttpStatus.BAD_REQUEST);
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getHttpStatus());
	}

	/**
	 * 
	 * Quando os competidores são do mesmo pais em uma etapa inválida
	 * 
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler({ InvalidSameCountryPhaseException.class })
	public ResponseEntity<Object> handleInvalidSameCountryPhaseException(InvalidSameCountryPhaseException ex,
			WebRequest request) {

		Locale locale = LocaleContextHolder.getLocale();
		String errorMessage = messageSource.getMessage("InvalidSameCountryPhaseException", null, locale);

		CompetitionException apiError = new CompetitionException(errorMessage, HttpStatus.BAD_REQUEST);
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getHttpStatus());
	}

	/**
	 * Quando modalidade não existe
	 * 
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler({ InvalidModalityException.class })
	public ResponseEntity<Object> handleInvalidModalityException(InvalidModalityException ex,
			WebRequest request) {

		Locale locale = LocaleContextHolder.getLocale();
		String errorMessage = messageSource.getMessage("InvalidModalityException", null, locale);

		CompetitionException apiError = new CompetitionException(errorMessage, HttpStatus.BAD_REQUEST);
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getHttpStatus());
	}

	/**
	 * 
	 * Erro genérico de bind. Aqui é validado se os enums de etapa e país foram preenchidos
	 * com valores válidos. Caso contrário é emitido um erro informando o recurso para
	 * consulta dos valores disponíveis
	 * 
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler({ HttpMessageNotReadableException.class })
	public ResponseEntity<Object> handleEnumErrors(HttpMessageNotReadableException ex, WebRequest request) {

		boolean phase = ex.getMessage().contains("PhaseEnum");
		boolean country = ex.getMessage().contains("CountryEnum");
		Locale locale = LocaleContextHolder.getLocale();

		CompetitionException apiError;
		if (phase) {
			String errorMessage = messageSource.getMessage("InexistentPhase", null, locale);
			apiError = new CompetitionException(errorMessage, HttpStatus.BAD_REQUEST);

		} else if (country) {
			String errorMessage = messageSource.getMessage("InexistentCountry", null, locale);
			apiError = new CompetitionException(errorMessage, HttpStatus.BAD_REQUEST);

		} else {
			//Se não for etapa ou país, então se trata de algum erro não mapeado
			//e então devolvemos erro de servidor, com a exception de origem
			apiError = new CompetitionException(ex.getLocalizedMessage(), HttpStatus.BAD_GATEWAY);

		}

		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getHttpStatus());
	}
}
