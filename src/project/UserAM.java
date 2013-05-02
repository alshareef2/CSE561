package project;

import java.util.List;

import project.entities.TweetCommandEntity;
import project.entities.TweetCommandEntityList;
import project.entities.TweetCommandType;
import project.entities.TweetEntity;
import GenCol.entity;
import model.modeling.content;
import model.modeling.message;
import twitter.types.User;
import view.modeling.ViewableAtomic;

public class UserAM extends ViewableAtomic{
	//String pName;
	private String PASSIVE = "passive";
	private String STATE_TWEET = "tweeting";
	private String STATE_RETWEET = "Retweeting";
	private String STATE_DO_NOTHING = "Do_nothing";
	private String watchedHashtag = "#a";
	private int uid;

	public UserAM(){
		super("User");
		addInport("tweet");
		addInport("timeline");
		addOutport("followers");
	}

	public UserAM(String name, int uid){
		super(name);
		this.uid = uid;
		//this.pName = name;
		addInport("tweet");
		addInport("timeline");
		addOutport("followers");
	}

	public void initialize(){
		holdIn(PASSIVE, INFINITY);
	}

	private void processTweetCommand(TweetCommandEntity command){
		User actionUser = command.getUser();
		//do the thing that is commanded

		if(actionUser.getUserID() != uid){
			//holdIn(STATE_DO_NOTHING, 0);
		} else {
			TweetCommandType type = command.getCommandType();

			switch(type){
			case RETWEET: 
				holdIn(STATE_RETWEET, 0); 
				break;
			case TWEET:
				holdIn(STATE_TWEET, 0);
				break;
			case DONOTHING:
				holdIn(STATE_DO_NOTHING, 0);
				break;
			default:
				break;
			}
		}

	}


	public void  deltext(double e,message x){
		Continue(e);

		for (int i=0; i< x.getLength();i++){
			if(messageOnPort(x, "tweet", i)){
				try{
					TweetCommandEntityList tmp = (TweetCommandEntityList)x.getValOnPort("tweet", i);

					List<TweetCommandEntity> commands = tmp.getEntities();
					for(TweetCommandEntity tce : commands){
						processTweetCommand(tce); 
					}
				}
				catch(ClassCastException cce){
					System.out.println("Improper message on " + "tweet");
				}
			}
		}
	}

	public void deltint(){
		holdIn(PASSIVE, INFINITY);
	}


	public message out(){
		message m = new message( );
		
		if (phaseIs(STATE_TWEET)){
			content c = makeContent("followers", new TweetEntity("Tweet!"));
			m.add(c);
			System.out.println("MESSAGE SENT TO FOLLOWER!!!!!,, TWEET");
			//m.add(makeContent("followers", new TweetEntity("Tweet!")));
		} else if (phaseIs(STATE_RETWEET)){
			content c = makeContent("followers", new TweetEntity("Retweet!"));
			m.add(c);
			//m.add(makeContent("followers", new TweetEntity("Retweet!")));
			System.out.println("MESSAGE SENT TO FOLLOWER!!!!!,, RE_TWEET");
		}

		return m;
	}

}
