select distinct IFNULL(coded_diagnosis.name, diagnosis.value_text) diagnosis,
                certainty.name certainty,
                diagnosis_order.name "order"
from obs diagnosis
  inner join person on diagnosis.person_id = person.person_id and person.person_id = :patient_id
  inner join concept_name on diagnosis.concept_id = concept_name.concept_id and  concept_name_type = 'FULLY_SPECIFIED' and
                             concept_name.name in ('Non-coded Diagnosis', 'Coded Diagnosis')
  inner join encounter on diagnosis.encounter_id = encounter.encounter_id
  inner join visit on encounter.visit_id = visit.visit_id and visit.visit_type_id = :visit_type_id
  left join concept_name coded_diagnosis on diagnosis.value_coded = coded_diagnosis.concept_id
  inner join obs certainty_obs on diagnosis.obs_group_id = certainty_obs.obs_group_id and certainty_obs.concept_id = :diagnosis_certainty_concept_id
  inner join concept_name certainty on certainty_obs.value_coded = certainty.concept_id
  inner join obs order_obs on diagnosis.obs_group_id = order_obs.obs_group_id and order_obs.concept_id = :diagnosis_order_concept_id
  inner join concept_name diagnosis_order on order_obs.value_coded = diagnosis_order.concept_id
  left join obs status_obs on diagnosis.obs_group_id = status_obs.obs_group_id and status_obs.concept_id = :diagnosis_status_concept_id
  where status_obs.value_coded is NULL and date(diagnosis.obs_datetime) BETWEEN :start_date and :end_date;
