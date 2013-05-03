package project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import project.entities.HashtagTweetLists;
import project.entities.StartExperiment;
import project.entities.StatisticsEntity;
import model.modeling.message;
import twitter.types.Hashtag;
import twitter.types.Tweet;
import view.modeling.ViewableAtomic;

public class DTransd extends ViewableAtomic{

	double observation_time;
	// States
	public static final String PASSIVE = "passive";
	public static final String OBSERVE = "observing";
	public static final String SEND = "sending";
	public static final String SEND2 = "sendJobAndStats";

	// Ports
	public static final String LISTS_P = "lists";
	public static final String SOLVED_P = "solved";
	public static final String GET_EXP_P = "getExperiment";
	public static final String STAT_P = "stat";

	HashtagTweetLists ht;
	StatisticsEntity stat = new StatisticsEntity();
	int sendTo, num_of_proc = 0 , max_proc = DTM.PROCS;
	int proc_counter = 0;
	int twitter_time;
	Queue<HashtagTweetLists> waiting_lists = new LinkedList<HashtagTweetLists>();
	boolean first_write ;
	public static String watchHashtag ;
	private Set<Integer> watchedSet = new HashSet<Integer>();

	public DTransd(String name, double ot){
		super(name);
		observation_time = ot;
		addInport(LISTS_P);
		addInport(SOLVED_P);
		addInport(GET_EXP_P);
		addOutport(STAT_P);
	}

	public void initialize(){
		waiting_lists.clear();
		first_write = true;
		num_of_proc = 0;
		proc_counter = 0;
		twitter_time = 0;
		watchHashtag = "#a";
		holdIn(PASSIVE, INFINITY);
	}

	public void  deltext(double e,message x){
		Continue(e);
		twitter_time += e;
		ht = null;

		for (int i=0; i< x.getLength();i++){
			// the received input contains twitter lists which need to be processed
			if (messageOnPort(x,"lists",i))
			{
				if(x.getValOnPort("lists",i) instanceof HashtagTweetLists)
					ht = (HashtagTweetLists) x.getValOnPort("lists", i);

				if(ht != null){
					updateWatchSet();
					if(num_of_proc < max_proc){
						addProcessor(10);
						holdIn(SEND, 0);
					} else {
						waiting_lists.add(ht);
					}
				}
				//observation time can be calculated based on the input data e.g: number of hashtags
			} 
			// the received input contains statistics that have been calculated by a processor
			else if(messageOnPort(x,SOLVED_P,i)){
				stat = (StatisticsEntity) x.getValOnPort(SOLVED_P, i);
				if(waiting_lists.isEmpty()){
					removeProcessor(stat.getProcessedBy());
					holdIn(OBSERVE, 0);
				}
				else{
					ht = waiting_lists.poll();
					if(stat.getProcessedBy().length() >= 2)
						sendTo = (Integer.parseInt(stat.getProcessedBy().charAt(1)+""));

					holdIn(SEND2, 0);
				}

			} 
			// the received input contains some experiment data (received only at the beginning of the experiment)
			else if(messageOnPort(x,GET_EXP_P,i)){
				StartExperiment exp = (StartExperiment) x.getValOnPort(GET_EXP_P, i);
				watchHashtag = exp.getHashtagToWatch();
			}
		}
	}

	/**
	 * this method to update users information about the watched hashtag
	 * it basically calculate the number of users that have been aware of
	 * this hashtag at some time unit
	 * 
	 */
	private void updateWatchSet() {
		for (Tweet tweet : ht.getTweets()) {
			for(Hashtag hashtag: tweet.getHashtags())
				if(hashtag.getText().equals(watchHashtag))
					watchedSet.add(tweet.getUserID());
		}
	}

	public void deltint(){
		holdIn(PASSIVE, INFINITY);
	}

	public message out(){
		message m = new message( );
		if (phaseIs(OBSERVE)){
			showState();
			writeToWatchedFile();
			writeToFile(stat);
			m.add(makeContent(STAT_P, stat));
		} else if(phaseIs(SEND)){
			m.add(makeContent("send_lists_P"+proc_counter, ht));
		} else if(phaseIs(SEND2)){
			showState();
			writeToWatchedFile();
			writeToFile(stat);
			m.add(makeContent(STAT_P, stat));
			m.add(makeContent("send_lists_P" + sendTo, ht));
		}
		return m;
	}

	/**
	 * this method to add processor 
	 * 
	 * @param duration to define the processing sigma
	 * 
	 */
	private void addProcessor(double duration){
		DTM parent = (DTM) getParent();
		proc_counter ++;
		num_of_proc ++;
		Processor proc = parent.addProcessor(proc_counter, duration);
		addModel(proc);

		// add coupling between transd and proc
		addCoupling(proc.getName(),STAT_P,parent.tr.getName(),SOLVED_P);
		addOutport(parent.tr.getName(),"send_lists_" + proc.getName());
		addCoupling(parent.tr.getName(),"send_lists_" + proc.getName(),proc.getName(),"lists");

	}

	/**
	 * this method to remove a processor if it is not needed anymore
	 * 
	 * @param processor name to be removed
	 * 
	 */
	private void removeProcessor(String procName){
		DTM parent = (DTM) getParent();

		removeCoupling(parent.tr.getName(),"send_lists_"+procName,procName,"lists");
		removeCoupling(procName,STAT_P,parent.tr.getName(),SOLVED_P);
		removeOutport(parent.tr.getName(),"send_lists_"+procName);

		removeModel(procName);
		num_of_proc --;
	}

	/**
	 * write some experiment result to a file (twitter statistics)
	 */
	public void writeToFile(StatisticsEntity stat) {

		try {
			double fracWatched = stat.getwatchedPerc();

			String content = (twitter_time-10) + "\t" + stat.getEntropy() +"\t" + stat.getHashtags().size() + "\t"+ stat.getNumOfusers() + 
			"\t" + stat.getHerf() + "\t" + fracWatched + "\n";
			File file = new File("stats/stats_6000.txt");

			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw;
			if(first_write){
				fw = new FileWriter(file.getAbsoluteFile());

			}
			else{
				fw = new FileWriter(file.getAbsoluteFile(), true);
			}

			BufferedWriter bw = new BufferedWriter(fw);
			if(first_write){
				bw.append("Time\tEntropy\tNum. Hashtags\tNum. Users\tHHI\tFrac. Watched\n");
			}
			bw.append(content);
			bw.close();


			first_write = false;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			e.printStackTrace();
		}

	}

	/**
	 * write some experiment result to a file (watched hashtag data)
	 */
	public void writeToWatchedFile() {

		try {
			String content = (twitter_time-10) + "\t" + watchedSet.size() + "\n";
			File file = new File("stats/watchedHashtag.txt");

			if (!file.exists()) 
				file.createNewFile();
			
			FileWriter fw;
			if(first_write)
				fw = new FileWriter(file.getAbsoluteFile());
			else
				fw = new FileWriter(file.getAbsoluteFile(), true);

			BufferedWriter bw = new BufferedWriter(fw);
			if(first_write){
				bw.append("Time\tNum. Users\n");
			}
			bw.append(content);
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			e.printStackTrace();
		}

	}

}