package project.entities;

import GenCol.entity;

public class StartExperiment extends entity{

  private int numUsers;
  private int numFriends;
  private double avgTweetsPerTimeUnit;
  private double stdTweetsPerTimeUnit;

  public StartExperiment(String name, int numUsers, int numFriends, double avgTweetsPerTimeUnit, double stdTweetsPerTimeUnit){
    super(name);
    this.numUsers = numUsers;
    this.numFriends = numFriends;
    this.avgTweetsPerTimeUnit = avgTweetsPerTimeUnit;
    this.stdTweetsPerTimeUnit = stdTweetsPerTimeUnit;
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

}