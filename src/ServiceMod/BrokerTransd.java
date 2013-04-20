/**
 * For Subscriber Transducer
 * @author Sungung Kim
 * @date 3-18-2008
 */

package ServiceMod;


import java.lang.*;
import java.util.ArrayList;
import view.simView.*;
import model.modeling.*;
import model.simulation.*;
import view.modeling.*;
import GenCol.*;
import GenService.ServiceInfoMessage;
import GenService.ServiceLookupMessage;
import GenService.ServiceTransducer;

public class BrokerTransd extends ServiceTransducer {
	protected String name;
	protected double Observation_time;
  
	public BrokerTransd(String name, double Observation_time){
		super(name, Observation_time);
		this.name = name;
		this.Observation_time = Observation_time;
	}
  
	public void  show_state(){
		System.out.println("\n----------------------------------------------------["+name+"]");
		System.out.println("- The num of published services      : " + numOfpublished());
		System.out.println("- Frequency of publication           : " + numOfpublished() / Observation_time);
		System.out.println("- The num of request to lookup       : " + numOflookup());		
		System.out.println("- The num of failed subscriptions    : " + numOfFailure());
		System.out.println("- Frequency of subscription          : " + (numOflookup() - numOfFailure()) / Observation_time);
		System.out.println("- The length of time available (sec) : " + lengthOfAvail());
		System.out.println("-----------------------------------------------------------------");
	}
	
	public int numOfpublished(){
		int count = 0;
		
		for(Pair val : in){
			if(val.getKey() instanceof ServiceInfoMessage)
				count++;
		}
		
		return count;
	}
	
	public int numOflookup(){
		int count = 0;
		
		for(Pair val : in){
			if(val.getKey() instanceof ServiceLookupMessage)
				count++;
		}		
		return count;
	}
	
	public int numOfFailure(){
		int count = 0;
		
		for(Pair val : out){
			if(val.getKey() instanceof ServiceInfoMessage){
				if(((entity)val.getKey()).getName().equalsIgnoreCase("No Found")||
				   ((entity)val.getKey()).getName().equalsIgnoreCase("Not Avail"))
					count++;
			}				
		}		
		return count;
	}
	
	public double lengthOfAvail(){
		double start = 0;
		double end = 0;
		
		for(Pair val : out){
			String tmp = ((entity)val.getKey()).getName();
			if(tmp.equals("start")){
				start = Double.parseDouble(val.getValue().toString());
			}
			else if(tmp.equals("end")){
				end = Double.parseDouble(val.getValue().toString());
			}
		}
		
		if((end-start)<0)
			return 0;
		else
			return (end - start);
	}
}