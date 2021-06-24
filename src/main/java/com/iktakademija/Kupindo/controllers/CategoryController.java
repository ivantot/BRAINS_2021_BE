package com.iktakademija.Kupindo.controllers;

import java.util.List;
import java.util.Optional;

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

	// 2.3 -- T5
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

	// 2.4
	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.POST, value = "")
	public CategoryEntity addCategory(@RequestBody CategoryEntity category) {
		// add a new category with a next ID
		CategoryEntity categoryEntity = new CategoryEntity();
		categoryEntity.setCategoryName(category.getCategoryName());
		categoryEntity.setCategoryDescription(category.getCategoryDescription());
		return categoryRepository.save(categoryEntity);
	}

	// 2.5
	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	public CategoryEntity editCategory(@PathVariable Integer id, @RequestBody CategoryEntity newCategory) {
		// get Category by id from db
		Optional<CategoryEntity> categoryEntity = categoryRepository.findById(id);
		if (categoryEntity.isPresent()) {
			// edit a category according the id from path and return the modified value
			if (newCategory.getCategoryDescription() != null) {
				categoryEntity.get().setCategoryDescription(newCategory.getCategoryDescription());
			}
			if (newCategory.getCategoryName() != null) {
				categoryEntity.get().setCategoryName(newCategory.getCategoryName());
			}
			return categoryRepository.save(categoryEntity.get());
		}
		return null;
	}
	/*
	// 2.6
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public CategoryEntity deleteEntity(@PathVariable Integer id) {
		// get Category by id from db
		Optional<CategoryEntity> categoryEntity = categoryRepository.findById(id);
		if (categoryEntity.isPresent()) {
			// check if id exists for categories, if so, return a category that got removed,
			// otherwise
			// return null
			categoryRepository.delete(categoryEntity.get());
			return categoryEntity.get();
		}
		return null;
	}
	 */

	// 2.6
	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public CategoryEntity deleteEntity(@PathVariable Integer id) {
		// get Category by id from db
		Optional<CategoryEntity> categoryEntity = categoryRepository.findById(id);
		if (categoryEntity.isPresent()) {
			// check if id exists for categories, if so, return a category that got removed,
			// otherwise
			// return null
			if (!billService.categoryInBillsExists(id) || !offerService.categoryInOffersExists(id)) {
				categoryRepository.delete(categoryEntity.get());
				return categoryEntity.get();
			}
		}
		return null;
	}

	// 2.7
	@JsonView(Views.Public.class)
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public CategoryEntity getById(@PathVariable Integer id) {
		// get Category by id from db
		Optional<CategoryEntity> categoryEntity = categoryRepository.findById(id);
		if (categoryEntity.isPresent()) {
			return categoryEntity.get();
		}
		return null;
	}

}
