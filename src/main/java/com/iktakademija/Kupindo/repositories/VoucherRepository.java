package com.iktakademija.Kupindo.repositories;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

import com.iktakademija.Kupindo.entities.OfferEntity;
import com.iktakademija.Kupindo.entities.UserEntity;
import com.iktakademija.Kupindo.entities.VoucherEntity;

public interface VoucherRepository extends CrudRepository<VoucherEntity, Integer> {

	List<VoucherEntity> findByUser(UserEntity buyer);

	List<VoucherEntity> findByOffer(OfferEntity offers);

	List<VoucherEntity> findByExpirationDateGreaterThanEqual(LocalDate now);

}
