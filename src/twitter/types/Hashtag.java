package twitter.types;
import java.util.LinkedList;
import java.util.List;

// small change 2
public class Hashtag {

	private int hashtagID;
	private String text;
	private String topic;
	private List<Hashtag> prev = new LinkedList<Hashtag>();
	private List<Hashtag> next = new LinkedList<Hashtag>();
	private List<Tweet> tweets = new LinkedList<Tweet>();
	
	/**
	 * @param hashtagID
	 * @param text
	 * @param topic
	 */
	public Hashtag(int hashtagID, String text, String topic) {
		super();
		this.hashtagID = hashtagID;
		this.text = text;
		this.topic = topic;
		prev = new LinkedList<Hashtag>();
		next = new LinkedList<Hashtag>();
		tweets = new LinkedList<Tweet>();
	}
	
	/**
	 * @return the hashtagID
	 */
	public int getHashtagID() {
		return hashtagID;
	}
	
	
	/**
	 * @param hashtagID the hashtagID to set
	 */
	public void setHashtagID(int hashtagID) {
		this.hashtagID = hashtagID;
	}
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * @return the topic
	 */
	public String getTopic() {
		return topic;
	}
	
	/**
	 * @param topic the topic to set
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}

	/**
	 * @return the prev
	 */
	public List<Hashtag> getPrev() {
		return prev;
	}

	/**
	 * @param prev the prev to set
	 */
	public void setPrev(List<Hashtag> prev) {
		this.prev = prev;
	}

	public void addToPrev(Hashtag newTag){
		prev.add(newTag);
	}

	/**
	 * @return the next
	 */
	public List<Hashtag> getNext() {
		return next;
	}

	/**
	 * @param next the next to set
	 */
	public void setNext(List<Hashtag> next) {
		this.next = next;
	}

	public void addToNext(Hashtag newTag){
		next.add(newTag);
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

	public String toString(){
		return text;
	}
	
	
}
