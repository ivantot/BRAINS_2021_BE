package com.iktakademija.Kupindo.controllers;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iktakademija.Kupindo.entities.BillEntity;
import com.iktakademija.Kupindo.entities.CategoryEntity;
import com.iktakademija.Kupindo.entities.OfferEntity;
import com.iktakademija.Kupindo.entities.UserEntity;
import com.iktakademija.Kupindo.repositories.BillRepository;
import com.iktakademija.Kupindo.repositories.CategoryRepository;
import com.iktakademija.Kupindo.repositories.OfferRepository;
import com.iktakademija.Kupindo.repositories.UserRepository;
import com.iktakademija.Kupindo.res.ERole;
import com.iktakademija.Kupindo.services.BillService;
import com.iktakademija.Kupindo.services.OfferService;

@RestController
@RequestMapping("/kupindo/bills")
public class BillController {

	@Autowired
	private BillRepository billRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private OfferRepository offerRepository;
	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private BillService billService;
	
	@Autowired
	private OfferService offerService;
	// 3.3
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<BillEntity> getAllBills() {
		return (List<BillEntity>) billRepository.findAll();
	}
/*
	// 3.6 --> creating
	@RequestMapping(value = "/{offerId}/buyer/{buyerId}", method = RequestMethod.POST)
	public BillEntity createBill(@PathVariable Integer offerId, @PathVariable Integer buyerId) {
		BillEntity billEntity = new BillEntity();
		Optional<UserEntity> buyerEntity = userRepository.findById(buyerId);
		Optional<OfferEntity> offerEntity = offerRepository.findById(offerId);
		if (offerEntity.isPresent() && buyerEntity.isPresent()) {
			billEntity.setBillCreated(new Date());
			billEntity.setPaymentCanceled(false);// default state
			billEntity.setPaymentMade(false);// default state
			billEntity.setUser(buyerEntity.get());
			billEntity.setOffer(offerEntity.get());
			return billRepository.save(billEntity);
		}
		return null;
	}

	// 3.6 --> updating
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public BillEntity updateBill(@PathVariable Integer id, @RequestParam Boolean paymentMade,
			@RequestParam Boolean paymentCanceled) {
		// get from db using optional
		Optional<BillEntity> billEntity = billRepository.findById(id);
		if (billEntity.isPresent()) {
			// do magic
			if (paymentMade != null) {
				billEntity.get().setPaymentMade(paymentMade);
			}
			if (paymentCanceled != null) {
				billEntity.get().setPaymentCanceled(paymentCanceled);
			}
			return billRepository.save(billEntity.get());
		}
		return null;
	}
*/
	// 3.6 --> deleting
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public BillEntity deleteBill(@PathVariable Integer id) {
		// get from db using optional
		Optional<BillEntity> billEntity = billRepository.findById(id);
		if (billEntity.isPresent()) {
			// do magic only if paymentCanceled == true
			if (billEntity.get().getPaymentCanceled() == true) {
				billRepository.delete(billEntity.get());
				return billEntity.get();
			}
		}
		return null;
	}

	// 3.7
	@RequestMapping(value = "/findByBuyer/{buyerId}", method = RequestMethod.GET)
	public Optional<BillEntity> getBillsByUser(@PathVariable Integer buyerId) {
		Optional<UserEntity> buyer = userRepository.findById(buyerId);
		if (buyer.isPresent()) {
			return billRepository.findByUser(buyer);
		}
		return null;
	}

	// 3.8
	@RequestMapping(value = "/findByCategory/{categoryId}", method = RequestMethod.GET)
	public Optional<BillEntity> getBillsByCategory(@PathVariable Integer categoryId) {
		Optional<CategoryEntity> category = categoryRepository.findById(categoryId);
		Optional<OfferEntity> offers = offerRepository.findByCategory(category);
		return billRepository.findByOffer(offers);
	}
	
