package com.olympics.tokyo.competitionservice;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.olympics.tokyo.competitionservice.model.Competition;
import com.olympics.tokyo.competitionservice.model.Competitors;
import com.olympics.tokyo.competitionservice.model.CountryEnum;
import com.olympics.tokyo.competitionservice.model.Local;
import com.olympics.tokyo.competitionservice.model.Modality;
import com.olympics.tokyo.competitionservice.model.PhaseEnum;
import com.olympics.tokyo.competitionservice.repository.CompetitionsSchedule;
import com.olympics.tokyo.competitionservice.repository.Locals;
import com.olympics.tokyo.competitionservice.repository.Modalities;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CompetitionServiceApplication.class)
@WebAppConfiguration
public class CompetitionServiceApplicationTests {

	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	private MockMvc mockMvc;

	private HttpMessageConverter mappingJackson2HttpMessageConverter;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private Locals locals;

	@Autowired
	private Modalities modalities;

	@Autowired
	private CompetitionsSchedule schedule;

	@Autowired
	void setConverters(HttpMessageConverter<?>[] converters) {

		this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
				.filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().orElse(null);

		assertNotNull("the JSON message converter must not be null", this.mappingJackson2HttpMessageConverter);
	}

	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
	}

	/**
	 * Este teste deve retornar inválido, pois não é possível ter a mesma
	 * modalidade no mesmo horário no mesmo local
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	public void invalidCompetitionBySameDateAtSameLocal() throws IOException, Exception {
		schedule.deleteAll();
		Local local = locals.findAll().iterator().next();
		Modality modality = modalities.findAll().iterator().next();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.MINUTE, 31);
		Competition competition = new Competition();
		competition.setEndDate(instance);
		competition.setInitDate(Calendar.getInstance());
		competition.setCompetitors(new Competitors(CountryEnum.AR, CountryEnum.BR));
		competition.setPhase(PhaseEnum.EL);
		competition.setLocal(local);
		competition.setModality(modality);

		this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition)));

		Competition competition2 = new Competition();
		competition2.setEndDate(instance);
		competition2.setInitDate(Calendar.getInstance());
		competition2.setCompetitors(new Competitors(CountryEnum.AR, CountryEnum.BR));
		competition2.setPhase(PhaseEnum.EL);
		competition2.setLocal(local);
		competition2.setModality(modality);

		this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition)))
				.andExpect(status().isBadRequest());

	}

	/**
	 * Este teste deve retornar válido, pois a restrição apara o mesmo horário e
	 * local só ocorre quando se trata da mesma modalidade
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	public void validCompetitionBySameDateAtSameLocalOtherModality() throws IOException, Exception {
		schedule.deleteAll();
		Local local = new Local();
		local.setDescription("Ariake Arena");
		Modality modality = modalities.findAll().iterator().next();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Competition competition = createFullCompetition(local, modality, 100, 200);

		this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition)))
				.andExpect(status().isCreated());

		modality.setId(null);
		modality.setDescription("Boxing");
		Competition competition2 = createFullCompetition(local, modality, 100, 200);

		this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition2)))
				.andExpect(status().isCreated());

	}

	/**
	 * A competição é criada mesmo na omissão de um dos atributos da modalidade,
	 * desde que ao menos um seja válido
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	public void validCompetitionByModalityDescAndId() throws IOException, Exception {
		schedule.deleteAll();
		Local local = new Local();
		local.setDescription("Ariake Arena");
		Modality modality = new Modality();
		modality.setId(1l);
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Competition competition = createFullCompetition(local, modality, 100, 200);

		this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition)))
				.andExpect(status().isCreated());

		modality.setId(null);
		modality.setDescription("Boxing");
		Competition competition2 = createFullCompetition(local, modality, 100, 200);

		this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition2)))
				.andExpect(status().isCreated());

	}

	/**
	 * Erro quando o local não existir
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	public void invalidLocal() throws IOException, Exception {
		schedule.deleteAll();
		Local local = new Local();
		local.setDescription("dasdsa");
		Modality modality = new Modality();
		modality.setId(1l);
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Competition competition = createFullCompetition(local, modality, 100, 200);

		this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition)))
				.andExpect(status().isBadRequest());

	}

	/**
	 * Erro quando a modalidade não existir
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	public void invalidModality() throws IOException, Exception {
		schedule.deleteAll();
		Local local = new Local();
		local.setDescription("Ariake Arena");
		Modality modality = new Modality();
		modality.setDescription("dasdasdas");
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Competition competition = createFullCompetition(local, modality, 100, 200);

		this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition)))
				.andExpect(status().isBadRequest());

	}

	/**
	 * Quando houver mais de 4 eventos para uma mesma localidade em um dia o
	 * sistema não deve permitir a criação de mais competições neste local para
	 * o dia
	 * 
	 * Os 4 primeiros devem executar coms sucesso
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	public void invalidCompetitionBySameDateAtSameLocalDayLimit() throws IOException, Exception {
		schedule.deleteAll();
		Local local = new Local();
		local.setDescription("Ariake Arena");
		Modality modality = modalities.findAll().iterator().next();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Competition competition = createFullCompetition(local, modality, 201, 300);

		this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition)))
				.andExpect(status().isCreated());

		Competition competition2 = createFullCompetition(local, modality, 301, 400);

		this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition2)))
				.andExpect(status().isCreated());

		Competition competition3 = createFullCompetition(local, modality, 401, 500);

		this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition3)))
				.andExpect(status().isCreated());

		Competition competition4 = createFullCompetition(local, modality, 501, 600);

		this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition4)))
				.andExpect(status().isCreated());

		Competition competition5 = createFullCompetition(local, modality, 601, 700);

		this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition5)))
				.andExpect(status().isBadRequest());

	}

	/**
	 * O limite de eventos é por local. Testando a criação de mais de 4 eventos
	 * em locais distintos. Todos devem ser criados com sucesso
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	public void validCreationMultipleLocals() throws IOException, Exception {
		schedule.deleteAll();
		Local local = locals.findAll().iterator().next();
		Modality modality = modalities.findAll().iterator().next();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Competition competition = createFullCompetition(local, modality, 1000, 2000);

		this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition)))
				.andExpect(status().isCreated());

		Competition competition2 = createFullCompetition(local, modality, 2001, 3000);

		this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition2)))
				.andExpect(status().isCreated());

		Competition competition3 = createFullCompetition(local, modality, 3001, 4000);

		this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition3)))
				.andExpect(status().isCreated());

		Competition competition4 = createFullCompetition(local, modality, 4001, 5000);

		this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition4)))
				.andExpect(status().isCreated());

		Local local2 = new Local();
		local2.setDescription("Sapporo Dome");
		Competition competition5 = createFullCompetition(local2, modality, 5001, 6000);

		this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition5)))
				.andExpect(status().isCreated());

	}

	/**
	 * Popular as competições e fazer filtro por modalidade. A quantidade de elementos
	 * filtrados deve corresponder com o que foi inserido
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	public void testModalityFilter() throws IOException, Exception {
		schedule.deleteAll();
		Local local = locals.findAll().iterator().next();
		Modality modality = new Modality();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		modality.setId(1l);
		Competition competition = createFullCompetition(local, modality, 1000, 2000);
		this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition)))
				.andExpect(status().isCreated());

		modality.setId(2l);
		Competition competition2 = createFullCompetition(local, modality, 2001, 3000);
		this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition2)))
				.andExpect(status().isCreated());

		modality.setId(3l);
		Competition competition3 = createFullCompetition(local, modality, 3001, 4000);
		this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition3)))
				.andExpect(status().isCreated());

		modality.setId(1l);
		Competition competition4 = createFullCompetition(local, modality, 4001, 5000);
		this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition4)))
				.andExpect(status().isCreated());

		modality.setId(1l);
		Competition competition5 = createFullCompetition(local, modality, 5001, 6000);
		this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition5)))
				.andExpect(status().isCreated());

		long mod = 1;
		int qt = 3;
		runFilterModality(mod, qt);

		mod = 2;
		qt = 1;
		runFilterModality(mod, qt);

		mod = 3;
		qt = 1;
		runFilterModality(mod, qt);
	}

	private void runFilterModality(long id, int qt)
			throws Exception, IOException, JsonParseException, JsonMappingException {
		MvcResult andReturn = this.mockMvc
				.perform(get("/competition?modality=" + modalities.findOne(id).getDescription()).contentType(contentType))
				.andExpect(status().isOk()).andReturn();

		byte[] contentAsByteArray = andReturn.getResponse().getContentAsByteArray();

		ObjectMapper objectMapper = new ObjectMapper();
		Competition[] newCompetition = objectMapper.readValue(contentAsByteArray, Competition[].class);
		assertTrue(newCompetition.length == qt);
	}

	private Competition createFullCompetition(Local local, Modality modality, int init, int end) {
		Calendar initDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		initDate.add(Calendar.MINUTE, init);
		endDate.add(Calendar.MINUTE, end);
		Competition competition = new Competition();
		competition.setInitDate(initDate);
		competition.setEndDate(endDate);
		competition.setCompetitors(new Competitors(CountryEnum.AR, CountryEnum.BR));
		competition.setPhase(PhaseEnum.EL);
		competition.setLocal(local);
		competition.setModality(modality);
		return competition;
	}

	/**
	 * Nas finais e semifinais deve ser possivel que os dois competidores sejam
	 * do mesmo pais
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	public void insertValidCompetitionSameCountry() throws IOException, Exception {
		Local local = locals.findAll().iterator().next();
		Modality modality = modalities.findAll().iterator().next();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.MINUTE, 31);
		Competition competition = new Competition();
		competition.setEndDate(instance);
		competition.setInitDate(Calendar.getInstance());
		competition.setCompetitors(new Competitors(CountryEnum.AR, CountryEnum.AR));
		competition.setPhase(PhaseEnum.F);
		competition.setLocal(local);
		competition.setModality(modality);

		MvcResult ret = this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition)))
				.andExpect(status().isCreated()).andReturn();

		String location = ret.getResponse().getHeader("Location");
		Pattern pattern = Pattern.compile("(\\d+)$");
		Matcher matcher = pattern.matcher(location);
		matcher.find();
		Long id = Long.parseLong(matcher.group(), 10);

		Competition newCompetition = schedule.findOne(id);

		verifyCompetition(competition, newCompetition);

	}

	/**
	 * Teste de inserção básico validando no banco de dados após inserção
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	public void insertValidCompetition() throws IOException, Exception {
		schedule.deleteAll();
		List<Local> local = locals.findByDescription("Enoshima");
		Modality modality = modalities.findAll().iterator().next();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.MINUTE, 31);
		Competition competition = new Competition();
		competition.setEndDate(instance);
		competition.setInitDate(Calendar.getInstance());
		competition.setCompetitors(new Competitors(CountryEnum.AR, CountryEnum.BR));
		competition.setPhase(PhaseEnum.EL);
		competition.setLocal(local.get(0));
		competition.setModality(modality);

		MvcResult ret = this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition)))
				.andExpect(status().isCreated()).andReturn();

		String location = ret.getResponse().getHeader("Location");
		Pattern pattern = Pattern.compile("(\\d+)$");
		Matcher matcher = pattern.matcher(location);
		matcher.find();
		Long id = Long.parseLong(matcher.group(), 10);

		Competition newCompetition = schedule.findOne(id);

		verifyCompetition(competition, newCompetition);

	}

	/**
	 * Teste de inserção básico validando em uma nova busca
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	public void insertValidCompetitionValidateREST() throws IOException, Exception {
		schedule.deleteAll();
		List<Local> local = locals.findByDescription("Enoshima");
		Modality modality = modalities.findAll().iterator().next();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.MINUTE, 31);
		Competition competition = new Competition();
		competition.setEndDate(instance);
		competition.setInitDate(Calendar.getInstance());
		competition.setCompetitors(new Competitors(CountryEnum.AR, CountryEnum.BR));
		competition.setPhase(PhaseEnum.EL);
		competition.setLocal(local.get(0));
		competition.setModality(modality);

		MvcResult ret = this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition)))
				.andExpect(status().isCreated()).andReturn();

		String location = ret.getResponse().getHeader("Location");
		Pattern pattern = Pattern.compile("(\\d+)$");
		Matcher matcher = pattern.matcher(location);
		matcher.find();
		Long id = Long.parseLong(matcher.group(), 10);

		MvcResult andReturn = this.mockMvc.perform(get("/competition/" + id)).andExpect(status().isOk()).andReturn();

		byte[] contentAsByteArray = andReturn.getResponse().getContentAsByteArray();

		ObjectMapper objectMapper = new ObjectMapper();
		Competition newCompetition = objectMapper.readValue(contentAsByteArray, Competition.class);
		verifyCompetition(competition, newCompetition);

	}

	/**
	 * Teste com o local sem o id, mas com descrição válida. O sistema deve
	 * buscar a descrição no banco e criar a competição com sucesso
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	public void insertValidCompetitionWithoutLocalId() throws IOException, Exception {
		Modality modality = modalities.findAll().iterator().next();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.MINUTE, 31);
		Competition competition = new Competition();
		competition.setEndDate(instance);
		competition.setInitDate(Calendar.getInstance());
		competition.setCompetitors(new Competitors(CountryEnum.AR, CountryEnum.BR));
		competition.setPhase(PhaseEnum.EL);
		Local local = new Local();
		local.setDescription("Baji Koen");
		competition.setLocal(local);
		competition.setModality(modality);

		MvcResult ret = this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition)))
				.andExpect(status().isCreated()).andReturn();

		String location = ret.getResponse().getHeader("Location");
		Pattern pattern = Pattern.compile("(\\d+)$");
		Matcher matcher = pattern.matcher(location);
		matcher.find();
		Long id = Long.parseLong(matcher.group(), 10);

		Competition newCompetition = schedule.findOne(id);

		verifyCompetition(competition, newCompetition);

	}

	/**
	 * Teste com o local com o id válido, mas sem descrição. O sistema deve
	 * buscar o id no banco e criar a competição com sucesso
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	public void insertValidCompetitionWithoutLocalDesc() throws IOException, Exception {
		Modality modality = modalities.findAll().iterator().next();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Calendar init = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		init.add(Calendar.MINUTE, 100);
		end.add(Calendar.MINUTE, 200);
		Competition competition = new Competition();
		competition.setInitDate(init);
		competition.setEndDate(end);
		competition.setCompetitors(new Competitors(CountryEnum.AR, CountryEnum.BR));
		competition.setPhase(PhaseEnum.EL);
		Local local = new Local();
		local.setId(5l);
		competition.setLocal(local);
		competition.setModality(modality);

		MvcResult ret = this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition)))
				.andExpect(status().isCreated()).andReturn();

		String location = ret.getResponse().getHeader("Location");
		Pattern pattern = Pattern.compile("(\\d+)$");
		Matcher matcher = pattern.matcher(location);
		matcher.find();
		Long id = Long.parseLong(matcher.group(), 10);

		Competition newCompetition = schedule.findOne(id);

		verifyCompetition(competition, newCompetition);

	}

	/**
	 * Uma competição só é válida se tiver pelo menos 30 minutos. Deve retornar
	 * erro por estar abaixo deste valor
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	public void invalidDateByTimeLimit() throws IOException, Exception {

		Local local = locals.findAll().iterator().next();
		Modality modality = modalities.findAll().iterator().next();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.MINUTE, 25);
		Competition competition = new Competition();
		competition.setEndDate(instance);
		competition.setInitDate(Calendar.getInstance());
		competition.setCompetitors(new Competitors(CountryEnum.AR, CountryEnum.BR));
		competition.setPhase(PhaseEnum.EL);
		competition.setLocal(local);
		competition.setModality(modality);

		this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition)))
				.andExpect(status().isBadRequest());

	}

	/**
	 * Data fim não pode ser menor que data inicio
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	public void invalidDateByDateOrder() throws IOException, Exception {
		schedule.deleteAll();

		Local local = locals.findAll().iterator().next();
		Modality modality = modalities.findAll().iterator().next();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.MINUTE, 25);
		Competition competition = new Competition();
		competition.setEndDate(Calendar.getInstance());
		competition.setInitDate(instance);
		competition.setCompetitors(new Competitors(CountryEnum.AR, CountryEnum.BR));
		competition.setPhase(PhaseEnum.EL);
		competition.setLocal(local);
		competition.setModality(modality);

		this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition)))
				.andExpect(status().isBadRequest());
	}

	/**
	 * Todos os campos são necessários para criação de uma competição
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	public void invalidCompetitionByNullFields() throws IOException, Exception {
		schedule.deleteAll();

		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.MINUTE, 25);
		Competition competition = new Competition();
		competition.setEndDate(instance);
		competition.setInitDate(Calendar.getInstance());
		competition.setCompetitors(new Competitors(CountryEnum.AR, CountryEnum.BR));
		competition.setPhase(PhaseEnum.EL);

		this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition)))
				.andExpect(status().isBadRequest());

	}

	/**
	 * Em uma competição que não seja final ou semifinal não deve ser possível
	 * que os dois competidores sejam do mesmo pais
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	public void invalidCompetitionSameCountry() throws IOException, Exception {
		schedule.deleteAll();

		Modality modality = modalities.findAll().iterator().next();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.MINUTE, 25);
		Competition competition = new Competition();
		competition.setEndDate(instance);
		competition.setInitDate(Calendar.getInstance());
		competition.setCompetitors(new Competitors(CountryEnum.AR, CountryEnum.AR));
		competition.setPhase(PhaseEnum.EL);
		competition.setModality(modality);

		this.mockMvc.perform(post("/competition").contentType(contentType).content(json(competition)))
				.andExpect(status().isBadRequest());

	}

	/**
	 * Validação da competição recebida com a que foi persistida
	 * 
	 * @param competition
	 * @param newCompetition
	 */
	private void verifyCompetition(Competition competition, Competition newCompetition) {

		assertTrue(validateLocal(competition, newCompetition));
		assertTrue(validateModality(competition, newCompetition));
		assertTrue(competition.getPhase().equals(newCompetition.getPhase()));
		assertTrue(competition.getInitDate().equals(newCompetition.getInitDate()));
		assertTrue(competition.getEndDate().equals(newCompetition.getEndDate()));
		assertTrue(
				competition.getCompetitors().getCompetitor1().equals(newCompetition.getCompetitors().getCompetitor1()));
		assertTrue(
				competition.getCompetitors().getCompetitor2().equals(newCompetition.getCompetitors().getCompetitor2()));

	}

	private boolean validateModality(Competition competition, Competition newCompetition) {
		return competition.getModality().getId().equals(newCompetition.getModality().getId());
	}

	private boolean validateLocal(Competition competition, Competition newCompetition) {
		if (competition.getLocal().getId() != null) {
			return competition.getLocal().getId().equals(newCompetition.getLocal().getId());
		} else if (competition.getLocal().getDescription() != null
				&& !competition.getLocal().getDescription().isEmpty()) {
			return competition.getLocal().getDescription().equals(newCompetition.getLocal().getDescription());

		}

		return false;
	}

	private String json(Object o) throws IOException {
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}

}
