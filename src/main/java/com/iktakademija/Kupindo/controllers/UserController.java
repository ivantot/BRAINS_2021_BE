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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.Kupindo.entities.UserEntity;
import com.iktakademija.Kupindo.entities.dto.UserDTO;
import com.iktakademija.Kupindo.repositories.UserRepository;
import com.iktakademija.Kupindo.res.ERole;
import com.iktakademija.Kupindo.security.Views;
import com.iktakademija.Kupindo.utils.RESTError;

@RestController
@RequestMapping("/kupindo/users")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	// 1.3 --> T5
	@JsonView(Views.Public.class)
	@RequestMapping(method = RequestMethod.GET, value = "/public")
	public ResponseEntity<?> getUsersPublic() {
		// get all users from DB
		List<UserEntity> users = (List<UserEntity>) userRepository.findAll();
		if (users.isEmpty()) {
			return new ResponseEntity<RESTError>(new RESTError(4, "No users in database."), HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<UserEntity>>(users, HttpStatus.OK);
	}

	@JsonView(Views.Private.class)
	@RequestMapping(method = RequestMethod.GET, value = "/private")
	public ResponseEntity<?> getUsersPrivate() {
		// get all users from DB
		List<UserEntity> users = (List<UserEntity>) userRepository.findAll();
		if (users.isEmpty()) {
			return new ResponseEntity<RESTError>(new RESTError(4, "No users in database."), HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<UserEntity>>(users, HttpStatus.OK);
	}

	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.GET, value = "/admin")
	public ResponseEntity<?> getUsersAdmin() {
		// get all users from DB
		List<UserEntity> users = (List<UserEntity>) userRepository.findAll();
		if (users.isEmpty()) {
			return new ResponseEntity<RESTError>(new RESTError(4, "No users in database."), HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<UserEntity>>(users, HttpStatus.OK);
	}

	// 1.4 --> T5 
	@JsonView(Views.Private.class)
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public ResponseEntity<?> getUserByID(@PathVariable("id") Integer clientID) {
		// find by id
		// get a users from DB --> if - selektorski izraz!!!!
		Optional<UserEntity> op = userRepository.findById(clientID);
		if (!op.isPresent()) {
			return new ResponseEntity<RESTError>(new RESTError(4, "No users in database."), HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<UserEntity>(op.get(), HttpStatus.OK);
	}

	// 1.5 --> T5
	@RequestMapping(method = RequestMethod.POST, value = "")
	@JsonView(Views.Private.class)
	public ResponseEntity<?> addUser(@Valid @RequestBody UserDTO user) {

		// perform check if same username is used in database
		if (userRepository.findByUsername(user.getUsername()).isPresent()) {
			return new ResponseEntity<RESTError>(new RESTError(444, "Username already used, choose another."),
					HttpStatus.FORBIDDEN);
		}

		// check for matching password and repeatedPassword
		if (!user.getPassword().equals(user.getRepeatedPassword())) {
			return new ResponseEntity<RESTError>(new RESTError(555, "Passwords not matching."), HttpStatus.FORBIDDEN);
		}

		UserEntity newUser = new UserEntity();

		newUser.setUsername(user.getUsername());
		newUser.setEmail(user.getEmail());
		newUser.setPassword(user.getPassword());
		newUser.setFirstName(user.getFirstName());
		newUser.setLastName(user.getLastName());
		newUser.setUserRole(ERole.ROLE_CUSTOMER);

		return new ResponseEntity<UserEntity>(userRepository.save(newUser), HttpStatus.OK);
	}

	// 1.6 --> T5
	@JsonView(Views.Private.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody UserEntity updUser) {
		// get a users from DB
		Optional<UserEntity> op = userRepository.findById(id);
		if (!op.isPresent()) {
			return new ResponseEntity<RESTError>(new RESTError(4, "No users in database."), HttpStatus.NO_CONTENT);
		}
		op.get().setFirstName(updUser.getFirstName());
		op.get().setLastName(updUser.getLastName());
		op.get().setEmail(updUser.getEmail());
		op.get().setUsername(updUser.getUsername());
		return new ResponseEntity<UserEntity>(userRepository.save(op.get()), HttpStatus.OK);
	}

	// 1.7 --> T5
	@JsonView(Views.Private.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/change/{id}/role/{role}")
	public ResponseEntity<?> updateRole(@PathVariable Integer id, @PathVariable ERole role) {
		// get a users from DB
		Optional<UserEntity> op = userRepository.findById(id);
		if (!op.isPresent()) {
			return new ResponseEntity<RESTError>(new RESTError(4, "No users in database."), HttpStatus.NO_CONTENT);
		}
		// if id exists return user with updated field
		op.get().setUserRole(role);
		return new ResponseEntity<UserEntity>(userRepository.save(op.get()), HttpStatus.OK);
	}

	// 1.8 --> T5 
	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/changePassword/{id}")
	public ResponseEntity<?> updatePassword(@RequestParam String old_password, @RequestParam String new_password,
			@PathVariable Integer id) {
		// get a users from DB
		Optional<UserEntity> op = userRepository.findById(id);
		if (!op.isPresent()) {
			return new ResponseEntity<RESTError>(new RESTError(4, "No users in database."), HttpStatus.NO_CONTENT);
		}
		// if id exists return user with updated field
		if (op.get().getPassword().equals(old_password)) {
			op.get().setPassword(new_password);
			return new ResponseEntity<UserEntity>(userRepository.save(op.get()), HttpStatus.OK);
		}
		return new ResponseEntity<RESTError>(new RESTError(464, "Password not correct."), HttpStatus.NO_CONTENT);
	}

	// 1.9 --> T5
	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
		// get a users from DB
		Optional<UserEntity> op = userRepository.findById(id);
		if (!op.isPresent()) {
			return new ResponseEntity<RESTError>(new RESTError(4, "No users in database."), HttpStatus.NO_CONTENT);
		}
		userRepository.delete(op.get());
		return new ResponseEntity<UserEntity>(op.get(), HttpStatus.OK);
	}

	// 1.10 / bolje resenje je filtriranje sa @RequestParams za sva polja i
	// koriscenje /users kao path --> T5
	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.GET, value = "/by_username")
	public ResponseEntity<?> getByUsername(@RequestParam String username) {
		// get a users from DB
		Optional<UserEntity> op = userRepository.findByUsername(username);
		if (!op.isPresent()) {
			return new ResponseEntity<RESTError>(new RESTError(4, "No user with requested username in database."),
					HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<UserEntity>(op.get(), HttpStatus.OK);
	}
}
