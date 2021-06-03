package com.iktakademija.Kupindo.controllers;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iktakademija.Kupindo.entities.UserEntity;
import com.iktakademija.Kupindo.repositories.UserRepository;
import com.iktakademija.Kupindo.res.ERole;

@RestController
@RequestMapping("/kupindo/users")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	// 1.3
	@RequestMapping(method = RequestMethod.GET, value = "")
	public List<UserEntity> getUsers() {
		// get all users from DB
		return (List<UserEntity>) userRepository.findAll();
	}

	// 1.4
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public UserEntity getUserByID(@PathVariable("id") Integer clientID) {
		// find by id
		// get a users from DB --> if - selektorski izraz!!!!
		Optional<UserEntity> op = userRepository.findById(clientID);
		if (op.isPresent()) {
			return op.get();
		}
		return null;
	}

	// 1.5

	@RequestMapping(method = RequestMethod.POST, value = "")
	public UserEntity addUser(@RequestBody UserEntity user) {
		UserEntity userEntity = new UserEntity();
		userEntity.setFirstName(user.getFirstName());
		userEntity.setLastName(user.getLastName());
		userEntity.setUsername(user.getUsername());
		userEntity.setPassword(user.getPassword());
		userEntity.setEmail(user.getEmail());
		userEntity.setUserRole(ERole.ROLE_CUSTOMER);
		return userRepository.save(userEntity);
	}

	// 1.6
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	public UserEntity updateUser(@PathVariable Integer id, @RequestBody UserEntity updUser) {
		// get a users from DB
		Optional<UserEntity> op = userRepository.findById(id);
		if (op.isPresent()) {
			if (updUser.getFirstName() != null) {
				op.get().setFirstName(updUser.getFirstName());
			}
			if (updUser.getLastName() != null) {
				op.get().setLastName(updUser.getLastName());
			}
			if (updUser.getEmail() != null) {
				op.get().setEmail(updUser.getEmail());
			}
			if (updUser.getUsername() != null) {
				op.get().setUsername(updUser.getUsername());
			}
			userRepository.save(op.get());
			return updUser;
		}

		return null;
	}

	// 1.7
	@RequestMapping(method = RequestMethod.PUT, value = "/change/{id}/role/{role}")
	public UserEntity updateRole(@PathVariable Integer id, @PathVariable ERole role) {
		// get a users from DB
		Optional<UserEntity> op = userRepository.findById(id);
		if (op.isPresent()) {
			// if id exists return user with updated field
			op.get().setUserRole(role);
			return userRepository.save(op.get());
		}
		return null;
	}

	// 1.8
	@RequestMapping(method = RequestMethod.PUT, value = "/changePassword/{id}")
	public UserEntity updatePassword(@RequestParam String old_password, @RequestParam String new_password,
			@PathVariable Integer id) {
		// get a users from DB
		Optional<UserEntity> op = userRepository.findById(id);
		if (op.isPresent()) {
		}
		// if id exists return user with updated field
		if (op.get().getPassword().equals(old_password)) {
			op.get().setPassword(new_password);
			userRepository.save(op.get());
			return op.get();
		}
		return null;
	}

	// 1.9
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public UserEntity deleteUser(@PathVariable Integer id) {
		// get a users from DB
		Optional<UserEntity> op = userRepository.findById(id);
		if (op.isPresent()) {
		}
		// if id exists return user with updated field
		userRepository.delete(op.get());
		return op.get();
	}

	// 1.10 / bolje resenje je filtriranje sa @RequestParams za sva polja i
	// koriscenje /users kao path
	@RequestMapping(method = RequestMethod.GET, value = "/by_username")
	public UserEntity getByUsername(@RequestParam String username) {
		// get a users from DB
		Optional<UserEntity> op = userRepository.findByUsername(username);
		if (op.isPresent()) {
			return op.get();
		}
		return null;
	}

	// -------------------------------------------------------------------------------------

}
