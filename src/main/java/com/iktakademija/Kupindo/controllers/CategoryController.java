package com.iktakademija.Kupindo.controllers;

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
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.Kupindo.entities.CategoryEntity;
import com.iktakademija.Kupindo.repositories.CategoryRepository;
import com.iktakademija.Kupindo.security.Views;
import com.iktakademija.Kupindo.services.BillService;
import com.iktakademija.Kupindo.services.OfferService;
import com.iktakademija.Kupindo.utils.RESTError;

@RestController
@RequestMapping(path = "/kupindo/categories")
public class CategoryController {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired

	private OfferService offerService;
	@Autowired

	private BillService billService;

	// 2.3 --> T5
	@JsonView(Views.Public.class)
	@RequestMapping(method = RequestMethod.GET, value = "")
	public ResponseEntity<?> getCategories() {
		// get Categories from db
		List<CategoryEntity> categoryEntity = (List<CategoryEntity>) categoryRepository.findAll();
		if (categoryEntity.isEmpty()) {
			return new ResponseEntity<RESTError>(new RESTError(2, "No categories in database."), HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity<List<CategoryEntity>>(categoryEntity, HttpStatus.OK);
	}

	// 2.4 --> T6
	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.POST, value = "")
	public ResponseEntity<?> addCategory(@Valid @RequestBody CategoryEntity category) {
		// add a new category with a next ID
		CategoryEntity categoryEntity = new CategoryEntity();
		categoryEntity.setCategoryName(category.getCategoryName());
		categoryEntity.setCategoryDescription(category.getCategoryDescription());
		return new ResponseEntity<CategoryEntity>(categoryRepository.save(categoryEntity), HttpStatus.OK);
	}

	// 2.5 --> T5
	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	public ResponseEntity<?> editCategory(@PathVariable Integer id, @RequestBody CategoryEntity newCategory) {
		// get Category by id from db
		Optional<CategoryEntity> categoryEntity = categoryRepository.findById(id);
		if (!categoryEntity.isPresent()) {

			return new ResponseEntity<RESTError>(new RESTError(3435, "Category not in database."),
					HttpStatus.BAD_REQUEST);
		}

		categoryEntity.get().setCategoryDescription(newCategory.getCategoryDescription());
		categoryEntity.get().setCategoryName(newCategory.getCategoryName());
		return new ResponseEntity<CategoryEntity>(categoryRepository.save(categoryEntity.get()), HttpStatus.OK);

	}

	// 2.6 --> T5
	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public ResponseEntity<?> deleteEntity(@PathVariable Integer id) {
		// get Category by id from db
		Optional<CategoryEntity> categoryEntity = categoryRepository.findById(id);
		if (!categoryEntity.isPresent()) {
			return new ResponseEntity<RESTError>(new RESTError(3435, "Category not in database."),
					HttpStatus.BAD_REQUEST);
		}
		if (billService.categoryInBillsExists(id)) {
			return new ResponseEntity<RESTError>(
					new RESTError(3435,
							"There are bills related to category, take care of bills before deleting category."),
					HttpStatus.BAD_REQUEST);

		}

		if (offerService.categoryInOffersExists(id)) {
			return new ResponseEntity<RESTError>(
					new RESTError(3435,
							"There are offers related to category, take care of offers before deleting category."),
					HttpStatus.BAD_REQUEST);

		}
		categoryRepository.delete(categoryEntity.get());
		return new ResponseEntity<CategoryEntity>(categoryEntity.get(), HttpStatus.OK);
	}

	// 2.7 --> T5
	@JsonView(Views.Public.class)
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public ResponseEntity<?> getById(@PathVariable Integer id) {
		// get Category by id from db
		Optional<CategoryEntity> categoryEntity = categoryRepository.findById(id);
		if (!categoryEntity.isPresent()) {

			return new ResponseEntity<RESTError>(new RESTError(3435, "Category not in database."),
					HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<CategoryEntity>(categoryEntity.get(), HttpStatus.OK);
	}

}
