package selections;

import java.util.List;

public interface ItemSelector<T> {
	
	public List<T> getItems();
	public void setItems(List<T> items);
	public T getNextItem();
}
