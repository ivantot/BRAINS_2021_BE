package com.iktakademija.Kupindo.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.iktakademija.Kupindo.entities.BillEntity;
import com.iktakademija.Kupindo.entities.OfferEntity;

public interface BillService {
	
	public List<BillEntity> getBillsByDate(Date date1, Date date2);
	public Boolean categoryInBillsExists(Integer categoryId);
	public void cancelAllBillsForExpiredOffer(Optional<OfferEntity> offerEntity);

}
