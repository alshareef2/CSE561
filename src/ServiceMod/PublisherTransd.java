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

public class PublisherTransd extends ServiceTransducer {
	protected String name;
  
	public PublisherTransd(String name, double Observation_time){
		super(name, Observation_time);
		this.name = name;
		//in = new ArrayList <Pair> ();
		//out = new ArrayList <Pair> (); 
	}
  
	public void  show_state(){
		System.out.println("\n----------------------------------------------------["+name+"]");
		System.out.println("- Publisher thruoughput   (msgs/sec)  : " + compute_TP());
		System.out.println("- The amount of data received (Kbyte) : " + total_size_msgs());
		System.out.println("- The number of subscribers           : " + numbOfSubscribers());
		System.out.println("-----------------------------------------------------------------");
	}
	
	/**
	 * This method calculate the total size of messages received.
	 * @return
	 */
	public double total_size_msgs(){
		double total_size_msgs = 0;
		
		for(int i=0; i< in.size(); i++){
			ServiceCallMessage temp = (ServiceCallMessage)(in.get(i).getKey());
			total_size_msgs += temp.getSize();
		}
		   
		return total_size_msgs; 		
	}
	
	public int numbOfSubscribers(){
		HashMap <String, String> subscribers =  new HashMap <String, String>();
		
		for(int i=0; i< in.size(); i++){
			ServiceCallMessage temp = (ServiceCallMessage)in.get(i).getKey();
			if (!subscribers.containsKey(temp.getSubscriber())){
				subscribers.put(temp.getSubscriber(), temp.getPublisher());
			}
		}		
		return subscribers.size();
	}
}