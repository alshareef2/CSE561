package twitter.types;

public class InputEntity {
	private int id;
	private int hashtag;
	
	
	/**
	 * @param id
	 * @param action
	 */
	public InputEntity(int id, int hashtag) {
		super();
		this.id = id;
		this.hashtag = hashtag;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the action
	 */
	public int getHashtag() {
		return hashtag;
	}
	/**
	 * @param action the action to set
	 */
	public void setHashtag(int action) {
		this.hashtag = action;
	}
	
	public String toString(){
		return "{\"id\": "+ id + ",\"hashtag\": " + hashtag + "}";
	}
}
