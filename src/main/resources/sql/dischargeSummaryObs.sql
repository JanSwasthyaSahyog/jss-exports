select admission_indication.value_text admission_indication, hospital_course.value_text hospital_course, advice_on_discharge.value_text advice_on_discharge,
  date(followup_date.value_datetime) followup_date,
  operative_procedure.value_text operative_procedure,
  date(date_of_operation.value_datetime) date_of_operation,
  surgical_indication.value_text surgical_indication
from patient_identifier
  LEFT JOIN
  (select person_id, value_text from obs
    join (select max(obs_id) max_obs_id from obs where date(obs_datetime) BETWEEN :start_date and :end_date and obs.person_id = :patient_id and obs.concept_id = 4332) last_obs on last_obs.max_obs_id = obs.obs_id) admission_indication on admission_indication.person_id = patient_identifier.patient_id
  LEFT JOIN
  (select person_id, value_text from obs
    join (select max(obs_id) max_obs_id from obs where date(obs_datetime) BETWEEN :start_date and :end_date  and obs.person_id = :patient_id and obs.concept_id = 1198) last_obs on last_obs.max_obs_id = obs.obs_id) hospital_course on hospital_course.person_id = patient_identifier.patient_id
  LEFT JOIN
  (select person_id, value_text from obs
    join (select max(obs_id) max_obs_id from obs where date(obs_datetime) BETWEEN :start_date and :end_date  and obs.person_id = :patient_id and obs.concept_id = 1968) last_obs on last_obs.max_obs_id = obs.obs_id) advice_on_discharge on advice_on_discharge.person_id = patient_identifier.patient_id
  LEFT JOIN
  (select person_id, value_datetime from obs
    join (select max(obs_id) max_obs_id from obs where date(obs_datetime) BETWEEN :start_date and :end_date  and obs.person_id = :patient_id and obs.concept_id = 1203) last_obs on last_obs.max_obs_id = obs.obs_id) followup_date on followup_date.person_id = patient_identifier.patient_id
  LEFT JOIN
  (select person_id, value_text from obs
    join (select max(obs_id) max_obs_id from obs where date(obs_datetime) BETWEEN  :start_date and :end_date  and obs.person_id = :patient_id and obs.concept_id = 1202) last_obs on last_obs.max_obs_id = obs.obs_id) operative_procedure on operative_procedure.person_id = patient_identifier.patient_id
  LEFT JOIN
  (select person_id, value_datetime from obs
    join (select max(obs_id) max_obs_id from obs where date(obs_datetime) BETWEEN  :start_date and :end_date  and obs.person_id = :patient_id and obs.concept_id = 1201) last_obs on last_obs.max_obs_id = obs.obs_id) date_of_operation on date_of_operation.person_id = patient_identifier.patient_id
  LEFT JOIN
  (select person_id, value_text from obs
    join (select max(obs_id) max_obs_id from obs where date(obs_datetime) BETWEEN  '2017-12-15' and '2017-12-31'  and obs.person_id = 212235 and obs.concept_id = 4334) last_obs on last_obs.max_obs_id = obs.obs_id) surgical_indication on surgical_indication.person_id = patient_identifier.patient_id
where patient_identifier.patient_id = :patient_id;
