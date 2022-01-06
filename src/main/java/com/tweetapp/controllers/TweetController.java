package com.tweetapp.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.entity.User;
import com.tweetapp.models.TweetDto;
import com.tweetapp.service.impl.UserServiceImpl;

@RestController
@RequestMapping("/tweets")
public class TweetController {
	
	@Autowired
	UserServiceImpl userService;


//	@Autowired
//	TweetService tweetService;
//
//	@RequestMapping(value = "/{userName}/add", method = RequestMethod.POST, produces = "application/json")
//	public ResponseEntity<String> postNewTweet(@RequestBody Tweet tweetDto,@PathVariable String userName) {
//		String  status = tweetService.postNewTweet(tweetDto,userName);
//		return new ResponseEntity<String>(status, HttpStatus.OK);
//	}
	
	
	@GetMapping("/{userName}/logout")
	public void logout(@PathVariable String userName)  {
		
		userService.logout(userName);

	}
	
	@RequestMapping(value = "/users/all", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<User>> getAllUsers() {
		List<User> userList = userService.getAllUsers();
		return new ResponseEntity<List<User>>(userList, HttpStatus.OK);
	}
	
	

	@RequestMapping(value = "/{userName}/add", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<String> postNewTweet(@RequestBody TweetDto tweetDto,@PathVariable String userName) {
		String  status = userService.postNewTweet(tweetDto,userName);
		return new ResponseEntity<String>(status, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{userName}/update/{id}", method = RequestMethod.PUT, produces = "application/json")
	public ResponseEntity<String> updateTweet(@RequestBody TweetDto tweetDto,@PathVariable String userName,@PathVariable int id) {
		String  status = userService.updateTweet(tweetDto,userName,id);
		return new ResponseEntity<String>(status, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{userName}/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public ResponseEntity<String> deleteTweet(@RequestBody TweetDto tweetDto,@PathVariable String userName,@PathVariable int id) {
		String  status = userService.deleteTweet(tweetDto,userName,id);
		return new ResponseEntity<String>(status, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{userName}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<TweetDto>> getUserTweets(@PathVariable String userName) {
		List<TweetDto> tweetList = userService.getUserTweets(userName);
		return new ResponseEntity<List<TweetDto>>(tweetList, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/all", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<TweetDto>> getAllTweets() {
		List<TweetDto> tweetList = userService.getAllTweets();
		return new ResponseEntity<List<TweetDto>>(tweetList, HttpStatus.OK);
	}

	
}
