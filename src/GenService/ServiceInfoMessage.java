package GenService;

import java.util.ArrayList;
import GenCol.*;
import GenCol.Pair;

public class ServiceInfoMessage extends entity{
	protected String receiver;
	protected String ServiceName;
	protected String Description;	
	protected String ServiceType; //atomic or composite
	protected ArrayList <Pair> EndPoints = new ArrayList <Pair> (); //methods exposed (Method name, parameter Type)
	
	//(Service name, Endpoint name)
	protected ArrayList <Pair> BindingInfo = new ArrayList <Pair> ();   
		
	public ServiceInfoMessage(String ServiceName, String Description, String ServiceType, ArrayList <Pair> Endpoints){
		this.ServiceName = ServiceName;
		this.Description = Description;
		this.ServiceType = ServiceType;
		this.EndPoints = Endpoints;
		this.receiver = "Broker";
	}	
	
	public void setBindingInfo(Pair val){
		//Add a servcie at the top so that this is the first service that will receive.
		BindingInfo.add(val);
	}
	
	public ArrayList <Pair> getBindingInfo(){
		return BindingInfo;
	}
	
	public void setReceiver(String val){
		this.receiver = val;
	}
	
	public String getReceiver(){
		return receiver;
	}
	
	public String getServiceName(){
		return ServiceName;
	}
	
	public String getDescription(){
		return Description;
	}
	
	public ArrayList <Pair> getEndpoints(){
		return EndPoints;
	}
	
	public String getName(){
		return ServiceName;
	}
	
	public String getServiceType(){
		return ServiceType;
	}
	
}
