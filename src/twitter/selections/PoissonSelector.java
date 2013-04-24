package twitter.selections;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PoissonSelector<T> implements ItemSelector<T> {

  private List<T> items;
  private Random random;
  
  public PoissonSelector(){
    this(new ArrayList<T>());
  }
  
  public PoissonSelector(List<T> items){
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
      // int idx = random.nextInt(items.size());
      int idx = getPoisson(1.0);
      if(idx >= items.size()){
        idx = items.size() - 1;
      }
      System.out.println("POISSON SELECTED " + idx);
      return items.get(idx);
    }
  }

  private static int getPoisson(double lambda) {
  double L = Math.exp(-lambda);
  double p = 1.0;
  int k = 0;

  do {
    k++;
    p *= Math.random();
  } while (p > L);

  return k - 1;
}
}
