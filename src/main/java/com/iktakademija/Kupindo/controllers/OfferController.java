package com.iktakademija.Kupindo.controllers;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iktakademija.Kupindo.entities.CategoryEntity;
import com.iktakademija.Kupindo.entities.OfferEntity;
import com.iktakademija.Kupindo.repositories.CategoryRepository;
import com.iktakademija.Kupindo.repositories.OfferRepository;
import com.iktakademija.Kupindo.repositories.UserRepository;
import com.iktakademija.Kupindo.res.EOfferStatus;
import com.iktakademija.Kupindo.res.ERole;
import com.iktakademija.Kupindo.services.OfferService;

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

	// 3.3
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<OfferEntity> getAllOffers() {
		return (List<OfferEntity>) offerRepository.findAll();
	}

	// 3.4
	@RequestMapping(method = RequestMethod.POST, value = "")
	public OfferEntity addOffer(@RequestBody OfferEntity newOffer) {
		OfferEntity offerEntity = new OfferEntity();
		offerEntity.setOfferName(newOffer.getOfferName());
		offerEntity.setOfferDescription(newOffer.getOfferDescription());
		offerEntity.setRegularPrice(newOffer.getRegularPrice());
		offerEntity.setOfferCreated(new Date()); // now!
		offerEntity.setImagePath(newOffer.getImagePath());
		offerEntity.setCategory(newOffer.getCategory());
		offerEntity.setUser(newOffer.getUser());
		offerEntity.setAvailableOffers(1);// set min required available offers
		offerEntity.setBoughtOffers(0);// set min required available offers
		offerEntity.setOfferStatus(EOfferStatus.WAIT_FOR_APPROVING);// default status
		return offerRepository.save(offerEntity);
	}

	// 3.5
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	public OfferEntity editOffer(@PathVariable Integer id, @RequestBody OfferEntity updOffer) {
		Optional<OfferEntity> offerEntity = offerRepository.findById(id);
		if (offerEntity.isPresent()) {
			// modify offer and return if existing, otherwise null; also check if updOffer
			// has nulls to avoid updating all fields
			if (updOffer.getOfferName() != null) {
				offerEntity.get().setOfferName(updOffer.getOfferName());
			}
			if (updOffer.getOfferDescription() != null) {
				offerEntity.get().setOfferDescription(updOffer.getOfferDescription());
			}
			if (updOffer.getRegularPrice() != null) {
				offerEntity.get().setRegularPrice(updOffer.getRegularPrice());
			}
			if (updOffer.getActionPrice() != null) {
				offerEntity.get().setActionPrice(updOffer.getActionPrice());
			}
			if (updOffer.getAvailableOffers() != null) {
				offerEntity.get().setAvailableOffers(updOffer.getAvailableOffers());
			}
			if (updOffer.getBoughtOffers() != null) {
				offerEntity.get().setBoughtOffers(updOffer.getBoughtOffers());
			}
			if (updOffer.getImagePath() != null) {
				offerEntity.get().setImagePath(updOffer.getImagePath());
			}
			return offerRepository.save(offerEntity.get());
		}
		return null;
	}

	// 3.6
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public OfferEntity deleteUser(@PathVariable Integer id) {
		Optional<OfferEntity> offerEntity = offerRepository.findById(id);
		if (offerEntity.isPresent()) {
			// delete offer if existing, otherwise null
			offerRepository.delete(offerEntity.get());
			return offerEntity.get();
		}
		return null;
	}

	// 3.7
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public OfferEntity getByID(@PathVariable Integer id) {
		Optional<OfferEntity> offerEntity = offerRepository.findById(id);
		if (offerEntity.isPresent()) {
			return offerEntity.get();
		}
		return null;
	}

	// 3.8
	@RequestMapping(method = RequestMethod.PUT, value = "changeOffer/{id}/status/{status}")
	public OfferEntity editOffer(@PathVariable Integer id, @PathVariable EOfferStatus status) {
		Optional<OfferEntity> offerEntity = offerRepository.findById(id);
		if (offerEntity.isPresent()) {
			// modify offer and return if existing, otherwise null
			offerEntity.get().setOfferStatus(status);
			return offerRepository.save(offerEntity.get());
		}
		return null;
	}

	// 3.9
	@RequestMapping(method = RequestMethod.GET, value = "/findByPrice/{price1}/and/{price2}")
	public List<OfferEntity> withinRange(@PathVariable Double price1, @PathVariable Double price2) {
		return offerRepository.findByRegularPriceBetweenOrderByRegularPriceAsc(price1, price2);
	}

	// 3.4* --> 2.3
	@RequestMapping(method = RequestMethod.POST, value = "/{categoryId}/seller/{sellerId}")
	public OfferEntity addOfferRevisited(@RequestBody OfferEntity newOffer, @PathVariable Integer categoryId,
			@PathVariable Integer sellerId) {
		if (userRepository.findById(sellerId).get().getUserRole().equals(ERole.ROLE_SELLER)) {
			OfferEntity offerEntity = new OfferEntity();
			offerEntity.setOfferName(newOffer.getOfferName());
			offerEntity.setOfferDescription(newOffer.getOfferDescription());
			offerEntity.setRegularPrice(newOffer.getRegularPrice());
			offerEntity.setOfferCreated(new Date()); // now!
			offerEntity.setImagePath(newOffer.getImagePath());
			offerEntity.setCategory(categoryRepository.findById(categoryId).get());
			offerEntity.setUser(userRepository.findById(sellerId).get());
			if (newOffer.getBoughtOffers() != null) {
				offerEntity.setAvailableOffers(newOffer.getBoughtOffers());
			} else {
				offerEntity.setAvailableOffers(1);// set min required available offers
			}
			offerEntity.setBoughtOffers(0);
			offerEntity.setOfferStatus(EOfferStatus.WAIT_FOR_APPROVING);// default status
			return offerRepository.save(offerEntity);
		}
		return null;
	}

	// 3.5* --> 2.4
	@RequestMapping(method = RequestMethod.PUT, value = "/{offerId}/category/{categoryId}")
	public OfferEntity editOfferCategory(@PathVariable Integer offerId, @PathVariable Integer categoryId) {
		Optional<OfferEntity> offerEntity = offerRepository.findById(offerId);
		if (offerEntity.isPresent()) {
			Optional<CategoryEntity> categoryEntity = categoryRepository.findById(categoryId);
			// modify offer and return if existing, otherwise null; also check if updOffer
			// has nulls to avoid updating all fields
			if (categoryEntity.isPresent()) {
				offerEntity.get().setCategory(categoryEntity.get());
				return offerRepository.save(offerEntity.get());
			}
		}
		return null;
	}

	// 2.1 service
	@RequestMapping(value = "/updateAvailableOffers/{id}", method = RequestMethod.PUT)
	public String updateAvailableOffers(@PathVariable Integer id, @RequestParam Integer boughtOffers,
			@RequestParam Integer availableOffers) {
		if (id == null || boughtOffers == null || availableOffers == null) {
			return null;
		}
		String retVal = offerService.updateAvailableOffers(id, boughtOffers, availableOffers);
		return retVal;
	}
}