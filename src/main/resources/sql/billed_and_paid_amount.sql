select billed_amount.sum as billed_amount, paid_amount.sum paid_amount from
  (select sum(amount_total) sum
   from sale_order INNER JOIN res_partner on sale_order.partner_id = res_partner.id and res_partner.ref = :patient_id
   where date(datetime_order) BETWEEN :start_date and :end_date) billed_amount,
  (select sum(amount) sum from account_voucher
    INNER JOIN res_partner on account_voucher.partner_id = res_partner.id and res_partner.ref = :patient_id
  where date(account_voucher.date) BETWEEN :start_date and :end_date) paid_amount;
