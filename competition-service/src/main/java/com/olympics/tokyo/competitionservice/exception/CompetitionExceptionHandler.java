package com.olympics.tokyo.competitionservice.exception;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class CompetitionExceptionHandler {

	@Autowired
	private MessageSource messageSource;
	
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Object> handleConstraintViolation(Exception ex, WebRequest request) {

		Locale locale = LocaleContextHolder.getLocale();
		String errorMessage = messageSource.getMessage("erro", null, locale);

		CompetitionException apiError = new CompetitionException(ex.getMessage(), HttpStatus.BAD_REQUEST);
		return new ResponseEntity<Object>(errorMessage, new HttpHeaders(), apiError.getHttpStatus());
	}

}
