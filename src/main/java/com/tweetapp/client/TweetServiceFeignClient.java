package com.tweetapp.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tweetapp.models.TweetDto;

@FeignClient(value = "TWEET-SERVICE", url = "${feign.client.uri.tweetsurl}")
public interface TweetServiceFeignClient {


	@RequestMapping(value = "/{userName}/add", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<String> postNewTweet(@RequestBody TweetDto tweetDto,@PathVariable String userName) ;
	
	@RequestMapping(value = "/{userName}/update/{id}", method = RequestMethod.PUT, produces = "application/json")
	public ResponseEntity<String> updateTweet(@RequestBody TweetDto tweetDto,@PathVariable String userName,@PathVariable int id) ;
	
	@RequestMapping(value = "/{userName}/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public ResponseEntity<String> deleteTweet(@RequestBody TweetDto tweetDto,@PathVariable String userName,@PathVariable int id) ;
	
	@RequestMapping(value = "/{userName}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<TweetDto>> getUserTweets(@PathVariable String userName) ;
	
	@RequestMapping(value = "/all", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<TweetDto>> getAllTweets() ;
}
