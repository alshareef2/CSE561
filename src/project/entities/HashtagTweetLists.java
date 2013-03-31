package project.entities;

import java.util.LinkedList;
import java.util.List;

import twitter.types.Hashtag;
import twitter.types.Tweet;

import GenCol.entity;

public class HashtagTweetLists extends entity{

	private List<Hashtag> hashtags = new LinkedList<Hashtag>();
	private List<Tweet> tweets = new LinkedList<Tweet>();
	
	public HashtagTweetLists(){
		
	}
	
	public HashtagTweetLists(List<Hashtag> tags, List<Tweet> tweets){
		hashtags = tags;
		this.tweets = tweets;
	}

	/**
	 * @return the hashtags
	 */
	public List<Hashtag> getHashtags() {
		return hashtags;
	}

	/**
	 * @param hashtags the hashtags to set
	 */
	public void setHashtags(List<Hashtag> hashtags) {
		this.hashtags = hashtags;
	}

	/**
	 * @return the tweets
	 */
	public List<Tweet> getTweets() {
		return tweets;
	}

	/**
	 * @param tweets the tweets to set
	 */
	public void setTweets(List<Tweet> tweets) {
		this.tweets = tweets;
	}
	
	

}
