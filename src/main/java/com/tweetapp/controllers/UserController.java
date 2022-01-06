package com.tweetapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.dao.UserRepository;
import com.tweetapp.entity.User;
import com.tweetapp.models.JwtRequest;
import com.tweetapp.models.JwtResponse;
import com.tweetapp.models.ResetPasswordDto;
import com.tweetapp.service.impl.UserServiceImpl;
import com.tweetapp.utils.JwtTokenUtil;

@RestController
@RequestMapping("/authenticate")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

	@Autowired
	UserServiceImpl userService;


	
	@RequestMapping(value = "/register", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<User> registerUser(@RequestBody User userDto) {
		
		
		User userDetails = userService.registerUser(userDto);
		return new ResponseEntity<User>(userDetails, HttpStatus.OK);
	}

	@PostMapping("/login")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest)  {

		
		JwtResponse jwtResponse = userService.login(authenticationRequest);

		return ResponseEntity.ok(jwtResponse);
	}
	
	@GetMapping("/{userName}/forget-password")
	public void forgetPassword(@PathVariable String userName)  {
		String otp = userService.generateOTP(userName);
		userService.forgetPassword(userName,otp);

	}
	
	@GetMapping("/{userName}/verify")
	public ResponseEntity<Boolean> verifyOTP(@PathVariable String userName, @RequestParam String otp)  {
		
		Boolean status = userService.verifyOTP(userName,otp);
		
		return new ResponseEntity<Boolean>(status, HttpStatus.OK);

	}
	
	@PostMapping("/{userName}/reset")
	public ResponseEntity<User> reset(@RequestBody ResetPasswordDto resetPasswordDto, @PathVariable String userName)  {
		
		User user = userService.reset(resetPasswordDto,userName);
		
		return new ResponseEntity<User>(user, HttpStatus.OK);

	}

	


	
}
