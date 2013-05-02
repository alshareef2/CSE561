package project;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

import project.entities.ExtremeTopicCommand;

import model.modeling.content;
import model.modeling.message;
import project.entities.*;
import twitter.graphs.stylized.StylizedGraph;
import twitter.types.Hashtag;
import twitter.types.Tweet;
import twitter.types.User;
import view.modeling.ViewableAtomic;

// asdf

public class TweetCreator extends ViewableAtomic{

	// states
	public static final String STATE_NOTHING = "Rec_Network";
	public static final String STATE_TIMETOTWEET = "Tweeting";
	public static final String STATE_INTERCEPTED = "Intercepted";
	public static final String STATE_RETURNSTATS = "forceReturnStats";
	public static final String STATE_SEND_CMDS = "sendUserCommands";

	//instance data
	private List<User> users;
	private List<Hashtag> tagsInPlay;
	private List<Tweet> tweetsProduced;
	private double howOftenToTweet;

	private ExtremeTopicCommand extremeTopic;

	private Random rng;
	private long nextTweetID;
	private long twitterTime;
	private double timeLeft;
	private double probEvolve;
	private Set<String> uniqueUsers;
	private String hashtagToWatch;


	//input ports
	public static final String IN_CONFIG = "config";
	public static final String IN_TWEETCOMMAND = "tweetCommand";
	public static final String IN_RETURNSTATSNOW = "forceStats";
	public static final String IN_EXTREMETOPIC = "extremeTopic";

	//output ports
	public static final String OUT_TWEET = "tweet";
	public static final String OUT_TWEET_COM = "tweet_com";

	// for users
	TweetCommandEntityList users_cmds;
	double tmp_sigma;
	boolean users_model = false;
	
	public TweetCreator(){
		this("TweetCreator");
	}

	public TweetCreator(String name){
		super(name);

		rng = new Random();
		uniqueUsers = new HashSet<String>();


		//add the ports
		addInport(IN_CONFIG);
		addInport(IN_TWEETCOMMAND);
		addInport(IN_RETURNSTATSNOW);
		addInport(IN_EXTREMETOPIC);
		addOutport(OUT_TWEET);
		addOutport(OUT_TWEET_COM);

	}

	public void initialize(){
		phase = STATE_NOTHING;
		sigma = INFINITY;

		tweetsProduced = new ArrayList<Tweet>();
		nextTweetID = 0;
		twitterTime = 0;
		probEvolve = 0.001;
	}

	private void processTweetCommand(TweetCommandEntity command){
		User actionUser = command.getUser();
		//do the thing that is commanded
		TweetCommandType type = command.getCommandType();
		Tweet tweetedTweet = null;
		switch(type){
		case RETWEET:
			//if the retweet user is not null, then we HAVE to retweet this guy
			User userToRetweet = command.getUserToRetweet();
			if(userToRetweet == null){
				List<User> friends = actionUser.getFollowing();
				userToRetweet = friends.get(rng.nextInt(friends.size()));
			}
			tweetedTweet = actionUser.retweet(nextTweetID++, twitterTime);
			if(tweetedTweet != null){
				tweetsProduced.add(tweetedTweet); 
			}
			uniqueUsers.add("" + actionUser.getUserID());
			break;
		case TWEET:
			List<Hashtag> tagsToTweet;
			if(command.getTagsToTweet() != null){
				tagsToTweet = command.getTagsToTweet();
			}
			else{
				tagsToTweet = getHashtagsToTweet(1);
			}
			tweetedTweet = actionUser.tweet(nextTweetID++, twitterTime, tagsToTweet);
			tweetsProduced.add(tweetedTweet);
			//if one of the tags is in the the extreme topic, tweet twice.
			uniqueUsers.add("" + actionUser.getUserID());

			break;
		case DONOTHING:
		default:
			break;
		}

		if(tweetedTweet != null && extremeTopic != null){
			boolean shouldTweetAgain = false;
			for(Hashtag tag : tweetedTweet.getHashtags()){
				if(tag.getTopic().equals(extremeTopic.getTopic())){
					shouldTweetAgain = true;
				}
			}
			if(shouldTweetAgain){
				System.out.println("RETWEETING EXTREME TWEET!!!!!!");
				Tweet anotherTweet = new Tweet(nextTweetID++);
				anotherTweet = tweetedTweet.logicalCopy(anotherTweet);
				tweetsProduced.add(anotherTweet);
			}
		}

	}

	private List<Hashtag> getHashtagsToTweet(int size){
		List<Hashtag> tagsToTweet = new ArrayList<Hashtag>();
		while(size-- > 0){
			//choose a tag
			Hashtag tag = tagsInPlay.get(rng.nextInt(tagsInPlay.size()));

			//see if we should evolve a new tag
			if(rng.nextDouble() < probEvolve){
				Hashtag newTag = new Hashtag(tagsInPlay.size(), tag.getText() + "PRIME", tag.getTopic());
				tag.addToNext(newTag);
				newTag.addToPrev(tag);

				tagsInPlay.add(newTag);
				tagsToTweet.add(newTag);
			}
			else{
				tagsToTweet.add(tag);  
			}
		}
		return tagsToTweet;
	}

