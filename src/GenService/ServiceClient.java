package GenService;

import view.simView.*;
import model.modeling.*;
import model.simulation.*;
import view.modeling.*;
import GenCol.*;
import util.*;
import java.lang.*;
import java.util.ArrayList;


public class ServiceClient extends ViewableAtomic{
	
	protected boolean isRetry = false;		//flag to indicate whether this lookup is retry or not
	protected int numOftry = 5;             //How many times subscriber try to look up broker if a service is not found
	protected double waitingTime = 1;       //waiting time before re-look up the broker
	protected double serviceResponse = 100; //This is used to wait the response from the service
	protected double startTime;             //When a subscriber can start to look up the service 
	protected double serviceDuration;	    //how long in sec or just one invocation will be one sec
	
	//Message types
	protected ServiceCallMessage ServiceRequest, ServiceResponse;
	protected ServiceLookupMessage lookUp;
	protected ArrayList <ServiceLookupMessage> lookupList;
	protected ServiceInfoMessage service;  //Store service information which is returned from the broker
	
	protected entity packet;
	protected String Subscriber;

	protected String servicePort = "service";
	protected String servicingState = "servicingState"; //to use as a servicing state 
	protected Pair data;	
	
	public ServiceClient() 
	{
		this("User", null, 0);
	}

	public ServiceClient(String name, ArrayList <ServiceLookupMessage> lookupList , double start){
		super(name);
		
	   	addInport("found"); //input port for Broker
		addInport(servicePort);   //Subscribe this method
	   	
	   	addOutport("request"); //ServiceRequest service
	   	addOutport("lookup");  //lookup the broker to find services
	   	
	   	this.Subscriber = name;
	   	this.lookupList = lookupList;
	   	this.startTime = start;
	   	
	   	initialize();
	}

	public void initialize(){
		//If there is an endpoint to subscribe than lookup
		/*if(lookupList.size() != 0){
			lookUp = lookupList.get(0);
			lookUp.setSubscriber(Subscriber);
			lookupList.remove(0);			
			data = lookUp.getData();
			serviceDuration = lookUp.getDuration()+1;
			System.out.println("--------------------------------------" + lookUp.getEndpoint());*/
			holdIn("lookingup", startTime);        //Start to look up the broker  
		//}
	
	 }

	public void  deltext(double e,message x)
	{
		Continue(e);
		
		//Two cases: input from the broker or input from the service
		
		//First: input from the broker
		if(phaseIs("passive")){
			for (int i=0; i< x.getLength();i++)
				if (messageOnPort(x, "found",i))
				{
					packet = x.getValOnPort("found", i);
					service = (ServiceInfoMessage)packet;
					
					//If the service is not found, passivate
					if(packet.getName().equalsIgnoreCase("No Found") || 
					   packet.getName().equalsIgnoreCase("Not Avail")){
						
						if(numOftry == 0)
							passivate();
						else{
							numOftry--;   //reduce number of try 
							isRetry = true;
							holdIn("lookingup", waitingTime);   // look up after waiting time unit
						}
					}
					else{
						isRetry = false;
						holdIn("found", 0);
					}
				}			
		}
		else{  //Second: inputs from the service(s) 
			for (int i=0; i< x.getLength();i++)
				if (messageOnPort(x, servicePort,i))
				{				 
					packet = x.getValOnPort(servicePort, i);
					ServiceResponse = (ServiceCallMessage)packet;
					servicingState = ServiceResponse.getData().value.toString();
					holdIn(servicingState, ServiceResponse.getDuration());
				}		
		}		
	}

	public void  deltint( )
	{
		// basically there is only one ServiceRequest for now.		
		if(phaseIs("found"))
			holdIn("request", 0);
		else if (phaseIs("request"))
			holdIn("waiting", serviceResponse);   //Waiting until 100 time unit and if no response, then send ServiceRequest again
		else if (phaseIs("waiting"))
			holdIn("request", 0);
		else if (phaseIs(servicingState)){
						
			//If there is an endpoint to subscribe than lookup
			if(lookupList.size() != 0){
				holdIn("lookingup", startTime);        //Start to look up the broker  
			}
			else
				passivate();
		}
		else{
			passivate();		
		}
	}

	public message out( )
	{
		message  m = new message();		
	
		if (phaseIs("request")){ //ServiceRequest Service
			ServiceRequest = new ServiceCallMessage(Subscriber, service.getName(), service.getBindingInfo(), data, serviceDuration);
			
			//We need to set the receiver by using setReceiver method
			ServiceRequest.setReceiver();
			
			//Select which is going to be displayed			
			ServiceRequest.setName(data.value.toString()+ data.key.toString());  
			
			m.add(makeContent("request", ServiceRequest));			
		}
		else if (phaseIs("lookingup")){  //Lookup Service.
			//If there is an endpoint to subscribe than lookup
			if(!isRetry && lookupList.size() != 0){
				lookUp = lookupList.get(0);
				lookUp.setSubscriber(Subscriber);
				lookupList.remove(0);			
				data = lookUp.getData();
				serviceDuration = lookUp.getDuration()+1;
				System.out.println("--------------------------------------" + lookUp.getEndpoint());
				//holdIn("lookingup", startTime);        //Start to look up the broker  
			}
			m.add(makeContent("lookup", lookUp));
		}
		
		return m;
	}	
	
	public String getPort(){
		return servicePort;
	}
	
}
