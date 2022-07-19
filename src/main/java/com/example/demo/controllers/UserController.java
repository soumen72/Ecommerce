package com.example.demo.controllers;

import java.security.InvalidKeyException;
import java.util.Optional;

import com.example.demo.controllers.Exceptions.RuntimeExceptionHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

	//private static Logger log = LoggerFactory.getLogger;


	private Logger logmsg = LoggerFactory.getLogger(UserController.class);
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;




	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) throws RuntimeExceptionHandle {
		User exists = userRepository.findByUsername(createUserRequest.getUsername());
		if (exists != null) {
			logmsg.error("[Fail] (username already exist ) for user : " + createUserRequest.getUsername() +", log error msg : invalid password" );
			throw  new RuntimeExceptionHandle("username already exist, enter new username") ;
		}
		User user = new User();
		user.setUsername(createUserRequest.getUsername());

		logmsg.debug("[working] Username set ", createUserRequest.getUsername());
		Cart cart = new Cart();
		cartRepository.save(cart);
		if(createUserRequest.getPass().length() < 4 || !createUserRequest.getPass().equals(createUserRequest.getConfirmPass())) {
			logmsg.error(" [Fail] Error with user password. Cannot create user (length not correct   )", createUserRequest.getUsername());
			throw  new RuntimeExceptionHandle("Error with user password") ;

		}
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPass()));
		user.setCart(cart);
		userRepository.save(user);

		logmsg.info("[Success] Created user Successful ", user.getUsername());
		return ResponseEntity.ok(user);
	}
	
}
