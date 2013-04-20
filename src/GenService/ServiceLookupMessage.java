package GenService;

import GenCol.*;

public class ServiceLookupMessage extends entity{
	protected String Subscriber;
	protected String ServiceName;
	protected String Endpoint;
	protected Pair data;
	protected Double duration;
	
	public ServiceLookupMessage(String ServiceName, 
						 String Endpoint, 
						 Pair data, 
						 double duration){
		this.ServiceName = ServiceName;
		this.Endpoint = Endpoint;
		this.data = data;
		this.duration = duration;
	}
	
	public String getServiceName(){
		return ServiceName;
	}
	
	public String getSubscriber(){
		return Subscriber;
	}
	
	public void setSubscriber(String val){
		Subscriber = val;
	}
	
	public String getEndpoint(){
		return Endpoint;
	}
	
	public Pair getData(){
		return data;
	}
	
	public double getDuration(){
		return duration;
	}
	
	public String getName(){
		return Endpoint;
	}

}
