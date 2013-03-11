package twitter.types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import twitter.selections.ItemSelector;
import twitter.selections.UniformRandomSelector;

public class User {

	private int userID;
	private List<User> followers = new LinkedList<User>();
	private List<User> following = new LinkedList<User>();
	private List<Tweet> tweets = new LinkedList<Tweet>();
	
	/**
	 * @param userID
	 */
	public User(int userID) {
		super();
		this.userID = userID;
	}

	/**
	 * @return the userID
	 */
	public int getUserID() {
		return userID;
	}

	/**
	 * @param userID the userID to set
	 */
	public void setUserID(int userID) {
		this.userID = userID;
	}
	
	/**
	 * @return the followers
	 */
	public List<User> getFollowers() {
		return followers;
	}

	/**
	 * @param followers the followers to set
	 */
	public void setFollowers(List<User> followers) {
		this.followers = followers;
	}

	/**
	 * @return the following
	 */
	public List<User> getFollowing() {
		return following;
	}

	/**
	 * @param following the following to set
	 */
	public void setFollowing(List<User> following) {
		this.following = following;
	}

	/**
	 * @return the tweets
	 */
	public List<Tweet> getTweets() {
		return tweets;
	}
	
	private List<Tweet> createTimeline(){
		List<Tweet> timeline = new ArrayList<Tweet>();
		for(User i : getFollowing()){
			for(Tweet t: i.getTweets()){
				timeline.add(t);
			}
		}
		
		Collections.sort(timeline);
		Collections.reverse(timeline);
		
		return timeline;
	}

	/**
	 * @param tweets the tweets to set
	 */
	public void setTweets(List<Tweet> tweets) {
		this.tweets = tweets;
	}

	public void retweet(int tweetID, long tweetTime){
		//select the tweet to retweet
		List<Tweet> timeline = createTimeline();
		
		ItemSelector<Tweet> findNextTweet = new UniformRandomSelector<Tweet>(timeline); 
		Tweet toRetweet = findNextTweet.getNextItem();
		//perform the retweet
		if(toRetweet != null){
			toRetweet.incrementNumberOfRT();
			tweets.add(toRetweet);
		}
	}
	
	public void tweet(int tweetID, long tweetTime, List<Hashtag> hashtags){
		Tweet newTweet = new Tweet(tweetID);
		newTweet.setHashtags(hashtags);
		newTweet.setTime(tweetTime);
		newTweet.setUserID(this.userID);
		
		tweets.add(newTweet);
	}
}
