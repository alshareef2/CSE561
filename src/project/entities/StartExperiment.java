package project.entities;

import GenCol.entity;

public class StartExperiment extends entity{

  private int numUsers;
  private int numFriends;
  private long experimentLife;
  private boolean extremeExp;
  private double avgTweetsPerTimeUnit;
  private double stdTweetsPerTimeUnit;
  private String hashtagToWatch;

  public StartExperiment(String name, int numUsers, int numFriends, double avgTweetsPerTimeUnit, double stdTweetsPerTimeUnit, String hashtagToWatch, long experimentLife, boolean extremeExp){
    super(name + ", " + experimentLife);
    this.numUsers = numUsers;
    this.numFriends = numFriends;
    this.avgTweetsPerTimeUnit = avgTweetsPerTimeUnit;
    this.stdTweetsPerTimeUnit = stdTweetsPerTimeUnit;
    this.hashtagToWatch = hashtagToWatch;
    this.experimentLife = experimentLife;
    this.extremeExp = extremeExp;
  }

  public boolean getExtremeExp(){
	  return extremeExp;
  }
  
  public void setExtremeExp(boolean extremeExp){
	  this.extremeExp = extremeExp;
  }
  
  public long getExperimentLife(){
    return experimentLife;
  }

  public void setExperimentLife(long experimentLife){
    this.experimentLife = experimentLife;
  }

  public int getNumUsers(){
    return numUsers;
  }

  public int getNumFriends(){
    return numFriends;
  }

  public double getAvgTweetsPerTimeUnit(){
    return avgTweetsPerTimeUnit;
  }

  public double getStdTweetsPerTimeUnit(){
    return stdTweetsPerTimeUnit;
  }

  public String getHashtagToWatch(){
    return hashtagToWatch;
  }

  public void setHashtagToWatch(String hashtagToWatch){
    this.hashtagToWatch = hashtagToWatch;
  }

}