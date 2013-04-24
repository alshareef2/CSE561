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

  
  //input ports
  public static final String IN_CONFIG = "config";
  public static final String IN_TWEETCOMMAND = "tweetCommand";
  public static final String IN_RETURNSTATSNOW = "forceStats";
  public static final String IN_EXTREMETOPIC = "extremeTopic";
  
  //output ports
  public static final String OUT_TWEET = "tweet";
  
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

  }
  
  public void initialize(){
    phase = STATE_NOTHING;
    sigma = INFINITY;

    tweetsProduced = new ArrayList<Tweet>();
    nextTweetID = 0;
    twitterTime = 0;
    probEvolve = 0.01;
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
        List<User> friends = actionUser.getFollowing();
        userToRetweet = friends.get(rng.nextInt(friends.size()));
      }
      Tweet retweetedTweet = actionUser.retweet(nextTweetID++, twitterTime);
      if(retweetedTweet != null){
        tweetsProduced.add(retweetedTweet); 
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
      Tweet tweetedTweet = actionUser.tweet(nextTweetID++, twitterTime, tagsToTweet);
      System.out.println("I am Tweeting: " + tweetedTweet);
      tweetsProduced.add(tweetedTweet);
      //if one of the tags is in the the extreme topic, tweet twice.
      if(extremeTopic != null && extremeTopic.getDuration() > 0.0){
        System.out.println("EXTREME TWEET!!!!");
        boolean shouldTweetAgain = false;
        for(Hashtag tag : tagsToTweet){
          if(tag.getTopic().equals(extremeTopic.getTopic())){
            shouldTweetAgain = true;
          }
        }
        if(shouldTweetAgain){
          Tweet anotherTweet = new Tweet(nextTweetID++);
          tweetedTweet.logicalCopy(anotherTweet);
          tweetsProduced.add(tweetedTweet);
        }
      }
      uniqueUsers.add("" + actionUser.getUserID());
      
      break;
    case DONOTHING:
    default:
      break;
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

    if(extremeTopic != null){
      extremeTopic.elapse(e);
      if(extremeTopic.getDuration() <= 0.0){
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
          List<TweetCommandEntity> commands = tmp.getEntities();
          for(TweetCommandEntity tce : commands){
            processTweetCommand(tce); 
          }
          
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
    content c = makeContent(OUT_TWEET, new HashtagTweetLists(tagsTweeted, tweetsTweeted, forcedReturnPhase, uniqueUsers.size()));
    m.add(c);
    tweetsProduced.clear();
    if(!forcedReturnPhase){
      uniqueUsers.clear();  
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
    }
  }
  
}
