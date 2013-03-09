package graphs.stylized;

import java.util.List;

public interface StylizedGraph {
	public List<Integer> getUsersFollowers(int userID);
	public List<Integer> getUsersFriends(int userID);
}
