package GenService;

import GenCol.*;
import java.util.ArrayList;

public class ServiceCallMessage extends entity{
	protected double PacketSize = 32;  //Default Packet size 32 bytes
	protected String name;
	protected Pair receiver;
	protected String Subscriber;
	protected String Publisher;
	protected ArrayList <Pair > BindingInfo;
	protected Pair Data;              //(Data type, Data value)
	protected Double Duration;
	
	public ServiceCallMessage(String Subscriber, String Publisher, 
						  ArrayList <Pair > BindingInfo, 
						  Pair Data, 
						  Double Duration){
		
		this.Subscriber  = Subscriber;
		this.Publisher   = Publisher;
		this.BindingInfo = BindingInfo;
		this.Data        = Data;
		this.Duration    = Duration;
		this.name        = Data.key.toString();
	}
	
	public String getName(){
		return name;
	}
	
	public Pair getData(){
		return Data;
	}
	
	public Double getDuration(){
		return Duration;
	}
	
	
	public ArrayList <Pair> getBindingInfo(){
		return BindingInfo;
	}
	
	public String getReceiver(){
		return receiver.value.toString();
	}
	
	public String getReceiverName(){
		return receiver.key.toString();
	}
	
	public String getSubscriber(){
		return Subscriber;
	}	
	
	public String getPublisher(){
		return Publisher;
	}
	
	public void setData(Pair data){
		this.Data = data;
	}
	
	public void setDuration(Double val){
		Duration = val;
	}
	
	public void setName(String val){
		name = val;
	}
	
	public void setReceiver(){
		receiver = BindingInfo.get(0);
		BindingInfo.remove(0); //After setting a receiver remove that receiver from the binding list.
	}
	
	public void setReceiver(String val){
		receiver = new Pair(val, val);
	}
	
	public void setSubscriber(String val){
		BindingInfo.add(new Pair(val, val));
	}
	
	public void setSize(double val){
		PacketSize = val;
	}
	
	public double getSize(){
		return PacketSize;
	}

}
