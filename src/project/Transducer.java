package project;

import java.util.LinkedList;
import java.util.Queue;

import project.entities.HashtagTweetLists;
import project.entities.StatisticsEntity;
import model.modeling.message;
import view.modeling.ViewableAtomic;

// Transd comment test
// transd commit test
public class Transducer extends ViewableAtomic{

	double observation_time;
	public static final String PASSIVE = "passive";
	public static final String OBSERVE = "observing";
	public static final String SEND = "sending";
	public static final String SEND2 = "sendJobAndStats";
	HashtagTweetLists ht;
	double time_left_P1, time_left_P2, time_left_P3;
	StatisticsEntity stat = new StatisticsEntity();
	boolean P1, P2, P3;
	int sendTo;
	Queue<HashtagTweetLists> waiting_lists = new LinkedList<HashtagTweetLists>();

	public Transducer(){
		super("Transducer");
		observation_time = 10;
		addInport("lists");
		addInport("solved");
		addOutport("stat");
		addOutport("send_lists_P1");
		addOutport("send_lists_P2");
		addOutport("send_lists_P3");
	}

	public Transducer(String name, double ot){
		super(name);
		observation_time = ot;
		addInport("lists");
		addInport("solved");
		addOutport("stat");
		addOutport("send_lists_P1");
		addOutport("send_lists_P2");
		addOutport("send_lists_P3");
	}

	public void initialize(){
		P1 = P2 = P3 = true;
		holdIn(PASSIVE, INFINITY);
	}

	public void  deltext(double e,message x){
		time_left_P1 = sigma - e;
		time_left_P2 = sigma - e;
		time_left_P3 = sigma - e;
		Continue(e);
		ht = null;

		for (int i=0; i< x.getLength();i++){
			if (messageOnPort(x,"lists",i))
			{
				if(x.getValOnPort("lists",i) instanceof HashtagTweetLists)
					ht = (HashtagTweetLists) x.getValOnPort("lists", i);

				if(ht != null){
					if(P1){
						sendTo = 1;
						P1 = false;
						holdIn(SEND, 0);
					} else if(P2){
						sendTo = 2;
						P2 = false;
						holdIn(SEND, 0);
					} else if(P3){
						sendTo = 3;
						P3 = false;
						holdIn(SEND, 0);
					} else {
						waiting_lists.add(ht);
					}
				}
				//observation time can be calculated based on the input data e.g: number of hashtags
			} else if(messageOnPort(x,"solved",i)){
				stat = (StatisticsEntity) x.getValOnPort("solved", i);
				if(stat.getProcessedBy().equals("P1")){
					if(waiting_lists.isEmpty()){
						P1 = true;
						holdIn(OBSERVE, 0);
					}
					else{
						ht = waiting_lists.poll();
						sendTo = 1;
						holdIn(SEND2, 0);
					}
				} else if(stat.getProcessedBy().equals("P2")){
					if(waiting_lists.isEmpty()){
						P2 = true;
						holdIn(OBSERVE, 0);
					}
					else{
						ht = waiting_lists.poll();
						sendTo = 2;
						holdIn(SEND2, 0);
					}
				} else {
					if(waiting_lists.isEmpty()){
						P3 = true;
						holdIn(OBSERVE, 0);
					}
					else{
						ht = waiting_lists.poll();
						sendTo = 3;
						holdIn(SEND2, 0);
					}
				}
			}
		}
	}


	public void deltint(){
		holdIn(PASSIVE, INFINITY);
	}

	public message out(){
		message m = new message( );
		if (phaseIs(OBSERVE)){
			showState();
			m.add(makeContent("stat", stat));
		} else if(phaseIs(SEND)){
			if(sendTo == 1)
				m.add(makeContent("send_lists_P1", ht));
			else if(sendTo == 2)
				m.add(makeContent("send_lists_P2", ht));
			else 
				m.add(makeContent("send_lists_P3", ht));
		} else if(phaseIs(SEND2)){
			m.add(makeContent("stat", stat));
			if(sendTo == 1)
				m.add(makeContent("send_lists_P1", ht));
			else if(sendTo == 2)
				m.add(makeContent("send_lists_P2", ht));
			else 
				m.add(makeContent("send_lists_P3", ht));
		}
		return m;
	}
}