select drug.name name, frequency_name.name frequency, concat(drug_order.dose, ' ', dose_units_name.name) dose  from orders
  inner join drug_order on date(orders.date_created) = :order_date and orders.order_type_id = 2 and orders.order_id = drug_order.order_id
  inner join drug on drug_order.drug_inventory_id = drug.drug_id
  inner join order_frequency on drug_order.frequency = order_frequency.order_frequency_id
  inner join concept_name frequency_name on frequency_name.concept_id = order_frequency.concept_id and frequency_name.concept_name_type = 'FULLY_SPECIFIED'
  inner join concept_name dose_units_name on dose_units_name.concept_id = drug_order.dose_units and dose_units_name.concept_name_type = 'FULLY_SPECIFIED'
where orders.patient_id = :patient_id
;
