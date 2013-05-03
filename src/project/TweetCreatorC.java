package project;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import twitter.types.User;
import view.modeling.ViewableAtomic;
import view.modeling.ViewableComponent;
import view.modeling.ViewableDigraph;

public class TweetCreatorC extends ViewableDigraph {
	public static int PROCS = 3;
	double processing_time = 10;
	ArrayList <UserAM> userList;

	ViewableAtomic tm;

	//input ports
	public static final String IN_CONFIG = "config";
	public static final String IN_TWEETCOMMAND = "tweetCommand";
	public static final String IN_RETURNSTATSNOW = "forceStats";
	public static final String IN_EXTREMETOPIC = "extremeTopic";

	//output ports
	public static final String OUT_TWEET = "tweet";

	public TweetCreatorC(){
		this("Twitter CM");
	}

	public TweetCreatorC(String name) {
		super(name);
		userList = new ArrayList <UserAM> ();
		addOutport("Statistics");

		tm = new TweetCreator();

		tm.setBackgroundColor(Color.pink);

		add(tm);
		tm.setBackgroundColor(Color.pink);

		//add the ports
		addInport(IN_CONFIG);
		addInport(IN_TWEETCOMMAND);
		addInport(IN_RETURNSTATSNOW);
		addInport(IN_EXTREMETOPIC);
		addOutport(OUT_TWEET);

		addCoupling(this, IN_CONFIG, tm, TweetCreator.IN_CONFIG);
		addCoupling(this, IN_TWEETCOMMAND, tm, TweetCreator.IN_TWEETCOMMAND);
		addCoupling(this, IN_RETURNSTATSNOW, tm, TweetCreator.IN_RETURNSTATSNOW);
		addCoupling(this, IN_TWEETCOMMAND, tm, TweetCreator.IN_TWEETCOMMAND);
		addCoupling(tm, OUT_TWEET, this, OUT_TWEET);

	}

	/**
	 * @return the userList
	 */
	public ArrayList<UserAM> getUserList() {
		return userList;
	}

	/**
	 * @param userList the procList to set
	 */
	public void setUserList(ArrayList<UserAM> userList) {
		this.userList = userList;
	}

	public void addUsers(List<User> users){

		for(User user: users){
			UserAM userAM = new UserAM("U_"+user.getUserID(),user.getUserID(), null);
			userList.add(userAM);
			add(userAM);
			System.out.println(user.getUserID()+" USERS GENERATED!!");
		}


		//return user;
	}

    /**
     * Automatically generated by the SimView program.
     * Do not edit this manually, as such changes will get overwritten.
     */
    public void layoutForSimView()
    {
        preferredSize = new Dimension(736, 285);
        ((ViewableComponent)withName("U2")).setPreferredLocation(new Point(50, 50));
        ((ViewableComponent)withName("U1")).setPreferredLocation(new Point(50, 50));
        ((ViewableComponent)withName("TweetCreator")).setPreferredLocation(new Point(50, 50));
        ((ViewableComponent)withName("User_1")).setPreferredLocation(new Point(50, 50));
        ((ViewableComponent)withName("User_3")).setPreferredLocation(new Point(50, 50));
        ((ViewableComponent)withName("U3")).setPreferredLocation(new Point(50, 50));
        ((ViewableComponent)withName("User_4")).setPreferredLocation(new Point(50, 50));
        ((ViewableComponent)withName("U4")).setPreferredLocation(new Point(50, 50));
        ((ViewableComponent)withName("U0")).setPreferredLocation(new Point(50, 50));
        ((ViewableComponent)withName("User_0")).setPreferredLocation(new Point(50, 50));
        ((ViewableComponent)withName("User_2")).setPreferredLocation(new Point(50, 50));
    }
}
