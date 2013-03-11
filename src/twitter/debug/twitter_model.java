/*     
 *    
 *  Author     : ACIMS(Arizona Centre for Integrative Modeling & Simulation)
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */

package twitter.debug;

import java.util.ArrayList;
import java.util.List;

import model.modeling.content;
import model.modeling.message;

import org.json.JSONObject;

import twitter.graphs.stylized.StylizedGraph;
import twitter.graphs.stylized.WattsStrogatz;
import twitter.types.Hashtag;
import twitter.types.InputEntity;
import twitter.types.User;
import view.modeling.ViewableAtomic;
import GenCol.doubleEnt;
import GenCol.entity;

public class twitter_model extends ViewableAtomic{
	int count[];
	double step_time;
	public static final int NUM_USERS = 3;
	public static final int NUM_FRIENDS = 1;
	public static final int NUM_HASHTAGS = 2;
	public static final double rewireProbability = 0.8;
	
	private List<User> userList;
	private List<Hashtag> hashtagList;
	private int nextTweetID;

	public void init_network(){
		userList = new ArrayList<User>();
		hashtagList = new ArrayList<Hashtag>();
		nextTweetID = 0;
		
		for(int i = 0; i < NUM_HASHTAGS; i++){
			hashtagList.add(new Hashtag(i, "#" + i, "#" + i));
		}
		
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

	public void AddTestTweetValue(String input){
		addTestInput("Tweet",new entity(input));
	}
	
	public void AddTestRetweetValue(double input){
		addTestInput("Retweet",new doubleEnt(input));
	}

	public twitter_model(){
		super("counter");
		step_time = 1 ;
		addInport("Tweet");
		addInport("Retweet");
		addOutport("out");
		InputEntity ip1 = new InputEntity(0,0);
		InputEntity ip2 = new InputEntity(0,1);
		InputEntity ip3 = new InputEntity(1,0);
		InputEntity ip4 = new InputEntity(1,1);
		InputEntity ip5 = new InputEntity(2,0);
		InputEntity ip6 = new InputEntity(2,1);
		
		AddTestTweetValue(ip1.toString());
		AddTestTweetValue(ip2.toString());
		AddTestTweetValue(ip3.toString());
		AddTestTweetValue(ip4.toString());
		AddTestTweetValue(ip5.toString());
		AddTestTweetValue(ip6.toString());
		
		AddTestRetweetValue(0);
		AddTestRetweetValue(1);
		AddTestRetweetValue(2);
		
		AddTestRetweetValue(10);
		
	}

	public void initialize(){
		phase = "passive";
		sigma = INFINITY;
		holdIn(phase, sigma);
		
		count = new int[NUM_HASHTAGS];
		for(int i=0; i< NUM_HASHTAGS; i++)
			count[i]  = 0;
		
		// initialize network
		init_network();
		
		super.initialize();
	}

	public void  deltext(double e,message x){
		Continue(e);
		System.out.println("EXT tttt " + x.getLength());
		//content c0 = new content("in",new doubleEnt(0.0));

		for (int i=0; i< x.getLength();i++){
			if (messageOnPort(x,"Tweet",i))
			{
				System.out.print("EXT tweet");
				entity ent =  x.getValOnPort("Tweet",i);
				JSONObject o = new JSONObject(ent.toString());
				int userID = o.getInt("id");
				int hashtagID = o.getInt("hashtag");

				count[hashtagID]++;
				
				User tmp = userList.get(userID);
				List<Hashtag> tagList = new ArrayList<Hashtag>();
				tagList.add(hashtagList.get(hashtagID));
				tmp.tweet(nextTweetID++, 0, tagList);
				
				holdIn("active", step_time);	
			}
			else if (messageOnPort(x,"Retweet",i)){
				doubleEnt ent = (doubleEnt) x.getValOnPort("Retweet",i);
//				JSONObject o = new JSONObject(ent.toString());
//				int userID = o.getInt("id");
				
				int userID = (int) ent.getv();
				if(userID != 10){
					User tmp = userList.get(userID);
					tmp.retweet(nextTweetID++, 0);
					holdIn("active", step_time);
				} else {
					holdIn("respond", 0);
				}
				
			}
		}
			
	}

	public void  deltint( ){
		phase = "passive";
		sigma = INFINITY;
		holdIn(phase, sigma);
	}

	public message out(){
		message m = new message( );
		if (phaseIs("respond")){
			System.out.println("hashtag 1: -->" + count[0]);
			System.out.println("hashtag 2: -->" + count[1]);
			showState();
			m.add(makeContent("out", new entity(Integer.toString(count[0]))));
		}
		return m;
	}

	public void showState(){
		super.showState();
		System.out.println("State: " + phase + "," + sigma + ",");
	}
}

