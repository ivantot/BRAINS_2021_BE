package com.iktakademija.Kupindo.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.Kupindo.entities.BillEntity;
import com.iktakademija.Kupindo.entities.CategoryEntity;
import com.iktakademija.Kupindo.entities.OfferEntity;
import com.iktakademija.Kupindo.entities.UserEntity;
import com.iktakademija.Kupindo.entities.dto.ReportDTO;
import com.iktakademija.Kupindo.entities.dto.ReportItemDTO;
import com.iktakademija.Kupindo.repositories.BillRepository;
import com.iktakademija.Kupindo.repositories.CategoryRepository;
import com.iktakademija.Kupindo.repositories.OfferRepository;
import com.iktakademija.Kupindo.repositories.UserRepository;
import com.iktakademija.Kupindo.res.ERole;
import com.iktakademija.Kupindo.security.Views;
import com.iktakademija.Kupindo.services.BillService;
import com.iktakademija.Kupindo.services.OfferService;
import com.iktakademija.Kupindo.services.VoucherService;
import com.iktakademija.Kupindo.utils.RESTError;

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

	@Autowired
	private VoucherService voucherService;

	// 3.3 -- T5
	@JsonView(Views.Admin.class)
	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public ResponseEntity<?> getAllBillsAdmin() {
		List<BillEntity> bills = (List<BillEntity>) billRepository.findAll();
		if (bills.isEmpty()) {
			// return 404
			return new ResponseEntity<>(new RESTError(1, "No bills in database"), HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<BillEntity>>(bills, HttpStatus.OK);
	}

	@JsonView(Views.Private.class)
	@RequestMapping(value = "/private", method = RequestMethod.GET)
	public ResponseEntity<?> getAllBillsPrivate() {
		List<BillEntity> bills = (List<BillEntity>) billRepository.findAll();
		if (bills.isEmpty()) {
			// return 404
			return new ResponseEntity<>(new RESTError(1, "No bills in database"), HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<BillEntity>>(bills, HttpStatus.OK);
	}

	@JsonView(Views.Public.class)
	@RequestMapping(value = "/public", method = RequestMethod.GET)
	public ResponseEntity<?> getAllBillsPublic() {
		List<BillEntity> bills = (List<BillEntity>) billRepository.findAll();
		if (bills.isEmpty()) {
			// return 404
			return new ResponseEntity<>(new RESTError(1, "No bills in database"), HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<BillEntity>>(bills, HttpStatus.OK);
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
	// 3.6 --> deleting -- T5
	@JsonView(Views.Private.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteBill(@PathVariable Integer id) {
		// get from db using optional
		Optional<BillEntity> billEntity = billRepository.findById(id);
		if (billEntity.isPresent()) {
			// do magic only if paymentCanceled == true
			if (billEntity.get().getPaymentCanceled() == true) {
				billRepository.delete(billEntity.get());
				return new ResponseEntity<BillEntity>(billEntity.get(), HttpStatus.OK);
			}
			return new ResponseEntity<RESTError>(new RESTError(11, "Payment not cancled. bill cannot be removed"),
					HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<RESTError>(new RESTError(21, "Bill not found in database"), HttpStatus.NOT_FOUND);
	}

	// 3.7
	@JsonView(Views.Admin.class)
	@RequestMapping(value = "/findByBuyer/{buyerId}", method = RequestMethod.GET)
	public ResponseEntity<?> getBillsByUser(@PathVariable Integer buyerId) {
		Optional<UserEntity> buyer = userRepository.findById(buyerId);
		if (buyer.isEmpty()) {
			return new ResponseEntity<RESTError>(new RESTError(2222, "User not found, check your entry."),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<List<BillEntity>>(billRepository.findByUser(buyer), HttpStatus.OK);
	}

	// 3.8
	@JsonView(Views.Admin.class)
	@RequestMapping(value = "/findByCategory/{categoryId}", method = RequestMethod.GET)
	public Optional<BillEntity> getBillsByCategory(@PathVariable Integer categoryId) {
		Optional<CategoryEntity> category = categoryRepository.findById(categoryId);
		Optional<OfferEntity> offers = offerRepository.findByCategory(category);
		return billRepository.findByOffer(offers);
	}

	// 3.9
	@JsonView(Views.Admin.class)
	@RequestMapping(value = "/findByDate/{date1}/and/{date2}", method = RequestMethod.GET)
	public List<BillEntity> findByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date1,
			@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date2) {
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
	@JsonView(Views.Private.class)
	@RequestMapping(value = "/{offerId}/buyer/{buyerId}", method = RequestMethod.POST)
	public BillEntity createBillReviseted(@PathVariable Integer offerId, @PathVariable Integer buyerId,
			@RequestParam Integer availableOffers, @RequestParam Integer boughtOffers) {
		BillEntity billEntity = new BillEntity();
		Optional<UserEntity> buyerEntity = userRepository.findById(buyerId);
		Optional<OfferEntity> offerEntity = offerRepository.findById(offerId);
		if (offerEntity.isPresent() && buyerEntity.isPresent() && offerEntity.get().getAvailableOffers() > 0
				&& buyerEntity.get().getUserRole().equals(ERole.ROLE_CUSTOMER)) {
			billEntity.setBillCreated(LocalDate.now());
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
	@JsonView(Views.Private.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public BillEntity updateBillRevisited(@PathVariable Integer id, @RequestParam Boolean paymentMade,
			@RequestParam Boolean paymentCanceled, @RequestParam Integer availableOffers,
			@RequestParam Integer boughtOffers) {
		// get from db using optional
		Optional<BillEntity> billEntity = billRepository.findById(id);
		// get offer using bill entity
		if (billEntity.isPresent()) {
			Optional<OfferEntity> offerEntity = offerRepository.findById(billEntity.get().getOffer().getId());
			// do magic
			if (paymentMade != null) {
				if (billEntity.get().getPaymentMade() == false && paymentMade == true) {
					billEntity.get().setPaymentMade(paymentMade);
					voucherService.createVoucherAfterPayment(billEntity.get());
				} else
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

	// 3.4 -- T5
	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.GET, value = "/generateReportByDate/{startDate}/and/{endDate}")
	public ResponseEntity<?> getReportSalesByDay(
			@PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
			@PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate) {

		List<BillEntity> billsInRange = (List<BillEntity>) billRepository.findByBillCreatedBetween(startDate, endDate);

		if (billsInRange.isEmpty()) {
			return new ResponseEntity<RESTError>(new RESTError(12, "No bills in selected range."),
					HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<List<ReportItemDTO>>(billService.generateReportsInRange(startDate, endDate),
				HttpStatus.OK);
	}

	// 3.5 -- T5
	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.GET, value = "/generateReport/{startDate}/and/{endDate}/category/{categoryID}")
	public ResponseEntity<?> getReportSales(@PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
			@PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate, @PathVariable String categoryID) {

		if (billRepository.findByBillCreatedBetween(startDate, endDate).isEmpty()) {
			return new ResponseEntity<RESTError>(new RESTError(12, "No bills in selected range."),
					HttpStatus.BAD_REQUEST);
		}

		if (categoryRepository.findByCategoryName(categoryID).isEmpty()) {
			return new ResponseEntity<RESTError>(new RESTError(12, "No category with this name in database."),
					HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<ReportDTO>(
				billService.generateReportByCategoryInRange(startDate, endDate, categoryID), HttpStatus.OK);
	}
}
