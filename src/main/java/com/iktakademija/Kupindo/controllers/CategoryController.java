package com.iktakademija.Kupindo.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iktakademija.Kupindo.entities.CategoryEntity;
import com.iktakademija.Kupindo.repositories.CategoryRepository;

@RestController
@RequestMapping(path = "/kupindo/categories")
public class CategoryController {

	@Autowired
	private CategoryRepository categoryRepository;

	// 2.3
	@RequestMapping(method = RequestMethod.GET, value = "")
	public List<CategoryEntity> getCategories() {
		// get Categories from db
		List<CategoryEntity> categoryEntity = (List<CategoryEntity>) categoryRepository.findAll();
		return categoryEntity;
	}

	// 2.4
	@RequestMapping(method = RequestMethod.POST, value = "")
	public CategoryEntity addCategory(@RequestBody CategoryEntity category) {
		// add a new category with a next ID
		CategoryEntity categoryEntity = new CategoryEntity();
		categoryEntity.setCategoryName(category.getCategoryName());
		categoryEntity.setCategoryDescription(category.getCategoryDescription());
		return categoryRepository.save(categoryEntity);
	}

	// 2.5
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

	// 2.7
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
