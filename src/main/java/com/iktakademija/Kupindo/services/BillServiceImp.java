package com.iktakademija.Kupindo.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iktakademija.Kupindo.entities.BillEntity;
import com.iktakademija.Kupindo.entities.OfferEntity;
import com.iktakademija.Kupindo.entities.dto.ReportDTO;
import com.iktakademija.Kupindo.entities.dto.ReportItemDTO;
import com.iktakademija.Kupindo.repositories.BillRepository;

@Service
public class BillServiceImp implements BillService {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private BillRepository billRepository;

	@Override
	public List<BillEntity> getBillsByDate(LocalDate date1, LocalDate date2) {
		// make a custom sql database query
		String sql = "SELECT b FROM BillEntity b WHERE b.billCreated BETWEEN :date1 AND :date2";
		Query query = em.createQuery(sql);
		query.setParameter("date1", date1);
		query.setParameter("date2", date2);
		List<BillEntity> retVal = query.getResultList();
		return retVal;
	}

	@Override
	public Boolean categoryInBillsExists(Integer id) {
		// query db to see if offers can be matched with provided category id
		String sql = "SELECT b FROM BillEntity b JOIN FETCH b.offer o WHERE o.category.id = :id";
		Query query = em.createQuery(sql);
		query.setParameter("id", id);
		List<BillEntity> retVal = query.getResultList();
		if (retVal.isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public void cancelAllBillsForExpiredOffer(Optional<OfferEntity> offerEntity) {
		if (offerEntity == null)
			return;

		// Get all bills that contains offer
		List<BillEntity> bills = billRepository.findAllByOffer(offerEntity.get());
		for (BillEntity billEntity : bills) {
			billEntity.setPaymentCanceled(true);
			billRepository.save(billEntity);
		}
	}

	/**
	 * a method for generating daily sales reports
	 * 
	 * @param takes in a date range to find bills 
	 * 
	 * @return a list of daily reports of sale
	 * 
	 */
	@Override
	public List<ReportItemDTO> generateReportsInRange(LocalDate startDate, LocalDate endDate) {
		// prepare database and blank list to store reports
		List<BillEntity> billsInRange = (List<BillEntity>) billRepository.findByBillCreatedBetween(startDate, endDate);
		List<ReportItemDTO> dailyReports = new ArrayList<>();

		// assign fields to ReportItemDTO for all bills on a given day in range, create list of ReportItemDTO
		Long elapsedDays = ChronoUnit.DAYS.between(startDate, endDate);
		LocalDate nextDay = startDate;

		for (Integer i = 0; i <= elapsedDays; i++) {
			ReportItemDTO dailyReport = new ReportItemDTO();
			Double dailyIncome = 0d;
			Integer dailyNoOfOffers = 0;
			dailyReport.setDate(nextDay);
			for (BillEntity billEntity : billsInRange) {
				if (billEntity.getBillCreated().equals(nextDay) && billEntity.getPaymentMade() == true) {
					dailyIncome += billEntity.getOffer().getActionPrice();
					dailyNoOfOffers++;
				}
			}
			dailyReport.setIncome(dailyIncome);
			dailyReport.setNumberOfOffers(dailyNoOfOffers);
			nextDay = nextDay.plusDays(1);
			if (dailyReport.getNumberOfOffers() != 0) {
				dailyReports.add(dailyReport);
			}
		}
		return dailyReports;
	}

	/**
	 * a method for generating a report on sales by item category
	 * 
	 * @param takes in a date range to find bills and a category
	 * 
	 * @return a report of sales by category
	 * 
	 */

	@Override
	public ReportDTO generateReportByCategoryInRange(LocalDate startDate, LocalDate endDate, String category) {
		// prepare database and blank list to store reports
		List<BillEntity> billsInRange = (List<BillEntity>) billRepository.findByBillCreatedBetween(startDate, endDate);
		List<ReportItemDTO> dailyReports = new ArrayList<>();
		ReportDTO report = new ReportDTO();
		report.setCategoryName(category);

		// assign fields to ReportItemDTO for all bills on a given day in range, create list of ReportItemDTO
		Long elapsedDays = ChronoUnit.DAYS.between(startDate, endDate);
		LocalDate nextDay = startDate;

		for (Integer i = 0; i <= elapsedDays; i++) {
			ReportItemDTO dailyReport = new ReportItemDTO();
			Double dailyIncome = 0d;
			Integer dailyNoOfOffers = 0;
			dailyReport.setDate(nextDay);
			for (BillEntity billEntity : billsInRange) {
				if (billEntity.getBillCreated().equals(nextDay) && billEntity.getPaymentMade() == true
						&& billEntity.getOffer().getCategory().getCategoryName().equals(category)) {
					dailyIncome += billEntity.getOffer().getActionPrice();
					dailyNoOfOffers++;
				}
			}
			dailyReport.setIncome(dailyIncome);
			dailyReport.setNumberOfOffers(dailyNoOfOffers);
			nextDay = nextDay.plusDays(1);
			if (dailyReport.getNumberOfOffers() != 0) {
				dailyReports.add(dailyReport);
			}
		}
		report.setReportItems(dailyReports);
		Double categoryIncome = 0d;
		Integer categoryNoOfOffers = 0;
		for (ReportItemDTO reportItemDTO : dailyReports) {
			categoryIncome += reportItemDTO.getIncome();
			categoryNoOfOffers += reportItemDTO.getNumberOfOffers();
		}
		report.setSumOfIncomes(categoryIncome);
		report.setTotalNumberOfSoldOffers(categoryNoOfOffers);
		return report;
	}
}
