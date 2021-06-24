package com.iktakademija.Kupindo.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.Kupindo.entities.OfferEntity;
import com.iktakademija.Kupindo.entities.UserEntity;
import com.iktakademija.Kupindo.entities.VoucherEntity;
import com.iktakademija.Kupindo.repositories.OfferRepository;
import com.iktakademija.Kupindo.repositories.UserRepository;
import com.iktakademija.Kupindo.repositories.VoucherRepository;
import com.iktakademija.Kupindo.res.ERole;
import com.iktakademija.Kupindo.security.Views;
import com.iktakademija.Kupindo.utils.RESTError;

@RestController
@RequestMapping("/kupindo/vouchers")
public class VoucherController {

	@Autowired
	private VoucherRepository voucherRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private OfferRepository offerRepository;

	// 4.3
	@JsonView(Views.Public.class)
	@RequestMapping(method = RequestMethod.GET, path = "/public")
	public ResponseEntity<?> getAllVouchersPublic() {
		List<VoucherEntity> vouchers = (List<VoucherEntity>) voucherRepository.findAll();
		if (vouchers.isEmpty()) {
			return new ResponseEntity<RESTError>(new RESTError(5, "No vouchers in database"), HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<VoucherEntity>>(vouchers, HttpStatus.OK);
	}

	@JsonView(Views.Private.class)
	@RequestMapping(method = RequestMethod.GET, path = "/private")
	public ResponseEntity<?> getAllVouchersPrivate() {
		List<VoucherEntity> vouchers = (List<VoucherEntity>) voucherRepository.findAll();
		if (vouchers.isEmpty()) {
			return new ResponseEntity<RESTError>(new RESTError(5, "No vouchers in database"), HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<VoucherEntity>>(vouchers, HttpStatus.OK);
	}

	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.GET, path = "/admin")
	public ResponseEntity<?> getAllVouchersAdmin() {
		List<VoucherEntity> vouchers = (List<VoucherEntity>) voucherRepository.findAll();
		if (vouchers.isEmpty()) {
			return new ResponseEntity<RESTError>(new RESTError(5, "No vouchers in database"), HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<VoucherEntity>>(vouchers, HttpStatus.OK);
	}

	// 4.6 --> creating
	@JsonView(Views.Private.class)
	@RequestMapping(value = "/{offerId}/buyer/{buyerId}", method = RequestMethod.POST)
	public VoucherEntity createVoucher(@PathVariable Integer offerId, @PathVariable Integer buyerId) {
		VoucherEntity voucherEntity = new VoucherEntity();
		Optional<UserEntity> buyerEntity = userRepository.findById(buyerId);
		Optional<OfferEntity> offerEntity = offerRepository.findById(offerId);
		if (offerEntity.isPresent() && buyerEntity.isPresent()
				&& buyerEntity.get().getUserRole().equals(ERole.ROLE_CUSTOMER)) {
			voucherEntity.setExpirationDate(LocalDate.now().plusDays(10));
			voucherEntity.setIsUsed(false);// default state
			voucherEntity.setUser(buyerEntity.get());
			voucherEntity.setOffer(offerEntity.get());
			return voucherRepository.save(voucherEntity);
		}
		return null;
	}

	// 4.6 --> updating
	@JsonView(Views.Private.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public VoucherEntity updateVoucher(@PathVariable Integer id, @RequestParam Boolean isUsed) {
		// get from db using optional
		Optional<VoucherEntity> voucherEntity = voucherRepository.findById(id);
		if (voucherEntity.isPresent()) {
			// do magic
			if (isUsed != null) {
				voucherEntity.get().setIsUsed(isUsed);
			}
			return voucherRepository.save(voucherEntity.get());
		}
		return null;
	}

	// 4.6 --> deleting
	@JsonView(Views.Private.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public VoucherEntity deleteVoucher(@PathVariable Integer id) {
		// get from db using optional
		Optional<VoucherEntity> voucherEntity = voucherRepository.findById(id);
		if (voucherEntity.isPresent()) {
			// do magic only if isUsed == true
			if (voucherEntity.get().getIsUsed() == true) {
				voucherRepository.delete(voucherEntity.get());
				return voucherEntity.get();
			}
		}
		return null;
	}

	// 4.7
	@JsonView(Views.Private.class)
	@RequestMapping(value = "/findByBuyer/{buyerId}", method = RequestMethod.GET)
	public List<VoucherEntity> getVouchersByUser(@PathVariable Integer buyerId) {
		Optional<UserEntity> buyer = userRepository.findById(buyerId);
		if (buyer.isPresent()) {
			return voucherRepository.findByUser(buyer.get());
		}
		return null;
	}

	// 4.8
	@JsonView(Views.Private.class)
	@RequestMapping(value = "/findByOffer/{offerId}", method = RequestMethod.GET)
	public List<VoucherEntity> getVouchersByOffer(@PathVariable Integer offerId) {
		Optional<OfferEntity> offers = offerRepository.findById(offerId);
		if (offers.isPresent()) {
			return voucherRepository.findByOffer(offers.get());
		}
		return null;
	}

	// 4.9
	@JsonView(Views.Admin.class)
	@RequestMapping(value = "/findNonExpiredVoucher")
	public List<VoucherEntity> findValid(LocalDate now) {
		now = LocalDate.now();
		return voucherRepository.findByExpirationDateGreaterThanEqual(now);
	}
}
