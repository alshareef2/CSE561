/**
 * For Subscriber Transducer
 * @author Sungung Kim
 * @date 3-18-2008
 */

package ServiceMod;

import view.simView.*;
import model.modeling.*;
import model.simulation.*;
import view.modeling.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.HashMap;

import GenCol.*;
import GenService.ServiceCallMessage;
import GenService.ServiceTransducer;

public class SubscriberTransd extends ServiceTransducer {
	protected String name;
	
	public SubscriberTransd(String name, double Observation_time){
		super(name, Observation_time);
		this.name = name;
	}
  
	public void  show_state(){
		System.out.println("\n----------------------------------------------------["+name+"]");
		System.out.println("- Avg Turnaround time (sec)            : " + compute_TA());
		System.out.println("- Total size of data received (Kbytes) : " + total_size_msgs());
		System.out.println("- The number of publishers             : " + numbOfPublisher());
		System.out.println("-----------------------------------------------------------------");
	}
	
	public int numbOfPublisher(){
		HashMap <String, String> publishers =  new HashMap <String, String>();
		
		for(Pair val : in){
			ServiceCallMessage temp = (ServiceCallMessage)val.getKey();
			if (!publishers.containsKey(temp.getPublisher())){
				publishers.put(temp.getPublisher(), temp.getReceiver());
			}
		}		
		return publishers.size();
	}
	
	/**
	 * This method calculate the total size of messages received.
	 * @return
	 */
	public double total_size_msgs(){
		double total_size_msgs = 0;
		
		for(int i=0; i< in.size(); i++){
			ServiceCallMessage temp = (ServiceCallMessage)in.get(i).getKey();
			total_size_msgs += temp.getSize();
		}
		   
		return total_size_msgs; 		
	}
	
}