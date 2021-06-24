package com.iktakademija.Kupindo.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.iktakademija.Kupindo.entities.CategoryEntity;

public interface CategoryRepository extends CrudRepository<CategoryEntity, Integer	> {

	Optional<CategoryEntity> findByCategoryName(String category);
	
}
