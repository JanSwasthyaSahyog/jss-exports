select pulse.value_numeric pulse, systolic.value_numeric systolic, diastolic.value_numeric diastolic,
  temperature.value_numeric temperature, weight.value_numeric weight, height.value_numeric height from patient_identifier
  LEFT JOIN
  (select person_id, value_numeric from obs
  join (select min(obs_id) min_obs_id from obs where date(obs_datetime) BETWEEN date(:start_date) and DATE_ADD(date(:start_date), INTERVAL 7 DAY) and obs.person_id = :patient_id and obs.concept_id = 1153) first_obs on first_obs.min_obs_id = obs.obs_id) pulse on pulse.person_id = patient_identifier.patient_id
LEFT JOIN
  (select person_id, value_numeric from obs
    join (select min(obs_id) min_obs_id from obs where date(obs_datetime) BETWEEN date(:start_date) and DATE_ADD(date(:start_date), INTERVAL 7 DAY)  and obs.person_id = :patient_id and obs.concept_id = 1157) first_obs on first_obs.min_obs_id = obs.obs_id) systolic on systolic.person_id = patient_identifier.patient_id
LEFT JOIN
  (select person_id, value_numeric from obs
    join (select min(obs_id) min_obs_id from obs where date(obs_datetime) BETWEEN date(:start_date) and DATE_ADD(date(:start_date), INTERVAL 7 DAY)  and obs.person_id = :patient_id and obs.concept_id = 1160) first_obs on first_obs.min_obs_id = obs.obs_id) diastolic on systolic.person_id = patient_identifier.patient_id
LEFT JOIN
  (select person_id, value_numeric from obs
    join (select min(obs_id) min_obs_id from obs where date(obs_datetime) BETWEEN date(:start_date) and DATE_ADD(date(:start_date), INTERVAL 7 DAY)  and obs.person_id = :patient_id and obs.concept_id = 1163) first_obs on first_obs.min_obs_id = obs.obs_id) temperature on systolic.person_id = patient_identifier.patient_id
LEFT JOIN
  (select person_id, value_numeric from obs
    join (select min(obs_id) min_obs_id from obs where date(obs_datetime) BETWEEN date(:start_date) and DATE_ADD(date(:start_date), INTERVAL 7 DAY)  and obs.person_id = :patient_id and obs.concept_id = 6) first_obs on first_obs.min_obs_id = obs.obs_id) weight on systolic.person_id = patient_identifier.patient_id
LEFT JOIN
  (select person_id, value_numeric from obs
    join (select min(obs_id) min_obs_id from obs where date(obs_datetime) BETWEEN date(:start_date) and DATE_ADD(date(:start_date), INTERVAL 7 DAY)  and obs.person_id = :patient_id and obs.concept_id = 5) first_obs on first_obs.min_obs_id = obs.obs_id) height on systolic.person_id = patient_identifier.patient_id
where patient_identifier.patient_id = :patient_id;