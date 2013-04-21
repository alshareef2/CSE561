package project;

import java.util.LinkedList;
import java.util.Queue;
import project.entities.HashtagTweetLists;
import project.entities.StatisticsEntity;
import model.modeling.message;
import view.modeling.ViewableAtomic;

public class DTransd extends ViewableAtomic{

	double observation_time;
	public static final String PASSIVE = "passive";
	public static final String OBSERVE = "observing";
	public static final String SEND = "sending";
	public static final String SEND2 = "sendJobAndStats";
	HashtagTweetLists ht;
	StatisticsEntity stat = new StatisticsEntity();
	boolean P1, P2, P3;
	int sendTo, num_of_proc = 0 , max_proc = 3;
	int proc_counter = 0;
	Queue<HashtagTweetLists> waiting_lists = new LinkedList<HashtagTweetLists>();

	public DTransd(){
		super("DTransd");
		observation_time = 10;
		addInport("lists");
		addInport("solved");
		addOutport("stat");
	}

	public DTransd(String name, double ot){
		super(name);
		observation_time = ot;
		addInport("lists");
		addInport("solved");
		addOutport("stat");
	}

	public void initialize(){
		P1 = P2 = P3 = true;
		waiting_lists.clear();
		num_of_proc = 0;
		proc_counter = 0;
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
					if(num_of_proc < max_proc){
						addProcessor(10);
						holdIn(SEND, 0);
					} else {
						waiting_lists.add(ht);
					}
				}
				//observation time can be calculated based on the input data e.g: number of hashtags
			} else if(messageOnPort(x,"solved",i)){
				stat = (StatisticsEntity) x.getValOnPort("solved", i);
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
			m.add(makeContent("send_lists_P"+proc_counter, ht));
		} else if(phaseIs(SEND2)){
			m.add(makeContent("stat", stat));
			m.add(makeContent("send_lists_P" + sendTo, ht));
		}
		return m;
	}

	private void addProcessor(double duration){
		DTM parent = (DTM) getParent();
		//	 exLog.append(parent.getSimulator().getTL()+","+"client"+parent.subCount+","+"created"+"\n");
		proc_counter ++;
		num_of_proc ++;
		Processor proc = parent.addProcessor(proc_counter, duration);
		addModel(proc);

		// between transd and proc
		addCoupling(proc.getName(),"stat",parent.tr.getName(),"solved");
		addOutport(parent.tr.getName(),"send_lists_"+proc.getName());
		addCoupling(parent.tr.getName(),"send_lists_"+proc.getName(),proc.getName(),"lists");

	}

	private void removeProcessor(String procName){
		DTM parent = (DTM) getParent();

		removeCoupling(parent.tr.getName(),"send_lists_"+procName,procName,"lists");
		removeCoupling(procName,"stat",parent.tr.getName(),"solved");
		removeOutport(parent.tr.getName(),"send_lists_"+procName);

		removeModel(procName);
		num_of_proc --;
	}

}