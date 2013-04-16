// twitter init entity
package project.entities;

import java.util.List;

import twitter.graphs.stylized.StylizedGraph;
import twitter.types.Hashtag;
import twitter.types.User;
import GenCol.entity;

public class TwitterInitEntity extends entity {

  private List<User> users;
  private List<Hashtag> hashtags;
  private double timeToAction;
  
  public List<Hashtag> getHashtags() {
    return hashtags;
  }
  public void setHashtags(List<Hashtag> hashtags) {
    this.hashtags = hashtags;
  }
  public double getTimeToAction() {
    return timeToAction;
  }
  public void setTimeToAction(double timeToAction) {
    this.timeToAction = timeToAction;
  }
  public List<User> getUsers() {
    return users;
  }
  public void setUsers(List<User> users) {
    this.users = users;
  }
}
