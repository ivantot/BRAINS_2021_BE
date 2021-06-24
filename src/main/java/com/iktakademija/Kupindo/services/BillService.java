package com.iktakademija.Kupindo.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.iktakademija.Kupindo.entities.BillEntity;
import com.iktakademija.Kupindo.entities.OfferEntity;
import com.iktakademija.Kupindo.entities.dto.ReportDTO;
import com.iktakademija.Kupindo.entities.dto.ReportItemDTO;

public interface BillService {
	
	public List<BillEntity> getBillsByDate(LocalDate date1, LocalDate date2);
	public Boolean categoryInBillsExists(Integer categoryId);
	public void cancelAllBillsForExpiredOffer(Optional<OfferEntity> offerEntity);
	public List<ReportItemDTO> generateReportsInRange(LocalDate startDate, LocalDate endDate);
	public ReportDTO generateReportByCategoryInRange(LocalDate startDate, LocalDate endDate, String category);
}
