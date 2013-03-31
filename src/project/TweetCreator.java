package project;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.modeling.content;
import model.modeling.message;
import project.entities.TweetCommandEntity;
import project.entities.TweetCommandType;
import project.entities.TwitterInitEntity;
import twitter.graphs.stylized.StylizedGraph;
import twitter.types.Hashtag;
import project.entities.HashtagTweetLists;
import twitter.types.Tweet;
import twitter.types.User;
import view.modeling.ViewableAtomic;

public class TweetCreator extends ViewableAtomic{

	// states
	public static final String STATE_NOTHING = "Rec_Network";
	public static final String STATE_TIMETOTWEET = "Tweeting";
	public static final String STATE_INTERCEPTED = "Intercepted";
	public static final String STATE_RETURNSTATS = "forceReturnStats";
	
	//instance data
	private StylizedGraph network;
	private List<User> users;
	private List<Hashtag> tagsInPlay;
	private List<Tweet> tweetsProduced;
	private double howOftenToTweet;
	
	private Random rng;
	private long nextTweetID;
	private long twitterTime;
	private double timeLeft;
	
	//input ports
	public static final String IN_CONFIG = "config";
	public static final String IN_TWEETCOMMAND = "tweetCommand";
	public static final String IN_RETURNSTATSNOW = "forceStats";
	
	//output ports
	public static final String OUT_TWEET = "tweet";
	
	public TweetCreator(){
		this("TweetCreator");
	}
	
	public TweetCreator(String name){
		
		rng = new Random();
		tweetsProduced = new ArrayList<Tweet>();
		nextTweetID = 0;
		twitterTime = 0;
		
		//add the ports
		addInport(IN_CONFIG);
		addInport(IN_TWEETCOMMAND);
		addOutport(OUT_TWEET);
	}
	
	public void initialize(){
		phase = STATE_NOTHING;
		sigma = INFINITY;
	}
	
	private void processTweetCommand(TweetCommandEntity command){
		User actionUser = command.getUser();
		//do the thing that is commanded
		TweetCommandType type = command.getCommandType();
		switch(type){
		case RETWEET:
			//if the retweet user is not null, then we HAVE to retweet this guy
			User userToRetweet = command.getUserToRetweet();
			if(userToRetweet == null){
				List<Integer> friends = network.getUsersFriends(actionUser.getUserID());
				userToRetweet = users.get(friends.get(rng.nextInt(friends.size())));
			}
			tweetsProduced.add(actionUser.retweet(nextTweetID++, twitterTime));
			break;
		case TWEET:
			List<Hashtag> tagsToTweet;
			if(command.getTagsToTweet() != null){
				tagsToTweet = command.getTagsToTweet();
			}
			else{
				tagsToTweet = getHashtagsToTweet();
			}
			tweetsProduced.add(actionUser.tweet(nextTweetID++, twitterTime, tagsToTweet));
			break;
		case DONOTHING:
		default:
			break;
		}
	}
	
	private List<Hashtag> getHashtagsToTweet(){
		List<Hashtag> tagsToTweet = new ArrayList<Hashtag>();
		tagsToTweet.add(tagsInPlay.get(rng.nextInt(tagsInPlay.size())));
		
		return tagsToTweet;
	}
	
	public void deltext(double e, message x){
		Continue(e);
		
		//if we get a new network info, then we will change the sate
		for(int i = 0; i < x.getLength(); i++){
			if(messageOnPort(x, IN_CONFIG, i)){
				try{
					TwitterInitEntity tmp = (TwitterInitEntity)x.getValOnPort(IN_CONFIG, i);
					this.network = tmp.getNetwork();
					this.howOftenToTweet = tmp.getTimeToAction();
					this.users = tmp.getUsers();
					this.tagsInPlay = tmp.getHashtags();
					holdIn(STATE_TIMETOTWEET, this.howOftenToTweet);
				}
				catch(ClassCastException cce){
					System.out.println("Improper message on " + IN_CONFIG);
				}
			}
		}
		
		//if we get a tweet command
		for(int i = 0; i < x.getLength(); i++){
			if(messageOnPort(x, IN_TWEETCOMMAND, i)){
				try{
					TweetCommandEntity tmp = (TweetCommandEntity)x.getValOnPort(IN_TWEETCOMMAND, i);
					processTweetCommand(tmp);
					holdIn(STATE_INTERCEPTED, sigma);
				}
				catch(ClassCastException cce){
					System.out.println("Improper message on " + IN_TWEETCOMMAND);
				}
			}
		}
		
		//if we get a command to change the stats, then we will do that.
		for(int i = 0; i < x.getLength(); i++){
			if(messageOnPort(x, IN_RETURNSTATSNOW, i)){
				timeLeft = sigma;
				holdIn(STATE_RETURNSTATS, 0.0);
			}
		}
	}
	
	public message out(){
		message m = new message();
		
		List<Hashtag> tagsTweeted = new ArrayList<Hashtag>();
		for(Tweet t : tweetsProduced){
			tagsTweeted.addAll(t.getHashtags());
		}
		
		content c = makeContent(OUT_TWEET, new HashtagTweetLists(tagsTweeted, tweetsProduced));
		m.add(c);
		
		tweetsProduced.clear();
		return m;
	}
	
	public void deltint(){
		if(phaseIs(STATE_TIMETOTWEET)){
			// do the option for every user
			double actionRoll;
			for(User u : users){
				actionRoll = rng.nextDouble();
				if(actionRoll <= u.getpTweet()){
					List<Hashtag> tagsToTweet = getHashtagsToTweet();
					tweetsProduced.add(u.tweet(nextTweetID++, twitterTime, tagsToTweet));
				}
				else if(actionRoll > u.getpTweet() && actionRoll <= u.getpTweet() + u.getpRetweet()){
					tweetsProduced.add(u.retweet(nextTweetID++, twitterTime));
				}
				
			}
		}
		else if(phaseIs(STATE_INTERCEPTED)){
			holdIn(STATE_TIMETOTWEET, this.howOftenToTweet);
		}
		else if(phaseIs(STATE_RETURNSTATS)){
			holdIn(STATE_TIMETOTWEET, timeLeft);
			timeLeft = 0;
		}
	}
	
}
