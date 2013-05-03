package twitter.types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import twitter.selections.*;

/**
* This model contains all of the data for the users. 
*/
public class User implements Comparable<User>{

	private int userID;
	//who follows the user
	private List<User> followers = new LinkedList<User>();
	//who the user follows
	private List<User> following = new LinkedList<User>();
	//tweets produced by the user
	private List<Tweet> tweets = new LinkedList<Tweet>();
	private double pTweet;
	private double pRetweet;
	
	public double getpTweet() {
		return pTweet;
	}

	public void setpTweet(double pTweet) {
		this.pTweet = pTweet;
	}

	public double getpRetweet() {
		return pRetweet;
	}

	public void setpRetweet(double pRetweet) {
		this.pRetweet = pRetweet;
	}

	/**
	 * @param userID
	 */
	public User(int userID) {
		super();
		this.userID = userID;
		pTweet = .2;
		pRetweet = .8;
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
	
	/**
	* This method creates the user's timeline, the list of tweets that the user will see when he logs in to twitter.
	* @return The list of tweets the user sees, from most recent, to least recent.
	*/
	private List<Tweet> createTimeline(){
		List<Tweet> timeline = new ArrayList<Tweet>();
		for(User i : getFollowing()){
			for(Tweet t: i.getTweets()){
				timeline.add(t);
			}
		}
		
		Collections.sort(timeline);

		return timeline;
	}

	/**
	 * @param tweets the tweets to set
	 */
	public void setTweets(List<Tweet> tweets) {
		this.tweets = tweets;
	}

	/**
	* Here, the user chooses a tweet from his timeline and retweets it.
	* @param nextTweetID the ID that the produced tweet will contain (even retweets need a new ID!).
	* @param tweetTime the time that the tweet was retweeted.
	* @return The published tweet.
	*/
	public Tweet retweet(long nextTweetID, long tweetTime){
		//select the tweet to retweet
		List<Tweet> timeline = createTimeline();
		
		ItemSelector<Tweet> findNextTweet = new PoissonSelector<Tweet>(timeline); 
		Tweet toRetweet = findNextTweet.getNextItem();
		Tweet nextTweet = null;

		//perform the retweet
		if(toRetweet != null){
			toRetweet.incrementNumberOfRT();

			nextTweet = new Tweet(nextTweetID);
			nextTweet = toRetweet.logicalCopy(nextTweet);
			nextTweet.setTime(tweetTime);
			nextTweet.setUserID(this.userID);

			tweets.add(nextTweet);
		}
		
		return nextTweet;
	}
	
	/**
	* Here, the user produces a tweet with the given hashtags.
	* @param nextTweetID the ID that the produced tweet will contain.
	* @param tweetTime the time that the tweet was produced.
	* @param hashtags a list of hashtags that the user will retweet
	* @return The published tweet.
	*/
	public Tweet tweet(long nextTweetID, long tweetTime, List<Hashtag> hashtags){
		Tweet newTweet = new Tweet(nextTweetID);
		newTweet.setHashtags(hashtags);
		newTweet.setTime(tweetTime);
		newTweet.setUserID(this.userID);
		
		tweets.add(newTweet);
		return newTweet;
	}

	/**
	* Checks if two users are the same. If the users are the same they will have the same ID.
	* @param otherUser the user to compare to.
	*/
	public int compareTo(User otherUser) {
		return otherUser.userID - userID;
	}
}
