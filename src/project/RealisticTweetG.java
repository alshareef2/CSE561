package project;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import project.util.ZipfGenerator;

import model.modeling.content;
import model.modeling.message;

import project.entities.*;
import twitter.graphs.stylized.WattsStrogatz;
import twitter.types.Hashtag;
import twitter.types.User;
import view.modeling.ViewableAtomic;

// this comment indicates that you see my changes.
public class RealisticTweetG extends ViewableAtomic {
  //states
  private static final String STATE_GENERATINGSETTINGS = "GenSettings";
  private static final String STATE_PRODUCING_TWEET_CMDS = "SendTweets";

  //random things
  private static final int NUM_USERS = 1000;
  private static final int NUM_FRIENDS = 130;
  
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
  private TwitterInitEntity sentOutTIE;
  
  public RealisticTweetG(){
    super("Real Tweet Gen");
    
    addOutport(OUT_SETTINGS);
    addOutport(OUT_TWTCMD);

    rng = new Random();
    zg = new ZipfGenerator(NUM_USERS, 1.);
  }
  
  public void initialize(){
    System.out.println("HERE 5");
    holdIn(STATE_GENERATINGSETTINGS, 0);
  }
  
  public message out(){
    System.out.println("HERE 6");
    message m = new message();
    content c = null;
    double averageTweetsPerSecond = 2.0;
    double standardDevTweetsPerSecond = 1.5;

    
    if(phaseIs(STATE_GENERATINGSETTINGS)){
      TwitterInitEntity tie = new TwitterInitEntity();
      tie.setNetwork(new WattsStrogatz(NUM_USERS, NUM_FRIENDS, .6));

      //set up the hashtags
      List<Hashtag> hashtags = new ArrayList<Hashtag>();
      hashtags.add(new Hashtag(0, "#a", "1"));
      hashtags.add(new Hashtag(1, "#b", "2"));
      hashtags.add(new Hashtag(2, "#c", "3"));
      tie.setHashtags(hashtags);
      
      //set up the users
      users = new ArrayList<User>();
      for(int i = 0; i < NUM_USERS; i++){
        users.add(new User(i));
      }
      tie.setUsers(users);
      
      tie.setTimeToAction(tweetTimeInterval);
      this.sentOutTIE = tie;
      
      c = makeContent(OUT_SETTINGS, tie);
    }
    else if(phaseIs(STATE_PRODUCING_TWEET_CMDS)){

      int numTweets = (int) ((rng.nextDouble() + averageTweetsPerSecond) * standardDevTweetsPerSecond);

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
  
  public void deltint(){
    holdIn(STATE_PRODUCING_TWEET_CMDS, ourSendtimeInterval);
  }
  
}
