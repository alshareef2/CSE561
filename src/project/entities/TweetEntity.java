package project.entities;

import twitter.types.Tweet;
import GenCol.entity;

public class TweetEntity extends entity{

	private Tweet tweet;
	
	public TweetEntity(String name, Tweet tweet){
		super(name);
		this.tweet = tweet;
	}

	public void setTweet(Tweet tweet){
		this.tweet = tweet;
	}
	
	public Tweet getTweet(){
		return tweet;
	}


}