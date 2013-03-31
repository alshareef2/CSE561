package project;

import java.util.ArrayList;
import java.util.List;

import model.modeling.content;
import model.modeling.message;

import project.entities.TweetCommandEntity;
import project.entities.TweetCommandType;
import project.entities.TwitterInitEntity;
import twitter.graphs.stylized.WattsStrogatz;
import twitter.types.Hashtag;
import twitter.types.User;
import view.modeling.ViewableAtomic;

// this comment indicates that you see my changes.
public class TweetG extends ViewableAtomic {
	//states
	private static final String STATE_GENERATINGSETTINGS = "GenSettings";
	private static final String STATE_PRODUCING_TWEET_CMDS = "SendTweets";
	
	//output ports
	public static final String OUT_SETTINGS = "Settings";
	public static final String OUT_TWTCMD = "Tweet Commands";
	
	//state
	private static final double tweetTimeInterval = 60.0;
	private static final double ourSendtimeInterval = 1.;
	private TwitterInitEntity sentOutTIE;
	
	public TweetG(){
		super("Tweet Gen");
		
		addOutport(OUT_SETTINGS);
		addOutport(OUT_TWTCMD);
	}
	
	public void initialize(){
		holdIn(STATE_GENERATINGSETTINGS, 0);
	}
	
	public message out(){
		message m = new message();
		content c = null;
		
		if(phaseIs(STATE_GENERATINGSETTINGS)){
			TwitterInitEntity tie = new TwitterInitEntity();
			tie.setNetwork(new WattsStrogatz(10, 3, .6));

			//set up the hashtags
			List<Hashtag> hashtags = new ArrayList<Hashtag>();
			hashtags.add(new Hashtag(0, "#a", "1"));
			hashtags.add(new Hashtag(1, "#b", "2"));
			hashtags.add(new Hashtag(2, "#c", "3"));
			tie.setHashtags(hashtags);
			
			//set up the users
			List<User> users = new ArrayList<User>();
			for(int i = 0; i < 10; i++){
				users.add(new User(i));
			}
			tie.setUsers(users);
			
			tie.setTimeToAction(tweetTimeInterval);
			this.sentOutTIE = tie;
			
			c = makeContent(OUT_SETTINGS, tie);
		}
		else if(phaseIs(STATE_PRODUCING_TWEET_CMDS)){
			TweetCommandEntity tce = new TweetCommandEntity();
			tce.setCommandType(TweetCommandType.TWEET);
			tce.setTagsToTweet(null);
			tce.setUser(sentOutTIE.getUsers().get(0));
			
			c = makeContent(OUT_TWTCMD, tce);
		}
		
		m.add(c);
		return m;
	}
	
	public void deltint(){
		holdIn(STATE_PRODUCING_TWEET_CMDS, ourSendtimeInterval);
	}
	
}
