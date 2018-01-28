select visit.visit_id, patient_identifier.identifier,
person.person_id,
floor(datediff(CURDATE(), person.birthdate) / 365) AS age,
person.gender gender,
pa.city_village village,
CASE person_attribute.value
WHEN 1 THEN 'YES'
WHEN 2 THEN 'NO'
END as smart_card_holder,
date(visit.date_started) as admission_date,
date(discharge.date_changed) as discharge_date,
TIMESTAMPDIFF(DAY, visit.date_started, discharge.date_changed) as length_of_hospitalisation
from visit
inner join visit_type on visit.visit_type_id = visit_type.visit_type_id AND visit_type.name = 'IPD'
inner join patient_identifier on patient_identifier.patient_id = visit.patient_id and preferred = 1
inner join person on person.person_id = visit.patient_id and person.voided = 0
inner join person_address pa on person.person_id = pa.person_id
left join visit_attribute as discharge on discharge.visit_id = visit.visit_id and value_reference = 'Discharged'
left join person_attribute on person.person_id = person_attribute.person_id and person_attribute_type_id = 37
where date(visit.date_started) BETWEEN '${input["startDate"]}' AND '${input["endDate"]}';
