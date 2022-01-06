package com.tweetapp.models;

import java.sql.Timestamp;
import java.util.Date;

public class AllTweetDto  {
	
	private String userName;
	
	private String tweet;
	
	private String postedDate;
	
	private int tweetId;
	
	

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTweet() {
		return tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
	}

	public String getPostedDate() {
		return postedDate;
	}

	public void setPostedDate(String postedDate) {
		this.postedDate = postedDate;
	}

	public int getTweetId() {
		return tweetId;
	}

	public void setTweetId(int tweetId) {
		this.tweetId = tweetId;
	}


	
		
	
}
