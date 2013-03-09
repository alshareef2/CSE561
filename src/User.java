import java.util.LinkedList;
import java.util.List;

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

	/**
	 * @param tweets the tweets to set
	 */
	public void setTweets(List<Tweet> tweets) {
		this.tweets = tweets;
	}

	public void retweet(int tweetID){
		
	}
	
	public void tweet(int tweetID){
		
	}
}
