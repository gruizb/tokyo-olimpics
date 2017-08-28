package com.olympics.tokyo.competitionservice;

import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import com.olympics.tokyo.competitionservice.model.Local;
import com.olympics.tokyo.competitionservice.model.Modality;
import com.olympics.tokyo.competitionservice.repository.Locals;
import com.olympics.tokyo.competitionservice.repository.Modalities;

@SpringBootApplication
public class CompetitionServiceApplication {

	@Autowired
	private Locals locals;

	@Autowired
	private Modalities modalities;

	public static void main(String[] args) {
		SpringApplication.run(CompetitionServiceApplication.class, args);
	}

	@Bean
	public LocaleResolver localeResolver() {
		AcceptHeaderLocaleResolver slr = new AcceptHeaderLocaleResolver();
		slr.setDefaultLocale(Locale.US);
		return slr;
	}

	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasenames("i18n/messages");
		source.setUseCodeAsDefaultMessage(true);
		source.setDefaultEncoding("UTF-8");

		return source;
	}

	@PostConstruct
	public void initData() {

		locals.save(new Local("Kokugikan Arena"));
		locals.save(new Local("Nippon Budokan"));
		locals.save(new Local("Ariake Arena"));
		locals.save(new Local("Ariake Coliseum"));
		locals.save(new Local("Enoshima"));
		locals.save(new Local("Baji Koen"));
		locals.save(new Local("Sapporo Dome"));
		locals.save(new Local("Miyagi Stadium"));

		modalities.save(new Modality("Soccer"));
		modalities.save(new Modality("Basketball"));
		modalities.save(new Modality("Volleyball"));
		modalities.save(new Modality("Handball"));
		modalities.save(new Modality("Boxing"));

	}
}
