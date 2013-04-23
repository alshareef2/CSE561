package project;

import model.modeling.message;
import project.entities.HashtagTweetLists;
import project.entities.StatisticsEntity;
import twitter.types.Hashtag;
import twitter.types.Tweet;
import view.modeling.ViewableAtomic;

import util.CorrelationMatrix;
import util.Matrix;

// commit test
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
						coe_matrix();
						entropy();
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
	
	private void entropy(){
		int nOfH = stat.getHashtags().size();
		double P[] = new double[nOfH];
		int total = 0;
		
		for(Hashtag h: stat.getHashtags().keySet())
			total += stat.getHashtags().get(h);
		
		int i=0;
		for(Hashtag h: stat.getHashtags().keySet())
			P[i] = (double) stat.getHashtags().get(h) / total;
		
		double entropy = 0;
		for(int j=0; j< nOfH; j++)
			entropy += P[j]*Math.log(P[j]);
		
		stat.setEntropy(entropy*-1);
	}

	private void coe_matrix(){
		int noOfH = stat.getHashtags().size();
		double data[][] = new double[60][noOfH];
		
		for(int i=0; i<60; i++)
			for(int k=0; k<noOfH; k++)
				data[i][k] = 0;

		for (Tweet tweet: ht.getTweets()){
			
			//System.out.print(tweet.getTweetID() +", NofH: "+ tweet.getHashtags().size() + " H# : ");
			//for(Hashtag hashtag : tweet.getHashtags()){
				//System.out.print(hashtag.getText() + ", ");
			//}
			//System.out.println("..");
			
			int i = 0;
			for (Hashtag hashtag : stat.getHashtags().keySet()) {
				if(tweet.getHashtags().contains(hashtag))
					data[((int) tweet.getTime())][i] ++;
				i++;
				
			}
		}
		
		System.out.println("COEF DATA MATRIX:");
		for(int i=0; i<60; i++){
			for(int k=0; k<noOfH; k++)
				System.out.print(data[i][k] + " ");
			System.out.print("\n");
		}

		Matrix m = CorrelationMatrix.makeCorrelationMatrix(data);
		stat.setCOEMatrix(m);
		
		System.out.println("COEF MATRIX:\n"+stat.getCOEMatrix());
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