	public void deltext(double e, message x){
		Continue(e);
		twitterTime += e;

		System.out.println("Twitter time: " + twitterTime);

		if(extremeTopic != null){
			extremeTopic.elapse(e);
			if(extremeTopic.getDuration() <= 0.0){
				System.out.println("Extreme topic expired");
				extremeTopic = null;
			}
		}

		//if we get a new network info, then we will change the sate
		for(int i = 0; i < x.getLength(); i++){
			if(messageOnPort(x, IN_CONFIG, i)){
				try{
					TwitterInitEntity tmp = (TwitterInitEntity)x.getValOnPort(IN_CONFIG, i);
					this.howOftenToTweet = tmp.getTimeToAction();
					this.users = tmp.getUsers();
					// generating user Atomic models
					DTM parent;
					if(getParent() instanceof DTM && users.size() <= 5){
						users_model = true;
						parent = (DTM) getParent();
						//parent.addUsers(users);
						for(User user: users){
							UserAM userAM = new UserAM("User_"+user.getUserID(), user.getUserID());
							addModel(userAM);
							//addCoupling(userAM.getName(),"stat",parent.tm.getName(),IN_TWEETCOMMAND);
							addCoupling(parent.tm.getName(),OUT_TWEET_COM,userAM.getName(),"tweet");
							//addOutport(userAM.getName(),"followers");
							for(User usr: user.getFollowers()){
								//addOutport(userAM.getName(),"follower_" + usr.getUserID());
								addCoupling(userAM.getName(),"followers","User_"+usr.getUserID(),"timeline");
							}
							//System.out.println(user.getUserID()+" USERS GENERATED!!");
						}
					}
					// finish generating users AMs
					this.tagsInPlay = tmp.getHashtags();
					holdIn(STATE_TIMETOTWEET, this.howOftenToTweet);
				}
				catch(ClassCastException cce){
					System.out.println("Improper message on " + IN_CONFIG);
				}
			}
		}

		//if we get an extreme topic, then store it
		for(int i = 0; i < x.getLength(); i++){
			if(messageOnPort(x, IN_EXTREMETOPIC, i)){
				try{
					ExtremeTopicCommand tmp = (ExtremeTopicCommand)x.getValOnPort(IN_EXTREMETOPIC, i);
					extremeTopic = tmp;
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
					TweetCommandEntityList tmp = (TweetCommandEntityList)x.getValOnPort(IN_TWEETCOMMAND, i);
					users_cmds = tmp;
					List<TweetCommandEntity> commands = tmp.getEntities();
					for(TweetCommandEntity tce : commands){
						processTweetCommand(tce); 
					}

					if(users_model){
						tmp_sigma = sigma;
						holdIn(STATE_SEND_CMDS, 0);
					} else
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
		if(phaseIs(STATE_SEND_CMDS)){
			content c = makeContent(OUT_TWEET_COM, users_cmds);
			m.add(c);
		} else{
			

			boolean forcedReturnPhase = phaseIs(STATE_RETURNSTATS);

			List<Hashtag> tagsTweeted = new ArrayList<Hashtag>();
			List<Tweet> tweetsTweeted = new ArrayList<Tweet>();
			for(int i = tweetsProduced.size() - 1; i >= 0; i--){
				Tweet t;
				if(forcedReturnPhase){
					t = tweetsProduced.get(i);
				}
				else{
					t = tweetsProduced.remove(i); 
				}

				tweetsTweeted.add(t);
				System.out.println("Sending: " + t);
				if(t != null && t.getHashtags() != null){
					tagsTweeted.addAll(t.getHashtags());
				}
			}
			System.out.println("--- PRINTED " + tagsTweeted.size() + " TAGS ---");
			content c = makeContent(OUT_TWEET, new HashtagTweetLists(tagsTweeted, tweetsTweeted, forcedReturnPhase, uniqueUsers.size()));
			m.add(c);
			tweetsProduced.clear();
			if(!forcedReturnPhase){
				uniqueUsers.clear();  
			}
		}
		
		return m;
	}

	public void deltint(){
		if(phaseIs(STATE_TIMETOTWEET)){
			// do the option for every user
			double actionRoll;
			for(User u : users){
				actionRoll = rng.nextDouble();
				if(actionRoll <= u.getpTweet()){
					List<Hashtag> tagsToTweet = getHashtagsToTweet(1);
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
		} else if(phaseIs(STATE_SEND_CMDS)){
			holdIn(STATE_INTERCEPTED, tmp_sigma);
		}
	}

}
