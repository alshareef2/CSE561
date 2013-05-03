package twitter.selections;

import java.util.List;

/**
* This is an item selector. The only thing thta implementors really get to choose is the probability distribution.
*/
public interface ItemSelector<T> {
	/**
  * @return all of the items in the collection
  */
	public List<T> getItems();
  /**
  * @param set of items.<br/>
  * Set all of the items in the collection
  */
	public void setItems(List<T> items);
  /**
  * Choose the next item from the set collection.
  * @return the next item from the list (selected by the subclass).
  */
	public T getNextItem();
}
