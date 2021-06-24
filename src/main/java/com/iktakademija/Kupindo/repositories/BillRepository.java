package com.iktakademija.Kupindo.repositories;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import com.iktakademija.Kupindo.entities.BillEntity;
import com.iktakademija.Kupindo.entities.OfferEntity;
import com.iktakademija.Kupindo.entities.UserEntity;

public interface BillRepository extends CrudRepository<BillEntity, Integer> {
	
	List <BillEntity> findByUser(Optional<UserEntity> buyer);
	/*
	@Query("SELECT * FROM BillEntity WHERE offer IN (SELECT id FROM OfferEntity WHERE category = :categoryId")
	*/
	Optional <BillEntity> findByOffer(Optional<OfferEntity> offers);
	
	Optional <BillEntity> findByBillCreatedBetweenOrderByBillCreatedAsc(Date startDate, Date endDate);
	
	List <BillEntity> findAllByOffer(OfferEntity offer);
	
	List<BillEntity> findByBillCreatedBetween(LocalDate date1, LocalDate date2);
	

}
