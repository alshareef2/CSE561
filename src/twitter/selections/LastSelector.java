package twitter.selections;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LastSelector<T> implements ItemSelector<T> {

  private List<T> items;
  private Random random;
  
  public LastSelector(){
    this(new ArrayList<T>());
  }
  
  public LastSelector(List<T> items){
    this.items = items;
    random = new Random();
  }
  
  @Override
  public List<T> getItems() {
    return items;
  }

  @Override
  public void setItems(List<T> items) {
    this.items = items;
  }

  @Override
  public T getNextItem() {
    if(items.size() == 0){
      return null;
    }
    else{
      return items.get(0);
    }
  }
}
