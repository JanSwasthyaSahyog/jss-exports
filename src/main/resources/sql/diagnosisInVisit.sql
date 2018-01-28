select distinct IFNULL(coded_diagnosis.name, diagnosis.value_text) diagnosis,
certainty.name certainty,
diagnosis_order.name "order"
from obs diagnosis
inner join encounter on diagnosis.encounter_id = encounter.encounter_id and  encounter.visit_id = :visit_id
inner join concept_name on diagnosis.concept_id = concept_name.concept_id and concept_name_type = 'FULLY_SPECIFIED' and concept_name.name in ('Non-coded Diagnosis', 'Coded Diagnosis')
inner join obs certainty_obs on diagnosis.obs_group_id = certainty_obs.obs_group_id and certainty_obs.concept_id = :diagnosis_certainty_concept_id
inner join concept_name certainty on certainty_obs.value_coded = certainty.concept_id
inner join obs order_obs on diagnosis.obs_group_id = order_obs.obs_group_id and order_obs.concept_id = :diagnosis_order_concept_id
inner join concept_name diagnosis_order on order_obs.value_coded = diagnosis_order.concept_id
left join concept_name coded_diagnosis on diagnosis.value_coded = coded_diagnosis.concept_id
;
