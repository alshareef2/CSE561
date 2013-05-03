package project;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import project.entities.TweetCommandEntity;
import project.entities.TweetCommandEntityList;
import project.entities.TweetCommandType;
import project.entities.TweetEntity;
import model.modeling.content;
import model.modeling.message;
import twitter.types.Hashtag;
import twitter.types.Tweet;
import twitter.types.User;
import view.modeling.ViewableAtomic;

public class UserAM extends ViewableAtomic{

	// STATES
	private String PASSIVE = "passive";
	private String STATE_TWEET = "tweeting";
	private String STATE_RETWEET = "Retweeting";
	private String STATE_DO_NOTHING = "Do_nothing";

	// PORTS
	private String TWEET_P = "tweet";
	private String TIMELINE_P = "timeline";
	private String FOLLOWERS_P = "followers";

	// Watched hashtag experiment variables
	public static long first_aware_time = Integer.MAX_VALUE;
	public static long last_aware_time = -1;

	private int uid;
	private List<Hashtag> tagsInPlay;
	private double probEvolve;
	private Random rng;
	private boolean aware; // to indicate user awareness about watched hashtag
	private Tweet tweet;

	public UserAM(String name, int uid, List<Hashtag> tagsInPlay){
		super(name);
		this.uid = uid;
		rng = new Random();
		this.tagsInPlay = tagsInPlay;
		this.aware = false;
		addInport(TWEET_P);
		addInport(TIMELINE_P);
		addOutport(FOLLOWERS_P);
	}

	public boolean getAware(){
		return aware;
	}

	public void initialize(){
		holdIn(PASSIVE, INFINITY);
		probEvolve = 0.001;
	}

	/**
	 * @param tweet command that need to be processed
	 * 
	 */
	private void processTweetCommand(TweetCommandEntity command){
		User actionUser = command.getUser();
		//do the thing that is commanded

		if(actionUser.getUserID() == uid) {
			TweetCommandType type = command.getCommandType();

			switch(type){
			case RETWEET: 
				// TO DO
				holdIn(STATE_RETWEET, 0); 
				break;
			case TWEET:
				List<Hashtag> tagsToTweet;
				if(command.getTagsToTweet() != null){
					tagsToTweet = command.getTagsToTweet();
				}
				else{
					tagsToTweet = getHashtagsToTweet(1);
				}

				for(Hashtag hashtag: tagsToTweet){
					if(hashtag.getText().equals(DTransd.watchHashtag))
						updateAwareness();
				}

				tweet = new Tweet(0);
				tweet.setHashtags(tagsToTweet);

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

	/**
	 * updates the user atomic model awareness of the watched hashtag
	 * the model state changes to be aware 
	 * the model color changes to red in the simulation view
	 * 
	 * it also maintains the time for the first user awareness and last user awareness 
	 * for all instances using static variables for some experimentation purposes
	 */
	private void updateAwareness() {
		setBackgroundColor(Color.red);
		this.aware = true;

		TweetCreator tm = (TweetCreator) ((DTM) getParent()).tm;
		if(first_aware_time > tm.getTwitterTime())
			first_aware_time = tm.getTwitterTime();
		if(last_aware_time < tm.getTwitterTime())
			last_aware_time = tm.getTwitterTime();

		if(TweetCreator.allUsersAware){
			writeToFile();
		}
	}

	public void  deltext(double e,message x){
		Continue(e);

		for (int i=0; i< x.getLength();i++){
			// the input is a tweeting command
			if(messageOnPort(x, TWEET_P, i)){
				try{
					TweetCommandEntityList tmp = (TweetCommandEntityList)x.getValOnPort(TWEET_P, i);
					List<TweetCommandEntity> commands = tmp.getEntities();
					for(TweetCommandEntity tce : commands){
						processTweetCommand(tce); 
					}
				}
				catch(ClassCastException cce){
					System.out.println("Improper message on " + TWEET_P);
				}
			} 
			// the input received via user timeline (tweet or retweet)
			else if(messageOnPort(x, TIMELINE_P, i)){
				try{
					TweetEntity te = (TweetEntity) x.getValOnPort(TIMELINE_P, i);
					if(te.getTweet() != null)
						for(Hashtag hashtag: te.getTweet().getHashtags()){
							if(hashtag.getText().equals(DTransd.watchHashtag))
								updateAwareness();
						}
				}
				catch(ClassCastException cce){
					System.out.println("Improper message on " + TIMELINE_P);
				}
			}
		}
	}

	public void deltint(){
		holdIn(PASSIVE, INFINITY);
	}


	public message out(){
		message m = new message( );

		// send tweet to the followers
		if (phaseIs(STATE_TWEET)){
			content c = makeContent(FOLLOWERS_P, new TweetEntity("Tweet!", tweet));
			m.add(c);
		} 
		// send retweet to the followers
		else if (phaseIs(STATE_RETWEET)){
			content c = makeContent(FOLLOWERS_P, new TweetEntity("Retweet!", tweet));
			m.add(c);
		}

		return m;
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


	/**
	 * write some experiment result (watched hashtag awareness) to a file
	 */
	public static void writeToFile() {

		try {
			String content = "1"+"\t" +first_aware_time + "\t" + last_aware_time + "\n";
			File file = new File("stats/watch_awareness.txt");

			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw;
			fw = new FileWriter(file.getAbsoluteFile(), true);

			BufferedWriter bw = new BufferedWriter(fw);
			bw.append(content);
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			e.printStackTrace();
		}

	}

}
