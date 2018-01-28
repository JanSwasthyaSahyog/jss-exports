select visit.date_started visit_date from visit
inner join (
  select min(visit.visit_id) visit_id from visit
  where visit.visit_id  > :reference_visit_id
  and visit.patient_id = :patient_id
  and visit.visit_type_id = :visit_type_id) subsequent_visit
on subsequent_visit.visit_id = visit.visit_id;