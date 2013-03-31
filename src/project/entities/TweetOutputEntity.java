// tweet output entity
package project.entities;

import java.util.List;

import twitter.types.Tweet;

import GenCol.entity;

public class TweetOutputEntity extends entity{

  List<Tweet> tweets;
  
  public TweetOutputEntity(){}
  
  public TweetOutputEntity(List<Tweet> tweets){
    this.tweets = tweets;
  }

  public List<Tweet> getTweets() {
    return tweets;
  }

  public void setTweets(List<Tweet> tweets) {
    this.tweets = tweets;
  }
  
}
