package com.iktakademija.Kupindo.services;

import com.iktakademija.Kupindo.entities.BillEntity;
import com.iktakademija.Kupindo.entities.VoucherEntity;

public interface VoucherService {

	public VoucherEntity createVoucherAfterPayment(BillEntity bill);

}
