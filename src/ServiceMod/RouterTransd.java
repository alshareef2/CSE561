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
import GenCol.*;
import GenService.ServiceCallMessage;
import GenService.ServiceTransducer;

public class RouterTransd extends ServiceTransducer {
	protected String name;
	double bandwidth = 10 * 1024 * 1024;  //10Mbps
	protected double time = 70;
	protected double TimePt = 0;
  
	public RouterTransd(String name, double Observation_time){
		super(name, Observation_time);
		this.name = name;
	}
  
	public void  show_state(){
		System.out.println("\n----------------------------------------------------["+name+"]");
		System.out.println("- Avg_Transmission_Delay at the Network(Sec)  : " + avg_Transmission_delay());
		System.out.println("- Total Size of Messages received (Kbytes)    : " + total_size_msgs());
		System.out.println("- The utilization for "+time+ " time units (%)     : " + getNetUtilization(time));
		//getMinUtilzation();
		System.out.println("-----------------------------------------------------------------");
	}
	
	/**
	 * This method calculate averate transmission delay at the router service
	 * @return
	 */
	public double avg_Transmission_delay(){
		double avg_delay = 0;
		double previous = 0;
		
		for(int index=0; index< out.size(); index++){
			previous = Double.parseDouble(in.get(index).getValue().toString());
			avg_delay += Double.parseDouble(out.get(index).getValue().toString()) - previous;
		}
		
		if(out.size()>0)
			avg_delay = avg_delay / out.size();
		
		return avg_delay;
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
	
	public double getNetUtilization(double time){		
		double Net_utilization = 0;		
		Net_utilization = (total_size_msgs() / (bandwidth * time)) * 100;
		
		return Net_utilization;
	}
	
	public void getMinUtilzation(){
		
		double min_size_msgs = 0;;
		double Min_utilization = 0;
		
		double timeT = 0;        //for the messages which arrives at the same time
		double sizeT = 0;
		
		double currTime = 0;
		double currSize = 0;
		
		double minTime = 0;     //time and size at the minimum workloads
		double minSize = 0;
		
		TimePt = 0;
		
		for(Pair val : in){
			
			currTime = Double.parseDouble(val.getValue().toString());
			currSize = ((ServiceCallMessage)val.getKey()).getSize();			
			
			if(timeT != currTime){
				
				if(minSize >= sizeT || minSize == 0){
					System.err.println("--------------------------------------netSize: " + sizeT);
					
					minSize = sizeT;  //swap
					System.err.println("--------------------------------------MinSize: " + minSize);
					
					minTime = timeT;
					System.err.println("--------------------------------------MinTime: " + minTime);			
				}
				
				timeT = currTime;
				sizeT = 0;		
				
			}
			else{
				System.err.println("--------------------------------------CurrTime: " + timeT);
				sizeT += currSize;
			}			
		}	
		
		//System.err.println("--------------------------------------" + minTime);
		//System.err.println("--------------------------------------" + minSize);
		
		//return Min_utilization;
	}
	
	public double getMaxUtilzation(){
		double Max_utilization = 0;
		
		
		return Max_utilization;
	}

}