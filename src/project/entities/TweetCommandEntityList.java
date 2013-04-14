package project.entities;

import project.entities.TweetCommandEntity;

import java.util.List;
import java.util.ArrayList;

import GenCol.entity;

public class TweetCommandEntityList extends entity{
  
  private List entities;

  public TweetCommandEntityList(){
    entities = new ArrayList<TweetCommandEntity>();
  }

  public void addEntity(TweetCommandEntity tce){
    entities.add(tce);
  }

  public List<TweetCommandEntity> getEntities(){
    return entities;
  }
}