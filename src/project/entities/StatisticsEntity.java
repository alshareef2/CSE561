package project.entities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import twitter.types.Hashtag;
import twitter.types.Tweet;
import GenCol.entity;

import util.Matrix;

public class StatisticsEntity extends entity{
	private String processedBy;
	private Hashtag top_tweeted;
	private Tweet top_retweeted;
	private Matrix coe_matrix;
	private double entropy;
	private int numOfUsers;
	private Map<Hashtag, Integer> hashtags = new HashMap<Hashtag, Integer>();
	
	public StatisticsEntity(){
		super("STATS!");
	}

	public void setProcessedBy(String processedBy){
		this.processedBy = processedBy;
	}
	
	public void setNumOfusers(int numOfUsers){
		this.numOfUsers = numOfUsers;
	}
	
	public int getNumOfusers(){
		return numOfUsers;
	}
	
	public String getProcessedBy(){
		return processedBy;
	}
	
	public void setCOEMatrix(Matrix matrix){
		this.coe_matrix = matrix;
	}
	
	public Matrix getCOEMatrix(){
		return coe_matrix;
	}
	
	public void setEntropy(double entropy){
		this.entropy = entropy;
	}
	
	public double getEntropy(){
		return entropy;
	}
	
	/**
	 * @return the top_tweeted
	 */
	public Hashtag getTop_tweeted() {
		return top_tweeted;
	}

	/**
	 * @param top_h the top_tweeted to set
	 */
	public void setTop_tweeted(Hashtag top_h) {
		this.top_tweeted = top_h;
	}

	/**
	 * @return the top_retweeted
	 */
	public Tweet getTop_retweeted() {
		return top_retweeted;
	}

	/**
	 * @param top_retweeted the top_retweeted to set
	 */
	public void setTop_retweeted(Tweet top_retweeted) {
		this.top_retweeted = top_retweeted;
	}

	/**
	 * @return the tweets
	 */
	public Map<Hashtag, Integer> getHashtags() {
		return hashtags;
	}

	/**
	 * @param tweets the tweets to set
	 */
	public void setHashtags(Map<Hashtag, Integer> hashtags) {
		this.hashtags = hashtags;
	}
	
	
}
