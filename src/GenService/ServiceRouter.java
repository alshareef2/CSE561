package GenService;

import view.simView.*;
import model.modeling.*;
import model.simulation.*;
import view.modeling.*;
import GenCol.*;
import util.*;

/** 
 * @author skim109
 * @version 1.0
 * The network receives an input and send output out after taking certain amount of time
 * currently 0.2 +- 0.1
 */

public class ServiceRouter extends ViewableAtomic{

	protected double trasmitionTime;   //the time to transmit a packet, this will be a random number 
	protected double network_traffic;  //How many pakcets can handle at once - bandwidth
	protected double mean = 0.2;       //mean for transmission delay
	protected double sig = 0.02;        //sigma of mean for transmission delay
	protected String outputPort;       //To find a receiver
	protected Queue inQ;
	protected entity packet, currPacket;
	protected rand r;
	
	public ServiceRouter() 
	{
		this("Network", 0);
	}

	public ServiceRouter(String name, double traffic){		
		super(name);	   	
	   	addInport("in");	         //All inputs come in thru this port    	
	   	network_traffic = traffic; 	 //Bandwidth	   	
	}

	public void initialize(){
		trasmitionTime = 0; 
		inQ= new Queue();
		passivate();
		super.initialize();
	 }

	public void  deltext(double e,message x)
	{
		Continue(e);
		
		// add the packets into the Queue, then it will send it in order		
		if(phaseIs("passive")){
			for (int i=0; i< x.getLength();i++)
				if (messageOnPort(x,"in",i)){
					packet = x.getValOnPort("in", i);
					r = new rand(System.currentTimeMillis()); //for random generator
					//trasmitionTime = r.normal(mean, sig);      //normal distribution 0.2 +- 0.02
					holdIn("transmitting", trasmitionTime);
					inQ.add(packet);
				}
			currPacket = (entity)inQ.first();
		}
		else
		{
			for (int i=0; i< x.getLength();i++)
				if (messageOnPort(x,"in",i)){
					packet = x.getValOnPort("in", i);				
					inQ.add(packet);
				}
		}
	}

	public void  deltint()
	{
		inQ.remove();
		
		if(!inQ.isEmpty()){				
			currPacket = (entity)inQ.first();
			r = new rand(System.currentTimeMillis()); //for random generator
			//trasmitionTime = r.normal(mean, sig);      //normal distribution 0.2 +- 0.02
			holdIn("transmitting", trasmitionTime);
		}
		else 
			passivate();
		
	}	
	public void deltcon(double e,message x)
	{
	   deltext(e,x);
	   deltint();
	}
	
	public message  out( )
	{
		message  m = new message();
		outputPort = ((ServiceCallMessage)currPacket).getReceiver();  //Receiver port
		
		//If there is no output port for this message, use service name as default
		if(!this.getOutportNames().contains(outputPort))
			outputPort = ((ServiceCallMessage)currPacket).getPublisher();
		
		if(phaseIs("transmitting"))
			m.add(makeContent(outputPort, currPacket));
		
		return m;
	}	
		
}
