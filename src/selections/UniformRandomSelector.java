package selections;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UniformRandomSelector<T> implements ItemSelector<T> {

	private List<T> items;
	private Random random;
	
	public UniformRandomSelector(){
		this(new ArrayList<T>());
	}
	
	public UniformRandomSelector(List<T> items){
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
		int idx = random.nextInt(items.size());
		return items.get(idx);
	}
}
