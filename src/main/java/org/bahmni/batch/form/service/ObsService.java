package org.bahmni.batch.form.service;

import org.bahmni.batch.BatchUtils;
import org.bahmni.batch.form.domain.Concept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ObsService {

	@Value("classpath:sql/conceptDetails.sql")
	private Resource conceptDetailsSqlResource;

	@Value("classpath:sql/conceptList.sql")
	private Resource conceptListSqlResource;

	@Value("classpath:sql/conceptAnswers.sql")
	private Resource conceptAnswersSqlResource;

	private String conceptDetailsSql;

	private String conceptListSql;

	private String visitsWithFormFilledSql;

	private String conceptAnswersSql;

	@Value("classpath:sql/visitsWithFormFilled.sql")
	private Resource visitWithFormsFilledSqlResource;


	@Autowired
	@Qualifier("emrJDBCTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	public List<Concept> getConceptsByNames(String commaSeparatedConceptNames){
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("conceptNames", BatchUtils.convertConceptNamesToSet(commaSeparatedConceptNames));

		return jdbcTemplate.query(conceptDetailsSql,parameters,new BeanPropertyRowMapper<>(Concept.class));
	}

	public List<Concept> getChildConcepts(String parentConceptName){
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("parentConceptName",parentConceptName);

		return jdbcTemplate.query(conceptListSql, parameters, new BeanPropertyRowMapper<>(Concept.class));

	}

	public List<Concept> getAnswerConcepts(Integer questionConceptId){
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("questionConceptId",questionConceptId);

		return jdbcTemplate.query(conceptAnswersSql, parameters, new BeanPropertyRowMapper<>(Concept.class));

	}

	public Long getTotalVisitsConceptFilledIn(Concept concept) {
		Map<String,Integer> params = new HashMap<>();
		params.put("form_concept_id",concept.getId());
		Object max_visit_count = jdbcTemplate.query(visitsWithFormFilledSql, params, new ColumnMapRowMapper()).get(0).get("max_visit_count");
		return max_visit_count == null ? 0 : (Long)max_visit_count;
	}


	@PostConstruct
	public void postConstruct(){
		this.conceptDetailsSql = BatchUtils.convertResourceOutputToString(conceptDetailsSqlResource);
		this.conceptListSql = BatchUtils.convertResourceOutputToString(conceptListSqlResource);
		this.visitsWithFormFilledSql = BatchUtils.convertResourceOutputToString(visitWithFormsFilledSqlResource);
		this.conceptAnswersSql = BatchUtils.convertResourceOutputToString(conceptAnswersSqlResource);
	}


}
