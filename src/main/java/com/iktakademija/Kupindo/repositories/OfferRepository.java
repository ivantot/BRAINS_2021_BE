package com.iktakademija.Kupindo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.iktakademija.Kupindo.entities.CategoryEntity;
import com.iktakademija.Kupindo.entities.OfferEntity;

public interface OfferRepository extends CrudRepository<OfferEntity, Integer> {
	
	List<OfferEntity> findByRegularPriceBetweenOrderByRegularPriceAsc(Double price1, Double price2);
	
	Optional<OfferEntity> findByCategory(Optional<CategoryEntity> category);

}
