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

	// 1.3
	@JsonView(Views.Public.class)
	@RequestMapping(method = RequestMethod.GET, value = "/public")
	public ResponseEntity<?> getUsersPublic() {
		// get all users from DB
		List<UserEntity> users = (List<UserEntity>) userRepository.findAll();
		if (users.isEmpty()) {
			return new ResponseEntity<RESTError>(new RESTError(4, "No users in database"), HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<UserEntity>>(users, HttpStatus.OK);
	}

	@JsonView(Views.Private.class)
	@RequestMapping(method = RequestMethod.GET, value = "/private")
	public ResponseEntity<?> getUsersPrivate() {
		// get all users from DB
		List<UserEntity> users = (List<UserEntity>) userRepository.findAll();
		if (users.isEmpty()) {
			return new ResponseEntity<RESTError>(new RESTError(4, "No users in database"), HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<UserEntity>>(users, HttpStatus.OK);
	}

	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.GET, value = "/admin")
	public ResponseEntity<?> getUsersAdmin() {
		// get all users from DB
		List<UserEntity> users = (List<UserEntity>) userRepository.findAll();
		if (users.isEmpty()) {
			return new ResponseEntity<RESTError>(new RESTError(4, "No users in database"), HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<UserEntity>>(users, HttpStatus.OK);
	}

	// 1.4
	@JsonView(Views.Private.class)
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

	// 1.5 deprecated in T5
	/*@JsonView(Views.Private.class)
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
	}*/

	// 1.5 - T5
	@RequestMapping(method = RequestMethod.POST, value = "")
	public ResponseEntity<?> addUser(@RequestBody UserDTO user) {

		// perform check if same username is used in database
		if (userRepository.findByUsername(user.getUsername()).isPresent()) {
			return new ResponseEntity<RESTError>(new RESTError(444, "Username already used, choose another."),
					HttpStatus.FORBIDDEN);
		}

		// check for matching password and repeatedPassword
		if (!user.getPassword().equals(user.getRepeatedPassword())) {
			return new ResponseEntity<RESTError>(new RESTError(555, "Passwords not matching"), HttpStatus.FORBIDDEN);
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

	// 1.6
	@JsonView(Views.Private.class)
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
	@JsonView(Views.Private.class)
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
	@JsonView(Views.Admin.class)
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
	@JsonView(Views.Admin.class)
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
	@JsonView(Views.Admin.class)
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
