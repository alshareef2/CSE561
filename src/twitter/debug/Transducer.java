package twitter.debug;

import project.entities.HashtagTweetLists;
import project.entities.StatisticsEntity;
import GenCol.entity;
import model.modeling.message;
import view.modeling.ViewableAtomic;

// Transd comment test
public class Transducer extends ViewableAtomic{

	double observation_time;
	public static final String PASSIVE = "passive";
	public static final String OBSERVE = "observing";
	
	
	public Transducer(){
		super("Tansducer");
		observation_time = 100;
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

	public void  deltext(double e,message x){
		Continue(e);
		HashtagTweetLists ht = null;
		
		for (int i=0; i< x.getLength();i++){
			if (messageOnPort(x,"lists",i))
			{
				if(x.getValOnPort("lists",i) instanceof HashtagTweetLists)
					ht = (HashtagTweetLists) x.getValOnPort("lists", i);

				if(ht != null){
					if(phaseIs(PASSIVE))
						holdIn(OBSERVE, observation_time);
						//observation time can be calculated based on the input data e.g: number of hashtags
				}
			}
		}
	}
	
	public void deltint(){
		process();
		holdIn(PASSIVE, INFINITY);
	}
	
	private void process() {
		// TODO Auto-generated method stub
		
	}

	public message out(){
		message m = new message( );
		if (phaseIs(OBSERVE)){
			System.out.println("Some Stats!");
			showState();
			m.add(makeContent("stat", new StatisticsEntity()));
		}
		return m;
	}
}