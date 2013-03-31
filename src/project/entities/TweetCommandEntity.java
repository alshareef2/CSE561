//tweet command entity
package project.entities;

import java.util.List;

import GenCol.entity;
import twitter.types.Hashtag;
import twitter.types.User;

public class TweetCommandEntity extends entity{
  private User user;
  private TweetCommandType commandType;
  private User userToRetweet;
  private List<Hashtag> tagsToTweet;
  
  public List<Hashtag> getTagsToTweet() {
    return tagsToTweet;
  }
  public void setTagsToTweet(List<Hashtag> tagsToTweet) {
    this.tagsToTweet = tagsToTweet;
  }
  public User getUserToRetweet() {
    return userToRetweet;
  }
  public void setUserToRetweet(User userToRetweet) {
    this.userToRetweet = userToRetweet;
  }
  public User getUser() {
    return user;
  }
  public void setUser(User user) {
    this.user = user;
  }
  public TweetCommandType getCommandType() {
    return commandType;
  }
  public void setCommandType(TweetCommandType commandType) {
    this.commandType = commandType;
  }
}
