/**
 * For Subscriber Transducer
 * @author Sungung Kim
 * @date 3-18-2008
 */

package GenService;

import view.simView.*;
import model.modeling.*;
import model.simulation.*;
import view.modeling.*;
import java.lang.*;
import java.util.ArrayList;
import GenCol.*;

public class ServiceTransducer extends ViewableAtomic {
  
  protected ArrayList <Pair> in, out;  //Information for input ports and output ports of the service
  protected double clock;              //To measure time
  protected double observation_time;   //Total observation time
  
  
  public ServiceTransducer(){
    this("ServiceTransd", 100);
  }

  public ServiceTransducer(String name, double Observation_time){
    super(name);

    //Inport for inputs
    addInport("in");
    
    //Inport for outputs 
    addInport("out");

    observation_time = Observation_time;
    
  }

  public void initialize(){
    phase = "active";
    sigma = observation_time;    
    clock = 0;   
    in = new ArrayList <Pair> ();
    out = new ArrayList <Pair> ();
 }

 public void  deltext(double e,message  x){
   clock = clock + e;
   Continue(e);
  
   entity msg;
   
   for(int i=0; i< x.size();i++){  
	   //when a request is arrived, then store the subscriber and request time
	   if(messageOnPort(x,"in",i)){
	   	   msg = x.getValOnPort("in", i);    	
	   	   in.add(new Pair(msg, clock));  //Subscriber name and time
	   }
	   //serviced info
	   else if(messageOnPort(x,"out",i)){
		   msg = x.getValOnPort("out", i);
		   out.add(new Pair(msg, clock));
       }
   }
   
   show_state();
}

 public void  deltint(){
	clock = clock + sigma;
	passivate();
	show_state();
 }
 
 public void  show_state(){
	//Override this method at the sub class
 }
    /**
	 * This method calculate the throughput of service publisher
	 * How to calculate: total number of message generated from the publisher / (endTime -startTime)
	 * @return
	 */
	public double compute_TP(){
		double thruput = 0;
		double startTime = -1;
		double endTime = 0;	
		
		if(in.size() != 0)
			startTime = Double.parseDouble(in.get(0).getValue().toString()); //the first request arrival time
		
		int size = out.size();
		
		if(size > 0){
			endTime = Double.parseDouble(out.get(size-1).getValue().toString()); //the last serviced time
			thruput = (double)out.size()/(endTime - startTime);
		}
		else{
			endTime = 0;
			thruput = 0;
		}		
		
		return thruput;
	}
	
	/**
	 * This method calcultate the turnaround time 
	 * How to calculate: the last time service received - the service request time
	 * @return
	 */
	public double compute_TA(){
		double ta_time = 0;
		double last_time_serviced = 0;
		 
		 for(int i=0; i<out.size(); i++){
			 for(int j=0; j<in.size(); j++){
				 if(((ServiceCallMessage)out.get(i).getKey()).getPublisher().equals(((ServiceCallMessage)in.get(j).getKey()).getPublisher())){
					 last_time_serviced = Double.parseDouble(in.get(j).getValue().toString());
				 }
			 }
			 ta_time = last_time_serviced - Double.parseDouble(out.get(i).getValue().toString());
		}
		//For the first case
		 if(ta_time < 0)
			ta_time = 0;
		
		 return ta_time;
	}
	
}