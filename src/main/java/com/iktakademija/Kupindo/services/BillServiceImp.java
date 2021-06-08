package com.iktakademija.Kupindo.services;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;

import com.iktakademija.Kupindo.entities.BillEntity;

@Service
public class BillServiceImp implements BillService {

	@PersistenceContext
	private EntityManager em;

	@Override
	public List<BillEntity> getBillsByDate(Date date1, Date date2) {
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
}
