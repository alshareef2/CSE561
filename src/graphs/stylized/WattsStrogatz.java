package graphs.stylized;

import java.util.ArrayList;
import java.util.List;

import Jama.Matrix;

public class WattsStrogatz implements StylizedGraph{
	private Matrix m;
	
	public WattsStrogatz(int numUsers, int numFriends, double pRewire){
		m = new Matrix(numUsers, numUsers);
		//create the initial lattice
		for(int i = 0; i < numUsers; i++){
			int neg = -1, ct = 1;
			for(int j = 0; j < numFriends; j++){
				int idx = ((int)Math.ceil(ct/2.0)) * neg + i;
				if(idx < 0){
					idx = numUsers + idx;
				}
				else{
					idx %= numUsers;
				}
				
				m.set(i, idx, 1.);
				ct++;
				neg *= -1;
			}
		}
		//randomly rewire
		for(int i = 0; i < numUsers; i++){
			for(int j = 0; j < numUsers; j++){
				//if there is a friendship and we should rewire, do so. 
				if(m.get(i, j) > 0.0){
					if(Math.random() <= pRewire){
						System.out.println(i + ", " + j);
						//select a new node
						int newIdx, attempt = 0;
						do{
							newIdx = (int)(numFriends * Math.random());
						}while(attempt++ < 10 && (newIdx == i || m.get(i, newIdx) > 0));
						
						if(attempt < 10){
							System.out.println("Rewiring (" + i + ", " + j + ") to " + newIdx);
							m.set(i, j, 0.0);
							m.set(i, newIdx, 1.);
						}
					}
				}
			}
		}
	}
	
	public List<Integer> getUsersFriends(int userID){
		List<Integer> friends = new ArrayList<Integer>();
		for(int j = 0; j < m.getColumnDimension(); j++){
			if(m.get(userID, j) > 0){
				friends.add(j);
			}
		}
		return friends;
	}
	
	public List<Integer> getUsersFollowers(int userID){
		List<Integer> friends = new ArrayList<Integer>();
		for(int j = 0; j < m.getColumnDimension(); j++){
			if(m.get(j, userID) > 0){
				friends.add(j);
			}
		}
		return friends;
	}
	
	public static void main(String[] args){
		System.out.println("Making network.");
		WattsStrogatz x = new WattsStrogatz(100, 10, 0.1);
		System.out.println("Network created");
		System.out.println(x.getUsersFriends(99));
		System.out.println(x.getUsersFollowers(99));
	}
}
