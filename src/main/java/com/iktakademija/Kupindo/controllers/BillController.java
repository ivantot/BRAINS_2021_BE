package com.iktakademija.Kupindo.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.Kupindo.entities.BillEntity;
import com.iktakademija.Kupindo.entities.CategoryEntity;
import com.iktakademija.Kupindo.entities.OfferEntity;
import com.iktakademija.Kupindo.entities.UserEntity;
import com.iktakademija.Kupindo.entities.dto.BillDTO;
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

	// read @ admin
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

	// read @ private
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

	// read @ public
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

	// create
	@JsonView(Views.Private.class)
	@RequestMapping(value = "/{offerID}/buyer/{buyerID}", method = RequestMethod.POST)
	public ResponseEntity<?> createBillReviseted(@Valid @RequestBody BillDTO bill, @PathVariable Integer offerID,
			@PathVariable Integer buyerID) {

		if (userRepository.findById(buyerID).isEmpty() || offerRepository.findById(offerID).isEmpty()) {
			return new ResponseEntity<RESTError>(new RESTError(12345, "User or offer not present in database"),
					HttpStatus.BAD_REQUEST);
		}

		BillEntity billEntity = new BillEntity();
		Optional<UserEntity> buyerEntity = userRepository.findById(buyerID);
		Optional<OfferEntity> offerEntity = offerRepository.findById(offerID);

		if (!buyerEntity.get().getUserRole().equals(ERole.ROLE_CUSTOMER)) {
			return new ResponseEntity<RESTError>(new RESTError(1234577, "User must be a customer."),
					HttpStatus.BAD_REQUEST);
		}

		billEntity.setBillCreated(bill.getBillCreated());
		billEntity.setUser(buyerEntity.get());
		billEntity.setOffer(offerEntity.get());
		billEntity.setPaymentCanceled(bill.getPaymentCanceled());
		billEntity.setPaymentMade(bill.getPaymentMade());
		offerEntity.get().setAvailableOffers(offerEntity.get().getAvailableOffers() - 1);
		offerEntity.get().setBoughtOffers(offerEntity.get().getBoughtOffers() + 1);
		offerRepository.save(offerEntity.get());
		return new ResponseEntity<BillEntity>(billRepository.save(billEntity), HttpStatus.OK);
	}

	// update
	@JsonView(Views.Private.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateBillRevisited(@PathVariable Integer id, @RequestParam Boolean paymentMade,
			@RequestParam Boolean paymentCanceled) {
		// get from db using optional
		Optional<BillEntity> billEntity = billRepository.findById(id);
		// get offer using bill entity
		if (!billEntity.isPresent()) {
			return new ResponseEntity<RESTError>(new RESTError(386, "Bill is not in the database."),
					HttpStatus.BAD_REQUEST);
		}

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
			offerService.updateAvailableOffers(offerEntity.get().getId());
			offerRepository.save(offerEntity.get());
		}
		return new ResponseEntity<BillEntity>(billRepository.save(billEntity.get()), HttpStatus.OK);
	}

	// delete
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

	// find by buyer ID
	@JsonView(Views.Admin.class)
	@RequestMapping(value = "/findByBuyer/{buyerId}", method = RequestMethod.GET)
	public ResponseEntity<?> getBillsByUser(@PathVariable Integer buyerId) {
		Optional<UserEntity> buyer = userRepository.findById(buyerId);
		if (buyer.isEmpty()) {
			return new ResponseEntity<RESTError>(new RESTError(2222, "User not found, check your entry."),
					HttpStatus.NOT_FOUND);
		}

		if (!buyer.get().getUserRole().equals(ERole.ROLE_CUSTOMER)) {
			return new ResponseEntity<RESTError>(new RESTError(2223, "User not a buyer, check your entry."),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<List<BillEntity>>(billRepository.findByUser(buyer), HttpStatus.OK);
	}

	// find bills by category
	@JsonView(Views.Admin.class)
	@RequestMapping(value = "/findByCategory/{categoryId}", method = RequestMethod.GET)
	public ResponseEntity<?> getBillsByCategory(@PathVariable Integer categoryId) {
		Optional<CategoryEntity> category = categoryRepository.findById(categoryId);
		if (category.isEmpty()) {
			return new ResponseEntity<RESTError>(new RESTError(1121, "Category not found in database"),
					HttpStatus.NOT_FOUND);
		}

		List<OfferEntity> offers = offerRepository.findAllByCategory(category);

		if (offers.isEmpty()) {
			return new ResponseEntity<RESTError>(
					new RESTError(1123, "No offers in selected category found in database"), HttpStatus.NOT_FOUND);
		}

		List<List<BillEntity>> bills = new ArrayList<>();
		for (OfferEntity offerEntity : offers) {
			if (!billRepository.findAllByOffer(offerEntity).isEmpty()) {
				bills.add(billRepository.findAllByOffer(offerEntity));
			}
		}

		if (bills.isEmpty()) {
			return new ResponseEntity<RESTError>(
					new RESTError(1125, "No bills for selected category found in database"), HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<List<List<BillEntity>>>(bills, HttpStatus.OK);
	}

	// find by date range
	@JsonView(Views.Admin.class)
	@RequestMapping(value = "/findByDate/{date1}/and/{date2}", method = RequestMethod.GET)
	public ResponseEntity<?> findByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date1,
			@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date2) {
		return new ResponseEntity<List<BillEntity>>(billService.getBillsByDate(date1, date2), HttpStatus.OK);
	}

	// generate daily reports
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

	// generate reports by category
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
