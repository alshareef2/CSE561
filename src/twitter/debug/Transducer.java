package twitter.debug;

import project.entities.HashtagTweetLists;
import project.entities.StatisticsEntity;
import GenCol.entity;
import model.modeling.message;
import twitter.types.Hashtag;
import twitter.types.Tweet;
import view.modeling.ViewableAtomic;

// Transd comment test
public class Transducer extends ViewableAtomic{

	double observation_time;
	public static final String PASSIVE = "passive";
	public static final String OBSERVE = "observing";
	HashtagTweetLists ht;
	StatisticsEntity stat = new StatisticsEntity();

	public Transducer(){
		super("Tansducer");
		observation_time = 10;
		addInport("lists");
		addInport("h_query");
		addOutport("stat");
		addOutport("send_lists");
	}

	public Transducer(String name, double ot){
		super(name);
		observation_time = ot;
		addInport("lists");
		addInport("h_query");
		addOutport("stat");
		addOutport("send_lists");
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
						holdIn(OBSERVE, observation_time);
						process();
					}
					//bservation time can be calculated based on the input data e.g: number of hashtags
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
		System.out.println("Number of Tweets: "+ht.getTweets().size() + ", Hashtags: "+ ht.getHashtags().size());
		for (Tweet tweet : ht.getTweets()) {
			System.out.println("TWEET ID:" + top_rt.getTweetID() + ",NoOfRT" + top_rt.getNumberOfRT());
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
		if (phaseIs(OBSERVE)){
			System.out.println("Some Stats 1: " + stat.getTop_tweeted().getHashtagID());
			System.out.println("Some Stats 2: " + stat.getTop_retweeted().getTweetID());
			showState();
			m.add(makeContent("stat", stat));
		}
		return m;
	}
}