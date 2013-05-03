package project;

import model.modeling.message;
import project.entities.HashtagTweetLists;
import project.entities.StatisticsEntity;
import twitter.types.Hashtag;
import twitter.types.Tweet;
import view.modeling.ViewableAtomic;

import util.CorrelationMatrix;
import util.Matrix;

public class Processor extends ViewableAtomic{

	String pName;
	double observation_time;
	
	// States
	public static final String PASSIVE = "passive";
	public static final String BUSY = "busy";
	
	// Ports
	public static final String LISTS_P = "lists";
	public static final String STAT_P = "stat";
	
	public static final String watchedHashtag = "#a";
	HashtagTweetLists ht;
	StatisticsEntity stat = new StatisticsEntity();

	public Processor(String name, double ot){
		super(name);
		this.pName = name;
		observation_time = ot;
		addInport(LISTS_P);
		addOutport(STAT_P);
	}

	public void initialize(){
		holdIn(PASSIVE, INFINITY);
	}

	public void  deltext(double e,message x){
		Continue(e);
		ht = null;

		for (int i=0; i< x.getLength();i++){
			// only accepts one input which the twitter lists that need processing
			if (messageOnPort(x,LISTS_P,i))
			{
				if(x.getValOnPort(LISTS_P,i) instanceof HashtagTweetLists)
					ht = (HashtagTweetLists) x.getValOnPort(LISTS_P, i);

				if(ht != null){
					if(phaseIs(PASSIVE)){
						holdIn(BUSY, observation_time);
						process();
						coe_matrix();
						entropy();
						herfindahl();
					}
				}
			}
		}
	}

	public void deltint(){
		holdIn(PASSIVE, INFINITY);
	}

	/**
	 * this method to process the twitter lists
	 * 
	 * it prepares the lists for calculating entropy, herfindahl index, coefficient
	 * it calculate the top tweeted hashtag, top retweeted tweet
	 * 
	 */
	private void process() {
		stat = new StatisticsEntity();
		stat.setNumOfusers(ht.getnumberOfUniqueUsers());
		for (Hashtag hashtag : ht.getHashtags()) {
			stat.getHashtags().put(hashtag, 0);
		}

		Tweet top_rt = new Tweet(-1111);
		Hashtag top_h = new Hashtag(-1111,"","");
		int max = 0;
	
		for (Tweet tweet : ht.getTweets()) {
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
			if(stat.getHashtags().get(hashtag) >= max){
				max = stat.getHashtags().get(hashtag);
				top_h = hashtag;
			}
		}

		stat.setTop_tweeted(top_h);

		//find the frequency of the watched hashtag
		int count = 0;
		int numWatched = 0;
		for(Tweet t : ht.getTweets()){
			for(Hashtag h : t.getHashtags()){
				count++;
				if(h.getText().equals(watchedHashtag)){
					numWatched++;
				}
			}
		}
		stat.setwatchedPerc(numWatched * 1.0 / count);
	}
	
	/**
	 * this method to calculate the hashtag entropy
	 * 
	 */
	private void entropy(){
		int nOfH = stat.getHashtags().size();
		double P[] = new double[nOfH];
		int total = 0;
		
		for(Hashtag h: stat.getHashtags().keySet())
			total += stat.getHashtags().get(h);
		
		int i=0;
		for(Hashtag h: stat.getHashtags().keySet())
			P[i++] = (double) stat.getHashtags().get(h) / total;
		
		double entropy = 0;
		for(int j=0; j< nOfH; j++)
			entropy += P[j]*Math.log(P[j]);
		
		stat.setEntropy(entropy*-1);
		
	}

	/**
	 * this method to calculate the herfindahl index
	 * 
	 */
	private void herfindahl(){
		int nOfH = stat.getHashtags().size();
		double P[] = new double[nOfH];
		int total = 0;
		
		for(Hashtag h: stat.getHashtags().keySet())
			total += stat.getHashtags().get(h);
		
		int i=0;
		for(Hashtag h: stat.getHashtags().keySet())
			P[i++] = (double) stat.getHashtags().get(h) / total;
		
		double hhi = 0;
		for(int j=0; j< nOfH; j++)
			hhi += P[j] * P[j];

		hhi = (hhi - 1.0 / nOfH) / (1 - 1.0 / nOfH);
		
		stat.setHerf(hhi);
		
	}

	/**
	 * this method to calculate the coefficient matrix for the hashtags
	 * 
	 */
	private void coe_matrix(){
		int noOfH = stat.getHashtags().size();
		double data[][] = new double[60][noOfH];
		
		for(int i=0; i<60; i++)
			for(int k=0; k<noOfH; k++)
				data[i][k] = 0;

		for (Tweet tweet: ht.getTweets()){
			int i = 0;
			for (Hashtag hashtag : stat.getHashtags().keySet()) {
				if(tweet.getHashtags().contains(hashtag))
					data[((int) tweet.getTime()) % 60][i] ++;
				i++;
			}
		}

		Matrix m = CorrelationMatrix.makeCorrelationMatrix(data);
		stat.setCOEMatrix(m);
		
	}

	public message out(){
		message m = new message( );
		if (phaseIs(BUSY)){
			System.out.println("Some Stats 1: " + stat.getTop_tweeted().getText());
			System.out.println("Some Stats 2: " + stat.getTop_retweeted().getTweetID());
			showState();
			stat.setProcessedBy(pName);
			m.add(makeContent(STAT_P, stat));
		}
		
		return m;
	}
}
