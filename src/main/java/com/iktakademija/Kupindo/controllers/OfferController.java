package com.iktakademija.Kupindo.controllers;

import java.io.IOException;
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
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.Kupindo.entities.CategoryEntity;
import com.iktakademija.Kupindo.entities.OfferEntity;
import com.iktakademija.Kupindo.repositories.CategoryRepository;
import com.iktakademija.Kupindo.repositories.OfferRepository;
import com.iktakademija.Kupindo.repositories.UserRepository;
import com.iktakademija.Kupindo.res.EOfferStatus;
import com.iktakademija.Kupindo.res.ERole;
import com.iktakademija.Kupindo.security.Views;
import com.iktakademija.Kupindo.services.BillService;
import com.iktakademija.Kupindo.services.OfferService;
import com.iktakademija.Kupindo.utils.RESTError;

@RestController
@RequestMapping(path = "/kupindo/offers")
public class OfferController {

	@Autowired
	private OfferRepository offerRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OfferService offerService;

	@Autowired
	private BillService billService;

	// 3.3 -- T5
	@JsonView(Views.Public.class)
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<?> getAllOffers() {
		List<OfferEntity> offers = (List<OfferEntity>) offerRepository.findAll();
		if (offers.isEmpty()) {
			return new ResponseEntity<RESTError>(new RESTError(3, "No offers in database"), HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<OfferEntity>>(offers, HttpStatus.OK);
	}

	// 3.4 -- T5
	@JsonView(Views.Private.class)
	@RequestMapping(method = RequestMethod.POST, value = "")
	public ResponseEntity<?> addOffer(@RequestBody OfferEntity newOffer) {
		OfferEntity offerEntity = new OfferEntity();
		offerEntity.setOfferName(newOffer.getOfferName());
		offerEntity.setOfferDescription(newOffer.getOfferDescription());
		offerEntity.setRegularPrice(newOffer.getRegularPrice());
		offerEntity.setOfferCreated(LocalDate.now()); // now!
		offerEntity.setImagePath(newOffer.getImagePath());
		offerEntity.setCategory(newOffer.getCategory());
		offerEntity.setUser(newOffer.getUser());
		offerEntity.setAvailableOffers(1);// set min required available offers
		offerEntity.setBoughtOffers(0);// set min required available offers
		offerEntity.setOfferStatus(EOfferStatus.WAIT_FOR_APPROVING);// default status
		return new ResponseEntity<OfferEntity>(offerRepository.save(offerEntity), HttpStatus.OK);
	}

	// 3.5 --> T5
	@JsonView(Views.Private.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	public ResponseEntity<?> editOffer(@PathVariable Integer id, @Valid @RequestBody OfferEntity updOffer) {
		Optional<OfferEntity> offerEntity = offerRepository.findById(id);

		if (!offerEntity.isPresent()) {
			return new ResponseEntity<RESTError>(new RESTError(34354, "Offer not in database."),
					HttpStatus.BAD_REQUEST);
		}

		offerEntity.get().setOfferName(updOffer.getOfferName());
		offerEntity.get().setOfferDescription(updOffer.getOfferDescription());
		offerEntity.get().setRegularPrice(updOffer.getRegularPrice());
		offerEntity.get().setActionPrice(updOffer.getActionPrice());
		offerEntity.get().setAvailableOffers(updOffer.getAvailableOffers());
		offerEntity.get().setBoughtOffers(updOffer.getBoughtOffers());
		offerEntity.get().setImagePath(updOffer.getImagePath());
		return new ResponseEntity<OfferEntity>(offerRepository.save(offerEntity.get()), HttpStatus.OK);

	}

	// 3.6 --> T5
	@JsonView(Views.Private.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public ResponseEntity<?> deleteOffer(@PathVariable Integer id) {
		Optional<OfferEntity> offerEntity = offerRepository.findById(id);
		if (offerEntity.isPresent()) {
			// delete offer if existing, otherwise Error
			offerRepository.delete(offerEntity.get());
			return new ResponseEntity<OfferEntity>(offerEntity.get(), HttpStatus.OK);
		}
		return new ResponseEntity<RESTError>(new RESTError(34354, "Offer not in database."), HttpStatus.BAD_REQUEST);
	}

	// 3.7 --> T5
	@JsonView(Views.Public.class)
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public ResponseEntity<?> getByID(@PathVariable Integer id) {
		Optional<OfferEntity> offerEntity = offerRepository.findById(id);
		if (offerEntity.isPresent()) {
			return new ResponseEntity<OfferEntity>(offerEntity.get(), HttpStatus.OK);
		}
		return new ResponseEntity<RESTError>(new RESTError(34354, "Offer not in database."), HttpStatus.BAD_REQUEST);
	}

	// 3.8 --> T5
	@JsonView(Views.Private.class)
	@RequestMapping(method = RequestMethod.PUT, value = "changeOffer/{id}/status/{status}")
	public ResponseEntity<?> editOffer(@PathVariable Integer id, @PathVariable EOfferStatus status) {
		Optional<OfferEntity> offerEntity = offerRepository.findById(id);
		if (!offerEntity.isPresent()) {
			return new ResponseEntity<RESTError>(new RESTError(34354, "Offer not in database."),
					HttpStatus.BAD_REQUEST);
		}
		offerEntity.get().setOfferStatus(status);
		if (status == EOfferStatus.EXPIRED) {
			billService.cancelAllBillsForExpiredOffer(offerEntity);
		}
		return new ResponseEntity<OfferEntity>(offerRepository.save(offerEntity.get()), HttpStatus.OK);
	}

	// 3.9 --> T5
	@JsonView(Views.Public.class)
	@RequestMapping(method = RequestMethod.GET, value = "/findByPrice/{price1}/and/{price2}")
	public ResponseEntity<?> withinRange(@PathVariable Double price1, @PathVariable Double price2) {
		return new ResponseEntity<List<OfferEntity>>(
				offerRepository.findByRegularPriceBetweenOrderByRegularPriceAsc(price1, price2), HttpStatus.OK);
	}

	// 3.4* --> 2.3 --> T5 --> T6 
	@JsonView(Views.Private.class)
	@RequestMapping(method = RequestMethod.POST, value = "/{categoryID}/seller/{sellerID}")
	public ResponseEntity<?> addOfferRevisited(@Valid @RequestBody OfferEntity newOffer,
			@PathVariable Integer categoryID, @PathVariable Integer sellerID) {

		if (userRepository.findById(sellerID).isEmpty()) {
			return new ResponseEntity<RESTError>(new RESTError(1209, "No specified user in database."),
					HttpStatus.BAD_REQUEST);
		}

		if (categoryRepository.findById(categoryID).isEmpty()) {
			return new ResponseEntity<RESTError>(new RESTError(1210, "No specified category in database."),
					HttpStatus.BAD_REQUEST);
		}

		if (userRepository.findById(sellerID).get().getUserRole().equals(ERole.ROLE_SELLER)) {
			OfferEntity offerEntity = new OfferEntity();
			offerEntity.setOfferName(newOffer.getOfferName());
			offerEntity.setOfferDescription(newOffer.getOfferDescription());
			offerEntity.setRegularPrice(newOffer.getRegularPrice());
			offerEntity.setActionPrice(newOffer.getActionPrice());
			offerEntity.setOfferCreated(LocalDate.now()); // now!
			offerEntity.setImagePath(newOffer.getImagePath());
			offerEntity.setCategory(categoryRepository.findById(categoryID).get());
			offerEntity.setUser(userRepository.findById(sellerID).get());
			if (newOffer.getAvailableOffers() != null) {
				offerEntity.setAvailableOffers(newOffer.getAvailableOffers());
			} else {
				offerEntity.setAvailableOffers(1);// set min required available offers
			}
			offerEntity.setBoughtOffers(0);
			offerEntity.setOfferStatus(EOfferStatus.WAIT_FOR_APPROVING);// default status
			return new ResponseEntity<OfferEntity>(offerRepository.save(offerEntity), HttpStatus.OK);
		}
		return new ResponseEntity<RESTError>(new RESTError(555, "Selected user in not a seller!"),
				HttpStatus.BAD_REQUEST);
	}

	// 3.5* --> 2.4 --> T5
	@JsonView(Views.Private.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/{offerId}/category/{categoryId}")
	public ResponseEntity<?> editOfferCategory(@PathVariable Integer offerId, @PathVariable Integer categoryId) {
		Optional<OfferEntity> offerEntity = offerRepository.findById(offerId);
		if (!offerEntity.isPresent()) {
			return new ResponseEntity<RESTError>(new RESTError(34354, "Offer not in database."),
					HttpStatus.BAD_REQUEST);
		}
		Optional<CategoryEntity> categoryEntity = categoryRepository.findById(categoryId);
		// modify offer and return if existing, otherwise null; also check if updOffer
		// has nulls to avoid updating all fields
		if (!categoryEntity.isPresent()) {
			return new ResponseEntity<RESTError>(new RESTError(1210, "No specified category in database."),
					HttpStatus.BAD_REQUEST);
		}
		offerEntity.get().setCategory(categoryEntity.get());
		return new ResponseEntity<OfferEntity>(offerRepository.save(offerEntity.get()), HttpStatus.OK);
	}

	// 2.1 service --> T5
	@JsonView(Views.Private.class)
	@RequestMapping(value = "/updateAvailableOffers/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateAvailableOffers(@PathVariable Integer id, @RequestParam Integer boughtOffers,
			@RequestParam Integer availableOffers) {
		if (id == null || boughtOffers == null || availableOffers == null) {
			return new ResponseEntity<RESTError>(
					new RESTError(65568, "All fields are mandatory, please make sure all values are provided."),
					HttpStatus.BAD_REQUEST);
		}
		Optional<OfferEntity> offerEntity = offerRepository.findById(id);
		if (!offerEntity.isPresent()) {
			return new ResponseEntity<RESTError>(new RESTError(34354, "Offer not in database."),
					HttpStatus.BAD_REQUEST);
		}

		offerEntity.get().setAvailableOffers(availableOffers);
		offerEntity.get().setBoughtOffers(boughtOffers);
		return new ResponseEntity<OfferEntity>(offerRepository.save(offerEntity.get()), HttpStatus.OK);
	}

	// 3.2 --> T5
	@JsonView(Views.Private.class)
	@RequestMapping(method = RequestMethod.POST, path = "/uploadImage/{id}")
	public ResponseEntity<?> uploadImageForOffer(@RequestParam(name = "file") MultipartFile file,
			@PathVariable(name = "id") Integer id) {

		if (id == null || file == null)
			return new ResponseEntity<RESTError>(
					new RESTError(65568, "All fields are mandatory, please make sure all values are provided."),
					HttpStatus.BAD_REQUEST);

		// Check do offer exists and get it. Otherwise return null
		Optional<OfferEntity> op = offerRepository.findById(id);
		if (op.isEmpty()) {
			return new ResponseEntity<RESTError>(new RESTError(34354, "Offer not in database."),
					HttpStatus.BAD_REQUEST);
		}
		OfferEntity offer = op.get();
		// chack file format and validate image	
		String retVal = null;
		try {
			retVal = offerService.uploadOfferImage(file);
			if (retVal == null || retVal == "") {
				return new ResponseEntity<RESTError>(new RESTError(343555, "Problems with writing file to disk."),
						HttpStatus.NOT_FOUND);
			}
			offer.setImagePath(retVal);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Return service retVal
		return new ResponseEntity<OfferEntity>(offerRepository.save(offer), HttpStatus.OK);
	}

	// set prices --> T5
	@RequestMapping(method = RequestMethod.PUT, value = "/setPrices/{offerID}")
	public ResponseEntity<?> setPrices(@PathVariable Integer offerID, @RequestParam Double regularPrice,
			@RequestParam Double actionPrice) {
		Optional<OfferEntity> offerEntity = offerRepository.findById(offerID);
		if (offerEntity.isEmpty()) {
			new ResponseEntity<RESTError>(new RESTError(666, "Offer not in database."), HttpStatus.NOT_FOUND);
		}
		if (regularPrice != null) {
			offerEntity.get().setRegularPrice(regularPrice);
		}
		if (actionPrice != null) {
			offerEntity.get().setActionPrice(actionPrice);
		}

		return new ResponseEntity<OfferEntity>(offerRepository.save(offerEntity.get()), HttpStatus.OK);
	}
}