	// 3.9
	@RequestMapping(value = "/findByDate/{date1}/and/{date2}", method = RequestMethod.GET)
	public List<BillEntity> findByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date1,
			@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date2) {
		return billService.getBillsByDate(date1, date2);
	}
	
	/*
	// 3.9
	@RequestMapping(value = "/findByDate/{startDate}/and/{endDate}", method = RequestMethod.GET)
	public Optional<BillEntity> findByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
			@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
		return billRepository.findByBillCreatedBetweenOrderByBillCreatedAsc(startDate, endDate);
	}
	
	// 3.6 --> creating --> 5.1 if used disable previous version of 3.6
	@RequestMapping(value = "/{offerId}/buyer/{buyerId}", method = RequestMethod.POST)
	public BillEntity createBillReviseted(@PathVariable Integer offerId, @PathVariable Integer buyerId) {
		BillEntity billEntity = new BillEntity();
		Optional<UserEntity> buyerEntity = userRepository.findById(buyerId);
		Optional<OfferEntity> offerEntity = offerRepository.findById(offerId);
		if (offerEntity.isPresent() && buyerEntity.isPresent() && offerEntity.get().getAvailableOffers() > 0 && buyerEntity.get().getUserRole().equals(ERole.ROLE_CUSTOMER)) {
			billEntity.setBillCreated(new Date());
			billEntity.setPaymentCanceled(false);// default state
			billEntity.setPaymentMade(false);// default state
			billEntity.setUser(buyerEntity.get());
			billEntity.setOffer(offerEntity.get());
			offerEntity.get().setAvailableOffers(offerEntity.get().getAvailableOffers()-1);
			offerEntity.get().setBoughtOffers(offerEntity.get().getBoughtOffers()+1);
			offerRepository.save(offerEntity.get());
			return billRepository.save(billEntity);
		}
		return null;
	}
	
	// 3.6 --> updating --> 5.2 if used disable previous version of 3.6
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public BillEntity updateBillRevisited(@PathVariable Integer id, @RequestParam Boolean paymentMade,
			@RequestParam Boolean paymentCanceled) {
		// get from db using optional
		Optional<BillEntity> billEntity = billRepository.findById(id);
		// get offer using bill entity
		if (billEntity.isPresent()) {
			Optional<OfferEntity> offerEntity = offerRepository.findById(billEntity.get().getOffer().getId());
			// do magic
			if (paymentMade != null) {
				billEntity.get().setPaymentMade(paymentMade);
			}
			if (paymentCanceled == true) {
				billEntity.get().setPaymentCanceled(paymentCanceled);
				offerEntity.get().setAvailableOffers(offerEntity.get().getAvailableOffers()+1);
				offerEntity.get().setBoughtOffers(offerEntity.get().getBoughtOffers()-1);
				offerRepository.save(offerEntity.get());
			}
			return billRepository.save(billEntity.get());
		}
		return null;
	}
	*/
	// 3.6 --> creating --> 5.1 if used disable previous version of 3.6 --> 2.2 controllers
	@RequestMapping(value = "/{offerId}/buyer/{buyerId}", method = RequestMethod.POST)
	public BillEntity createBillReviseted(@PathVariable Integer offerId, @PathVariable Integer buyerId, @RequestParam Integer availableOffers, @RequestParam Integer boughtOffers) {
		BillEntity billEntity = new BillEntity();
		Optional<UserEntity> buyerEntity = userRepository.findById(buyerId);
		Optional<OfferEntity> offerEntity = offerRepository.findById(offerId);
		if (offerEntity.isPresent() && buyerEntity.isPresent() && offerEntity.get().getAvailableOffers() > 0 && buyerEntity.get().getUserRole().equals(ERole.ROLE_CUSTOMER)) {
			billEntity.setBillCreated(new Date());
			billEntity.setPaymentCanceled(false);// default state
			billEntity.setPaymentMade(false);// default state
			billEntity.setUser(buyerEntity.get());
			billEntity.setOffer(offerEntity.get());
			//offerEntity.get().setAvailableOffers(offerEntity.get().getAvailableOffers()-1);
			//offerEntity.get().setBoughtOffers(offerEntity.get().getBoughtOffers()+1);
			offerService.updateAvailableOffers(offerId, boughtOffers, availableOffers);
			offerRepository.save(offerEntity.get());
			return billRepository.save(billEntity);
		}
		return null;
	}
	
	// 3.6 --> updating --> 5.2 if used disable previous version of 3.6 --> 2.3 controllers
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public BillEntity updateBillRevisited(@PathVariable Integer id, @RequestParam Boolean paymentMade,
			@RequestParam Boolean paymentCanceled, @RequestParam Integer availableOffers, @RequestParam Integer boughtOffers) {
		// get from db using optional
		Optional<BillEntity> billEntity = billRepository.findById(id);
		// get offer using bill entity
		if (billEntity.isPresent()) {
			Optional<OfferEntity> offerEntity = offerRepository.findById(billEntity.get().getOffer().getId());
			// do magic
			if (paymentMade != null) {
				billEntity.get().setPaymentMade(paymentMade);
			}
			if (paymentCanceled == true) {
				billEntity.get().setPaymentCanceled(paymentCanceled);
				//offerEntity.get().setAvailableOffers(offerEntity.get().getAvailableOffers()+1);
				//offerEntity.get().setBoughtOffers(offerEntity.get().getBoughtOffers()-1);
				offerService.updateAvailableOffers(offerEntity.get().getId(), boughtOffers, availableOffers);
				offerRepository.save(offerEntity.get());
			}
			return billRepository.save(billEntity.get());
		}
		return null;
	}

}
