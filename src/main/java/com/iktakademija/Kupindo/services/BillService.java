package com.iktakademija.Kupindo.services;

import java.util.Date;
import java.util.List;

import com.iktakademija.Kupindo.entities.BillEntity;

public interface BillService {
	
	public List<BillEntity> getBillsByDate(Date date1, Date date2);
	public Boolean categoryInBillsExists(Integer categoryId);

}
