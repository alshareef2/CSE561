package GenService;

import java.util.*;
import view.simView.*;
import model.modeling.*;
import model.simulation.*;
import view.modeling.*;
import GenCol.*;
import util.*;


/**
 * 
 * @author skim109
 * @version 1.0
 * @description this class defines a service publisher (service) 
 * This service maintain two two queue - msgQ for message processing & RequestList for servicing
 * 
 */

public class ServiceProvider extends ViewableAtomic{

	protected int iterator;              //to iterate servicing queue
	protected double Processing_time, Proc_time;
	protected double total_size_packets; //total size of packets
	protected double mean = 0.8;         //mean for servicing
	protected double sig  = 0.02;         //sig of mean for servicing
	protected String ServiceName, ServiceDescription, ServiceType;
	protected ArrayList <Pair> Endpoints;
	protected ArrayList <ServiceCallMessage> RequestList;  //After a message is processed, it will store into this list to be serviced
	protected GenCol.Queue msgQ;	
	protected ServiceCallMessage ServiceReturn, currMsg;
	protected ServiceInfoMessage ServiceInfo;
	protected entity servicePacket, packet;
	protected rand r;
	
	public ServiceProvider() 
	{
		this("Service", null, null, null, 0);
	}

	public ServiceProvider(String name, String descpt, String svType, ArrayList <Pair> endpts, double processingTime){		
		super(name);	   	
		
	   	this.ServiceName = name;
	   	this.ServiceDescription = descpt;
	   	this.ServiceType = svType;
	   	this.Endpoints = endpts;
	   	this.Proc_time = processingTime;
	   	
	   	//Default output port
		addOutport("publish");	   	
	   	
		//Depending on the service provider
	   	for(int i=0; i< Endpoints.size(); i++){
	   		addInport(Endpoints.get(i).key.toString()+"In");
	   		addOutport(Endpoints.get(i).key.toString()+"Out");
	   	}	
	   	
	   	initialize();
	}

	public void initialize(){		
		Processing_time = 0;
		iterator = 0;
		RequestList = new ArrayList <ServiceCallMessage> ();
		msgQ= new GenCol.Queue();
		
		//To publish the service at the first
		holdIn("publishing", 1);
		super.initialize();
	 }

	public void  deltext(double e,message x)
	{
		Continue(e);
		
		//If this is the first request, hold it for processing time
		if(phaseIs("passive")){
			for (int i=0; i< x.getLength();i++)
				for(int j=0; j< Endpoints.size(); j++){
					if (messageOnPort(x,Endpoints.get(j).key.toString()+"In",i)){
						servicePacket = x.getValOnPort(Endpoints.get(j).key.toString()+"In", i);
						
						//Check if the message arrives at the right destination
						if(CheckDestination()){						
							holdIn("processing", Proc_time);     //Time for processing before inserting into the list 
							Processing_time = 0;                 //Processing_time is used for indicating the remained time before inserting into the list
							msgQ.add(servicePacket);
							packet = (entity)msgQ.first();	
						}
						else
							holdIn("passive", INFINITY); 
					}
				}
			//First packet to process
					
		}
		//no requests are waiting, but some requests are servicing
		else if(msgQ.isEmpty()&&!RequestList.isEmpty()){
			for (int i=0; i< x.getLength();i++)
				for(int j=0; j< Endpoints.size(); j++){
					if (messageOnPort(x,Endpoints.get(j).key.toString()+"In",i)){
						servicePacket = x.getValOnPort(Endpoints.get(j).key.toString()+"In", i);
						Processing_time = Proc_time;  //No holding time, just update processing time         
						msgQ.add(servicePacket);					
					}
				}
			//First packet to process
			packet = (entity)msgQ.first();			
		}
		else{
			for (int i=0; i< x.getLength();i++)
				for(int j=0; j< Endpoints.size(); j++){
					if (messageOnPort(x,Endpoints.get(j).key.toString()+"In",i)){
						servicePacket = x.getValOnPort(Endpoints.get(j).key.toString()+"In", i);
						msgQ.add(servicePacket);
					}
				}				
		}			
	}

