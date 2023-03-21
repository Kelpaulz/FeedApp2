package com.bptn.feedApp.controller;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static org.springframework.http.HttpStatus.OK;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bptn.feedApp.jpa.User;
import com.bptn.feedApp.service.UserService;

@CrossOrigin(exposedHeaders = "Authorization")
@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserService userService;
	final Logger logger = LoggerFactory.getLogger(this.getClass());

	@GetMapping("/test")
	public String testController() {
		logger.debug("The testController() method was invoked!");
		return "The FeedApp application is up and running";
	}

	@GetMapping("/")
	public List<User> listUsers() {
//	public List<UserBean> listUsers() {
	
		logger.debug("The listUsers() method was invoked!");
		return this.userService.listUsers();
	}

	@GetMapping("/{username}")
	public Optional<User> findByUsername(@PathVariable String username) {
	//public UserBean findByUsername(@PathVariable String username) {
		logger.debug("The findByUsername() method was invoked!, username={}", username);
		return this.userService.findByUsername(username);
	}

	@GetMapping("/{first}/{last}/{username}/{password}/{phone}/{emailId}")
	public String createUser(@PathVariable String first, @PathVariable String last, @PathVariable String username,
			@PathVariable String password, @PathVariable String phone, @PathVariable String emailId) {
		//UserBean user = new UserBean();
		User user = new User();

		user.setFirstName(first);
		user.setLastName(last);
		user.setUsername(username);
		user.setPassword(password);
		user.setPhone(phone);
		user.setEmailId(emailId);
		user.setEmailVerified(false);
		user.setCreatedOn(Timestamp.from(Instant.now()));

		logger.debug("The createUser() method was invoked!, user={}", user.toString());

		this.userService.createUser(user);

		return "User Created Successfully";
	}
	@PostMapping("/signup")
	public User signup(@RequestBody User user) {
		logger.debug("Signing up, username: {}", user.getUsername());
		return this.userService.signup(user);
	}
	@GetMapping("/verify/email")
	public void verifyEmail() {
			
		logger.debug("Verifying Email");
			
		this.userService.verifyEmail();
	}
	@PostMapping("/login")
	public ResponseEntity<User> login(@RequestBody User user) {
		
		logger.debug("Authenticating, username: {}, password: {}", user.getUsername(), user.getPassword());
			
		/* Spring Security Authentication. */
		user = this.userService.authenticate(user);

		/* Generate JWT and HTTP Header */
		HttpHeaders jwtHeader = this.userService.generateJwtHeader(user.getUsername());
					
		logger.debug("User Authenticated, username: {}", user.getUsername());
			
		return new ResponseEntity<>(user, jwtHeader, OK);
	}
	@GetMapping("/reset/{emailId}")
	public void sendResetPasswordEmail(@PathVariable String emailId) {
			
			logger.debug("Sending Reset Password Email, emailId: {}", emailId);
			
			this.userService.sendResetPasswordEmail(emailId);
	}
}
