select visit.visit_id, patient_identifier.identifier,
person.person_id,
floor(datediff(CURDATE(), person.birthdate) / 365) AS age,
person.gender gender,
pa.city_village village,
pa.address3 tehsil,
pa.state_province state,
CASE smart_card_holder.value
WHEN 1 THEN 'YES'
WHEN 2 THEN 'NO'
END as smart_card_holder,
CASE smoking.value
WHEN 1 THEN 'YES'
WHEN 2 THEN 'NO'
END as smoking,
CASE alcohol.value
WHEN 1 THEN 'YES'
WHEN 2 THEN 'NO'
END as alcohol,
CASE food_security.value
WHEN 1 THEN 'YES'
WHEN 2 THEN 'NO'
END as food_security,
family_income.value family_income,
date(visit.date_started) as admission_date,
date(discharge.date_changed) as discharge_date,
TIMESTAMPDIFF(DAY, visit.date_started, discharge.date_changed) as length_of_hospitalisation
from visit
inner join visit_type on visit.visit_type_id = visit_type.visit_type_id AND visit_type.name = 'IPD'
inner join patient_identifier on patient_identifier.patient_id = visit.patient_id and preferred = 1
inner join person on person.person_id = visit.patient_id and person.voided = 0
inner join person_address pa on person.person_id = pa.person_id
left join visit_attribute as discharge on discharge.visit_id = visit.visit_id and value_reference = 'Discharged'
left join person_attribute smart_card_holder on person.person_id = smart_card_holder.person_id and smart_card_holder.person_attribute_type_id = 37
LEFT JOIN person_attribute smoking on person.person_id = smoking.person_id and smoking.person_attribute_type_id = 20
LEFT JOIN person_attribute alcohol on person.person_id = alcohol.person_id and alcohol.person_attribute_type_id = 21
LEFT JOIN person_attribute food_security on person.person_id = food_security.person_id and food_security.person_attribute_type_id = 23
LEFT JOIN person_attribute family_income on person.person_id = family_income.person_id and family_income.person_attribute_type_id = 33
where date(visit.date_started) BETWEEN '${input["startDate"]}' AND '${input["endDate"]}';
