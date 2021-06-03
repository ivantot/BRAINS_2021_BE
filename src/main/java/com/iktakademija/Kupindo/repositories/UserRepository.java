package com.iktakademija.Kupindo.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.iktakademija.Kupindo.entities.UserEntity;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {
	
	Optional<UserEntity> findByUsername (String username);

}
