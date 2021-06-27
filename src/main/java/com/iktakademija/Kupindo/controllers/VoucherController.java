package com.iktakademija.Kupindo.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.Kupindo.entities.OfferEntity;
import com.iktakademija.Kupindo.entities.UserEntity;
import com.iktakademija.Kupindo.entities.VoucherEntity;
import com.iktakademija.Kupindo.entities.dto.VoucherDTO;
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

	// 4.3 --> T5
	@JsonView(Views.Public.class)
	@RequestMapping(method = RequestMethod.GET, path = "/public")
	public ResponseEntity<?> getAllVouchersPublic() {
		List<VoucherEntity> vouchers = (List<VoucherEntity>) voucherRepository.findAll();
		if (vouchers.isEmpty()) {
			return new ResponseEntity<RESTError>(new RESTError(5, "No vouchers in database"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<VoucherEntity>>(vouchers, HttpStatus.OK);
	}

	@JsonView(Views.Private.class)
	@RequestMapping(method = RequestMethod.GET, path = "/private")
	public ResponseEntity<?> getAllVouchersPrivate() {
		List<VoucherEntity> vouchers = (List<VoucherEntity>) voucherRepository.findAll();
		if (vouchers.isEmpty()) {
			return new ResponseEntity<RESTError>(new RESTError(5, "No vouchers in database"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<VoucherEntity>>(vouchers, HttpStatus.OK);
	}

	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.GET, path = "/admin")
	public ResponseEntity<?> getAllVouchersAdmin() {
		List<VoucherEntity> vouchers = (List<VoucherEntity>) voucherRepository.findAll();
		if (vouchers.isEmpty()) {
			return new ResponseEntity<RESTError>(new RESTError(5, "No vouchers in database"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<VoucherEntity>>(vouchers, HttpStatus.OK);
	}

	// 4.6 --> creating --> T5
	@JsonView(Views.Private.class)
	@RequestMapping(value = "/{offerId}/buyer/{buyerId}", method = RequestMethod.POST)
	public ResponseEntity<?> createVoucher(@PathVariable Integer offerId, @PathVariable Integer buyerId,
			@Valid @RequestBody VoucherDTO voucher) {
		VoucherEntity voucherEntity = new VoucherEntity();
		Optional<UserEntity> buyerEntity = userRepository.findById(buyerId);
		Optional<OfferEntity> offerEntity = offerRepository.findById(offerId);
		if (!offerEntity.isPresent()) {
			return new ResponseEntity<RESTError>(new RESTError(99781, "Offer not present in database."),
					HttpStatus.BAD_REQUEST);
		}

		if (!buyerEntity.isPresent()) {
			return new ResponseEntity<RESTError>(new RESTError(99782, "User not present in database."),
					HttpStatus.BAD_REQUEST);
		}

		if (!buyerEntity.get().getUserRole().equals(ERole.ROLE_CUSTOMER)) {
			return new ResponseEntity<RESTError>(new RESTError(99783, "User not registered as a customer."),
					HttpStatus.BAD_REQUEST);
		}

		voucherEntity.setExpirationDate(voucher.getExpirationDate());
		voucherEntity.setIsUsed(voucher.getIsUsed());
		voucherEntity.setUser(buyerEntity.get());
		voucherEntity.setOffer(offerEntity.get());
		return new ResponseEntity<VoucherEntity>(voucherRepository.save(voucherEntity), HttpStatus.OK);
	}

	// 4.6 --> updating --> T5
	@JsonView(Views.Private.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateVoucher(@PathVariable Integer id, @RequestParam Boolean isUsed) {
		// get from db using optional
		Optional<VoucherEntity> voucherEntity = voucherRepository.findById(id);
		if (!voucherEntity.isPresent()) {
			return new ResponseEntity<RESTError>(new RESTError(5, "No voucher in database"), HttpStatus.BAD_REQUEST);
		}
		voucherEntity.get().setIsUsed(isUsed);
		return new ResponseEntity<VoucherEntity>(voucherRepository.save(voucherEntity.get()), HttpStatus.OK);
	}

	// 4.6 --> deleting --> T5
	@JsonView(Views.Private.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteVoucher(@PathVariable Integer id) {
		// get from db using optional
		Optional<VoucherEntity> voucherEntity = voucherRepository.findById(id);
		if (!voucherEntity.isPresent()) {
			return new ResponseEntity<RESTError>(new RESTError(5, "No voucher in database"), HttpStatus.BAD_REQUEST);
		}
		// do magic only if isUsed == true
		if (!voucherEntity.get().getIsUsed() == true) {
			return new ResponseEntity<RESTError>(
					new RESTError(5256, "Voucher can't be removed since it hasn't been used"), HttpStatus.BAD_REQUEST);
		}
		if (!voucherEntity.get().getExpirationDate().isAfter(LocalDate.now())) {
			return new ResponseEntity<RESTError>(
					new RESTError(5256, "Voucher can't be removed since it hasn't expired yet"),
					HttpStatus.BAD_REQUEST);
		}

		voucherRepository.delete(voucherEntity.get());
		return new ResponseEntity<VoucherEntity>(voucherEntity.get(), HttpStatus.OK);
	}

	// 4.7 --> T5
	@JsonView(Views.Private.class)
	@RequestMapping(value = "/findByBuyer/{buyerId}", method = RequestMethod.GET)
	public ResponseEntity<?> getVouchersByUser(@PathVariable Integer buyerId) {
		Optional<UserEntity> buyer = userRepository.findById(buyerId);
		if (!buyer.isPresent()) {
			return new ResponseEntity<RESTError>(new RESTError(5, "No buyer in database"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<VoucherEntity>>(voucherRepository.findByUser(buyer.get()), HttpStatus.OK);
	}

	// 4.8 --> T5
	@JsonView(Views.Private.class)
	@RequestMapping(value = "/findByOffer/{offerId}", method = RequestMethod.GET)
	public ResponseEntity<?> getVouchersByOffer(@PathVariable Integer offerId) {
		Optional<OfferEntity> offers = offerRepository.findById(offerId);
		if (!offers.isPresent()) {
			return new ResponseEntity<RESTError>(new RESTError(678, "No offer in database"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<VoucherEntity>>(voucherRepository.findByOffer(offers.get()), HttpStatus.OK);
	}

	// 4.9 --> T5
	@JsonView(Views.Admin.class)
	@RequestMapping(value = "/findNonExpiredVoucher")
	public ResponseEntity<?> findValid(LocalDate now) {
		now = LocalDate.now();
		return new ResponseEntity<List<VoucherEntity>>(voucherRepository.findByExpirationDateGreaterThanEqual(now),
				HttpStatus.OK);
	}
}
