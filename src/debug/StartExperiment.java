package debug;

import graphs.stylized.StylizedGraph;
import graphs.stylized.WattsStrogatz;

import java.util.ArrayList;
import java.util.List;

import types.User;

public class StartExperiment {
	
	public static final int NUM_USERS = 100;
	public static final int NUM_FRIENDS = 10;
	public static final double rewireProbability = 0.2;
	
	public static void main(String[] args){
		
		List<User> userList = new ArrayList<User>();
		
		//Create the network
		StylizedGraph graph = new WattsStrogatz(NUM_USERS, NUM_USERS, rewireProbability);
		
		//Create the users
		User tmp;
		for(int i = 0; i < NUM_USERS; i++){
			tmp = new User(i);
			userList.add(new User(i));
		}
		//populate the network info for the user objects
		for(int i = 0; i < NUM_USERS; i++){
			tmp = new User(i);
			List<Integer> userConnection = graph.getUsersFriends(i);
			List<User> tmpList = new ArrayList<User>();
			for(int j : userConnection){
				tmpList.add(userList.get(j));
			}
			tmp.setFollowing(tmpList);
			
			userConnection = graph.getUsersFollowers(i);
			tmpList = new ArrayList<User>();
			for(int j : userConnection){
				tmpList.add(userList.get(j));
			}
			tmp.setFollowers(tmpList);
		}
		
	}
	
}
