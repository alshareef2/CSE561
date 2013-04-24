package project;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import util.ZipfGenerator;

import GenCol.entity;

import model.modeling.content;
import model.modeling.message;

import project.entities.*;
import twitter.graphs.stylized.*;
import twitter.types.Hashtag;
import twitter.types.User;
import view.modeling.ViewableAtomic;

// this comment indicates that you see my changes.
public class RealisticTweetG extends ViewableAtomic {
  //states
  private static final String STATE_GENERATINGSETTINGS = "GenSettings";
  private static final String STATE_PRODUCING_TWEET_CMDS = "SendTweets";

  //random things
  private static int NUM_USERS = 10000;
  private static int NUM_FRIENDS = 130;
  public static double averageTweetsPerSecond = 2.0;
  public static double standardDevTweetsPerSecond = 1.5;
  
  //input ports
  public static final String IN_START = "Start Exp.";

  //output ports
  public static final String OUT_SETTINGS = "Settings";
  public static final String OUT_TWTCMD = "Tweet Commands";

  //random number generator
  private Random rng;
  private ZipfGenerator zg;
  
  //network stuff
  private List<User> users;

  //state
  private static final double tweetTimeInterval = 60.0;
  private static final double ourSendtimeInterval = 1.;
  private long timeUntilDeath;
  private TwitterInitEntity sentOutTIE;
  
  public RealisticTweetG(){
    super("Real Tweet Gen");
    
    addOutport(OUT_SETTINGS);
    addOutport(OUT_TWTCMD);

    addInport(IN_START);
    addTestInput(IN_START, new entity("Start"));

    rng = new Random();
    zg = new ZipfGenerator(NUM_USERS, 1.);
  }
  
  public void initialize(){
    passivate();
  }
  
  public message out(){
    message m = new message();
    content c = null;

    
    if(phaseIs(STATE_GENERATINGSETTINGS)){
      TwitterInitEntity tie = new TwitterInitEntity();
      System.out.println("Generating Settings with: (" + NUM_USERS + ", " + NUM_FRIENDS + ")");
      StylizedGraph net = new WattsStrogatz(NUM_USERS, NUM_FRIENDS, .6);
      //Abdul, I got rid of this. I hope it fixes your error!! - Fred

      //set up the hashtags
      List<Hashtag> hashtags = new ArrayList<Hashtag>();
      hashtags.add(new Hashtag(0, "#a", "1"));
      hashtags.add(new Hashtag(1, "#b", "2"));
      hashtags.add(new Hashtag(2, "#c", "3"));
      tie.setHashtags(hashtags);
      
      //set up the users
      users = new ArrayList<User>();
      for(int i = 0; i < NUM_USERS; i++){
        User tmp = new User(i);
        users.add(tmp);
      }

      //add friend/follower information
      for(int i = 0; i < NUM_USERS; i++){
        User tmp = users.get(i);
        List<User> followers = new ArrayList<User>();
        List<User> friends = new ArrayList<User>();
        for(int j : net.getUsersFollowers(i)){
          followers.add(users.get(j));
        }
        for(int j : net.getUsersFriends(i)){
          friends.add(users.get(j));
        }
        tmp.setFollowing(friends);
        tmp.setFollowers(followers);
      }

      tie.setUsers(users);
      
      tie.setTimeToAction(tweetTimeInterval);
      this.sentOutTIE = tie;
      
      c = makeContent(OUT_SETTINGS, tie);
    }
    else if(phaseIs(STATE_PRODUCING_TWEET_CMDS)){
      int x = 0;
      int numTweets = (int) ((rng.nextGaussian()  * standardDevTweetsPerSecond) + averageTweetsPerSecond);
      TweetCommandEntityList tcel = new TweetCommandEntityList();
      for(int i = 0; i < numTweets; i++){
        TweetCommandEntity tce = new TweetCommandEntity();
        //now, randomly select a user
        User u = users.get(zg.next());
        tce.setUser(u);

        double actionType = rng.nextDouble();
        if(actionType < u.getpTweet()){
          tce.setCommandType(TweetCommandType.TWEET);
        }
        else if(actionType < u.getpTweet() + u.getpRetweet()){
          tce.setCommandType(TweetCommandType.RETWEET);
        }
        else{
          tce.setCommandType(TweetCommandType.DONOTHING);
        }
        tce.setTagsToTweet(null);
        tcel.addEntity(tce);
      }
      System.out.println("Generating " + tcel.getEntities().size() + " tweets.");
      c = makeContent(OUT_TWTCMD, tcel);
    }
    
    m.add(c);
    return m;
  }
  
  public void deltext(double e, message x){
    Continue(e);

    for(int i = 0; i < x.getLength(); i++){
      if(messageOnPort(x, IN_START, i)){
        try{
          StartExperiment se = (StartExperiment) x.getValOnPort(IN_START, i);
          NUM_USERS = se.getNumUsers();
          NUM_FRIENDS = se.getNumFriends();
          averageTweetsPerSecond = se.getAvgTweetsPerTimeUnit();
          standardDevTweetsPerSecond = se.getStdTweetsPerTimeUnit();
          zg = new ZipfGenerator(NUM_USERS, 1.);
          this.timeUntilDeath = se.getExperimentLife();

          holdIn(STATE_GENERATINGSETTINGS, 0);
        }
        catch(ClassCastException cce){
          System.out.println("Improper message on " + IN_START);
        }
      }
    }
  }

  public void deltint(){
    timeUntilDeath -= ourSendtimeInterval;
    if(timeUntilDeath >= 0){
      holdIn(STATE_PRODUCING_TWEET_CMDS, ourSendtimeInterval);  
    }
    else{
      passivate();
    }
  }
  
}
