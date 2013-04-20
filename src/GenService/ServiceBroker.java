package GenService;

import view.simView.*;
import model.modeling.*;
import model.simulation.*;
import view.modeling.*;
import GenCol.*;

import util.*;
import java.util.*;
import java.lang.*;

/** 
 * @author Sungung Kim
 * @version 1.0
 * This class is available for a given time.
 * If inputs arrive before being available, then return "Not avail" message to subscribers and publishers
 * This class will receive inputs from the services for the purpose of publication
 * and return the matched service to the subscriber for subscription
 */

public class ServiceBroker extends ViewableAtomic{
	
	protected double previous = 0;            //message processing time
	protected double proc_time = 0;           //time to subscribe or publish
	protected double start;                   //start time to be available
	protected double available_time;          //Availabe duration
	protected double clock;                   //current time
	protected String pub = "publish";         //publication port
	protected String sub = "subscribe";       //subscription port
 	protected String subscriber;              //for the subscriber for the matched service
 	protected String endpt;
 	protected String serviceName;
	protected ArrayList <ServiceInfoMessage> UDDI = new ArrayList <ServiceInfoMessage> ();;	  //storage for the published services
	protected GenCol.Queue inQ;
	protected entity packet;
	protected ServiceInfoMessage returnMsg;
	
	public ServiceBroker() 
	{
		this("Broker", 0.0, 0.0);
	}

	public ServiceBroker(String name, double avail_time, double startTime){		
		super(name);
		available_time = avail_time;
		start = startTime;
		//Two basic input ports
	   	addInport(pub);
	   	addInport(sub);
	   	addOutport("active");
	   	initialize();
	}

	public void initialize(){
		clock = 0;
		inQ= new GenCol.Queue();
		holdIn("passive", start);
		super.initialize();
	 }

	public void  deltext(double e,message x)
	{
		Continue(e);  
		clock = clock + e;
		//add subscription or publication messages into the Queue
		if(phaseIs("active")){
			for (int i=0; i< x.getLength();i++)
				if (messageOnPort(x,pub,i)){
					packet = x.getValOnPort(pub, i);
					publish(packet);
					holdIn("publishing", proc_time);					
					inQ.add(packet);
				}
				else if (messageOnPort(x,sub,i)){					
					packet = x.getValOnPort(sub, i);
					holdIn("subscribing", proc_time);
					inQ.add(packet);
				}
				
			//indicate the first packet to send	
			packet = (entity)inQ.first();						
		}
		else if(phaseIs("passive")){  //in the case of 
			for (int i=0; i< x.getLength();i++)
				if (messageOnPort(x,pub,i)){
					packet = x.getValOnPort(pub, i);
					holdIn("Not Avail", 0);
					inQ.add(packet);
				}
				else if (messageOnPort(x,sub,i)){					
					packet = x.getValOnPort(sub, i);
					holdIn("Not Avail", 0);
					inQ.add(packet);
				}
			
			packet = (entity)inQ.first();
		}
		else {
			for (int i=0; i< x.getLength();i++)
				if (messageOnPort(x,pub,i)){
					packet = x.getValOnPort(pub, i);					
					inQ.add(packet);
				}
				else if (messageOnPort(x,sub,i)){					
					packet = x.getValOnPort(sub, i);
					inQ.add(packet);
				}
		}		
	}

	public void  deltint( )
	{
		clock = clock + sigma;
		
		if(inQ != null){
			inQ.remove();
		}		 
		
		//Activate the broker first
		//If Q is not empty, then keep the process
		if(phaseIs("passive")){
			holdIn("active", available_time);
		}
		else if(phaseIs("Not Avail")){
			if(!inQ.isEmpty()){		
				packet = (entity)inQ.first();
				holdIn("Not Avail", 0);
			}
			else		
				holdIn("passive", start - clock);
		}
		else if(!inQ.isEmpty()){		
			packet = (entity)inQ.first();
			
			//Publication and Subscription
			if(packet instanceof ServiceInfoMessage){			
				publish(packet);  //publication
				holdIn("publishing", proc_time);
			}
			else		
				holdIn("subscribing", proc_time); 
		}
		//update available time
		else if(phaseIs("publishing")||phaseIs("subscribing")){
			available_time = available_time - (clock - previous);
			holdIn("active", available_time);
			previous = clock;
		}
		else
			passivate();
		
	}
	
