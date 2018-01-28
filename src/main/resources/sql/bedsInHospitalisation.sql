select location.name ward, bed.bed_number,
  timestampdiff(DAY, bpam.date_started, IFNULL(bpam.date_stopped, now())) duration
from bed_patient_assignment_map bpam
  inner join encounter on bpam.encounter_id = encounter.encounter_id and encounter.visit_id = :visit_id
  inner join bed on bed.bed_id = bpam.bed_id
  inner join bed_location_map blm on bed.bed_id = blm.bed_id
  inner join location on blm.location_id = location.location_id;
