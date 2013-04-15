package project;

import model.modeling.message;
import project.entities.HashtagTweetLists;
import project.entities.StatisticsEntity;
import twitter.types.Hashtag;
import twitter.types.Tweet;
import view.modeling.ViewableAtomic;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class Processor extends ViewableAtomic{

	String pName;
	double observation_time;
	public static final String PASSIVE = "passive";
	public static final String BUSY = "busy";
	HashtagTweetLists ht;
	StatisticsEntity stat = new StatisticsEntity();

	public Processor(){
		super("Processor");
		observation_time = 10;
		addInport("lists");
		addOutport("stat");
	}

	public Processor(String name, double ot){
		super(name);
		this.pName = name;
		observation_time = ot;
		addInport("lists");
		addOutport("stat");
	}

	public void initialize(){
		holdIn(PASSIVE, INFINITY);
	}

	public void  deltext(double e,message x){
		Continue(e);
		ht = null;

		for (int i=0; i< x.getLength();i++){
			if (messageOnPort(x,"lists",i))
			{
				if(x.getValOnPort("lists",i) instanceof HashtagTweetLists)
					ht = (HashtagTweetLists) x.getValOnPort("lists", i);

				if(ht != null){
					if(phaseIs(PASSIVE)){
						holdIn(BUSY, observation_time);
						process();
					}
					//observation time can be calculated based on the input data e.g: number of hashtags
				}
			}
		}
	}

	public void deltint(){
		//process();
		holdIn(PASSIVE, INFINITY);
	}

	private void process() {
		for (Hashtag hashtag : ht.getHashtags()) {
			stat.getHashtags().put(hashtag, 0);
		}

		Tweet top_rt = new Tweet(-1111);
		Hashtag top_h = new Hashtag(-1111,"","");
		int max = 0;
		System.out.println("Number of Tweets: "+ht.getTweets().size() + ", Hashtags: "+ stat.getHashtags().size());
		for (Tweet tweet : ht.getTweets()) {
			System.out.println("TWEET ID:" + tweet.getTweetID() + ",Time: " + tweet.getTime());
			if(tweet.getNumberOfRT() >= top_rt.getNumberOfRT()){
				top_rt = tweet;
			}

			for (Hashtag hashtag : tweet.getHashtags()) {
				if(stat.getHashtags().containsKey(hashtag))
					stat.getHashtags().put(hashtag, stat.getHashtags().get(hashtag) + 1);
			}

		}

		stat.setTop_retweeted(top_rt);

		for (Hashtag hashtag : ht.getHashtags()) {
			//System.out.println("HASHTAG ID:" + top_h.getHashtagID() + ",TEXT" + top_h.getText());
			if(stat.getHashtags().get(hashtag) >= max){
				max = stat.getHashtags().get(hashtag);
				top_h = hashtag;
			}
		}

		stat.setTop_tweeted(top_h);
	}

	public message out(){
		message m = new message( );
		if (phaseIs(BUSY)){
			System.out.println("Some Stats 1: " + stat.getTop_tweeted().getText());
			System.out.println("Some Stats 2: " + stat.getTop_retweeted().getTweetID());
			showState();
			stat.setProcessedBy(pName);
			m.add(makeContent("stat", stat));
		}
		return m;
	}
}
