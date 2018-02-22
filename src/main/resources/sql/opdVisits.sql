select date(visit.date_started) visit_date from visit
where visit.patient_id = :patient_id
      and date(visit.date_started) BETWEEN :start_date and :end_date
      and visit.visit_type_id = :visit_type_id
;