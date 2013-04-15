package project.entities;

import GenCol.entity;

public class ExtremeTopicCommand extends entity{

  private String topic;
  private double duration;

  public ExtremeTopicCommand(){
    topic = "";
    duration = 0.0;
  }

  public ExtremeTopicCommand(String topic, double duration){
    this.topic = topic;
    this.duration = duration;
  }

  public String getTopic(){
    return topic;
  }

  public double getDuration(){
    return duration;
  }

  public void elapse(double e){
    duration -= e;
  }

}