	public void  deltint()
	{		
		if(phaseIs("publishing")){
			passivate();
		}
		/*
		 * there are 3 phases: processing, looping, servicing
		 * processing : after processing, remove a request from msgQ and store into the List
		 * looping    : loop the Request list
		 * servicing  : servicing each request stored in the Request list
		 */
		else if (phaseIs("processing")){
			//if a request is processed, remove from the queue and add into the servicing list			
			if(Processing_time == 0){   
				msgQ.remove();             
				RequestList.add((ServiceCallMessage)packet);				
				
				//if message queue is not empty then process it
				if(!msgQ.isEmpty())
				{
					packet = (entity)msgQ.first();
					holdIn("processing", 0);
					Processing_time = Proc_time;
				}
								
				//Then all request will be serviced
				holdIn("looping", 0);		
			}
			else
				holdIn("looping", 0);
					
		}
		else if(phaseIs("looping")){
			//if the duration for the request still remain, service this request
			if(!RequestList.isEmpty()&&(RequestList.get(iterator)).getDuration() > 1){  //So this will be the last service
				currMsg = RequestList.get(iterator);  //return value
				RequestList.get(iterator).setDuration((RequestList.get(iterator)).getDuration() - 1);//update
				holdIn("servicing", 0);
			}
			else if(!RequestList.isEmpty()&&(RequestList.get(iterator)).getDuration() == 1){  //So this will be the last service
				currMsg = RequestList.get(iterator);  //return value
				RequestList.get(iterator).setDuration((RequestList.get(iterator)).getDuration());//update
				holdIn("servicing", 0);
			}
			else
				holdIn("Done", 0);  //Skipping
		}
		else if(phaseIs("servicing") || phaseIs("Done")){			
			iterator++;
			if(iterator < RequestList.size()){				
				holdIn("looping", 0);
			}
			else{ //all services are serviced for this time, then advance 1 time unit
				iterator = 0;
				Processing_time--;
				//Before advancing time, remove requests that are serviced
				for(int i=0; i<RequestList.size(); i++){
					if(RequestList.get(i).getDuration() == 1.0)
						RequestList.remove(i);
				}
				
				//All request are serviced
				if(msgQ.isEmpty() && RequestList.isEmpty()){
					passivate();
				}
				else{
					r = new rand(System.currentTimeMillis());
					holdIn("processing", r.normal(mean, sig));   //next service time
				}
			}
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
		if(phaseIs("servicing")){
			
			// This place need to have a method to handle a service request, currently
			// there is only performService			 
			ServiceReturn = new ServiceCallMessage(currMsg.getSubscriber(), 
					                           currMsg.getPublisher(), 
					                           currMsg.getBindingInfo(),
					                           currMsg.getData(), 
					                           currMsg.getDuration());
					
			ServiceReturn.setData(performService(ServiceReturn.getData()));//Servicing 
			
			//Store the endpoint that subscriber subscribe
			String endPoint = "";
			
			//Set Receiver
			if(ServiceReturn.getBindingInfo().size() != 0){  //if there is a binding info, then send it to that
				endPoint = currMsg.getBindingInfo().get(0).value.toString();
				ServiceReturn.getBindingInfo().remove(0);
				ServiceReturn.setReceiver();    
			}
			else{
				endPoint = currMsg.getReceiver();
				ServiceReturn.setReceiver(ServiceReturn.getSubscriber());  //return to subscriber
			}
			ServiceReturn.setName(ServiceReturn.getData().value.toString());
			m.add(makeContent(endPoint + "Out", ServiceReturn));				
		}
		else if(phaseIs("publishing")){
			ServiceInfo = new ServiceInfoMessage(ServiceName, ServiceDescription, ServiceType, Endpoints);
			m.add(makeContent("publish", ServiceInfo));
		}
		return m;
	}
	
	public Pair performService(Pair data){
		return data;
	}	
	
	public String getServiceName(){
		return ServiceName;
	}
	
	public ArrayList <Pair> getEndpoints(){
		return Endpoints;
	}
	
	public boolean CheckDestination(){
		boolean isCorrect = false;
		
		ServiceCallMessage dest = (ServiceCallMessage)servicePacket;	
		
		if(dest.getBindingInfo().size() != 0){
			
				if(dest.getBindingInfo().get(0).key.toString().equalsIgnoreCase(ServiceName))
					isCorrect = true;
				else
					isCorrect = false;
		}
		else{
			if(dest.getReceiverName().equalsIgnoreCase(ServiceName)){
				isCorrect = true;
			}
			else
				isCorrect = false;
		}
		return isCorrect;
		
	}
	
}