	public void deltcon(double e,message x)
	{
	   deltext(e,x);
	   deltint();
	}
	
	public message out( )
	{
		message  m = new message();
		
		if(phaseIs("subscribing")){			
			int index = subscribe(packet);   //Find an index of matched service(s) 
			
			if(index >= 0 ){
				returnMsg = UDDI.get(index);
				if(returnMsg.getServiceType().equalsIgnoreCase("atomic"))
					returnMsg.setBindingInfo(new Pair(returnMsg.getServiceName(), endpt));
			}
			else  //No Service(s) are found
				returnMsg = new ServiceInfoMessage("No Found", null, null, null);
			
			//Send info to the requestor
			returnMsg.setReceiver(subscriber);
			m.add(makeContent(subscriber, returnMsg));	
		}
		else if(phaseIs("passive")){
			m.add(makeContent("active", new entity("start")));
		}
		else if(phaseIs("active")){
			m.add(makeContent("active", new entity("end")));
		}
		
		else if(phaseIs("Not Avail")){
			if(packet instanceof ServiceInfoMessage){			
				ServiceInfoMessage tmp = (ServiceInfoMessage)packet;
				returnMsg = new ServiceInfoMessage("Not Avail", null, null, null);
				returnMsg.setReceiver(tmp.getServiceName());
				m.add(makeContent(tmp.getServiceName(), returnMsg));
			}
			else{
				ServiceLookupMessage tmp = (ServiceLookupMessage)packet;
				returnMsg = new ServiceInfoMessage("Not Avail", null, null, null);
				returnMsg.setReceiver(tmp.getSubscriber());
				m.add(makeContent(tmp.getSubscriber(), returnMsg));
			}
		}
		
		return m;
	}
	
	/**
	 * Store the service information into the UDDI
	 **/	
	private void publish(entity msg){		
		
		ServiceInfoMessage Content = (ServiceInfoMessage)msg;
		UDDI.add(Content);
	}
	
	/**
	 * this method iterates UDDI until the matched service is found and return the index,
	 * otherwise return -1
	 **/	
	private int subscribe(entity msg){
		int index = 0;
		boolean isFound = false;
		
		System.out.println("********************** Start lookup");
		
		ServiceLookupMessage subscribeMsg = (ServiceLookupMessage)msg;
		
		//We need to know who is subscribing so that Broker can return the result to the subscriber
		subscriber  = subscribeMsg.getSubscriber();
		endpt       = subscribeMsg.getEndpoint();
		serviceName = subscribeMsg.getServiceName();
		
		//Match the requested endpoint with the stored endpoint(s)
		while(index < UDDI.size()){
			//Current service in the UDDI
			ServiceInfoMessage currService = UDDI.get(index);
			
			//If the service is found
			if(serviceName.equalsIgnoreCase(currService.getServiceName())){
				//Find the endpoint in the service
				for(int i=0; i<currService.getEndpoints().size(); i++){
					if(endpt.equalsIgnoreCase(currService.getEndpoints().get(i).getKey().toString())){
						isFound = true;
						break;
					}
				}			
			}
			
			if(isFound)
				break;  //Break While statement
			else
				index++;
		}
		
		//No service is matched
		if(!isFound)
			index = -1;
		
		System.out.println("********************** End lookup");
		
		return index;
	}
	
	public void publishCompositeService(ServiceInfoMessage compositeService){
		
		UDDI.add(compositeService);
		
		System.out.println("*********************************************************************                " + UDDI);
	}

}
