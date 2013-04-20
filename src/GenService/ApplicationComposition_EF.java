package GenService;

import view.simView.*;
import model.modeling.*;
import model.simulation.*;
import view.modeling.*;

import java.awt.*;
import java.io.*;

import GenCol.*;


import java.util.*;

public class ApplicationComposition_EF extends ViewableDigraph{	
	
 	protected ArrayList <ViewableAtomic> BrokerList;
	protected ArrayList <ViewableAtomic> RouterList;
 	protected ArrayList <ViewableAtomic> PublisherList;
	protected ArrayList <ViewableAtomic> SubscriberList;
	protected ArrayList <ViewableAtomic> TransducerList;

	public ApplicationComposition_EF(){
		this("ServiceComposition");
	}

	public ApplicationComposition_EF(String nm){
	    super(nm);
	    
	    BrokerList     = new ArrayList <ViewableAtomic> ();
	    RouterList     = new ArrayList <ViewableAtomic> ();
	    PublisherList  = new ArrayList <ViewableAtomic> ();
	    SubscriberList = new ArrayList <ViewableAtomic> ();
	    TransducerList = new ArrayList <ViewableAtomic> ();	
	    
	    //This function call construct the broker and router
	    BrokerRouterConstruct();
	    
	    //Thic function call construct the user
	    SubscriberConstruct();
	    
	    //This function call construct the specfic service
	    PublisherConstruct();	    
	   
	    //This function call construct the coupling
	    CompositionConstruct();
	}
	public void CompositionConstruct(){		
		
		this.addOutport(BrokerList.get(0).getName()+"In");
		this.addOutport(BrokerList.get(0).getName()+"Out");
		this.addOutport(RouterList.get(0).getName()+"In");
		this.addOutport(RouterList.get(0).getName()+"Out");
		
		add(BrokerList.get(0));
		add(RouterList.get(0));
	   
	    
	    for(int i=0; i<SubscriberList.size(); i++){
	    	add(SubscriberList.get(i));
	    	this.addOutport(SubscriberList.get(i).getName()+"In");
			this.addOutport(SubscriberList.get(i).getName()+"Out");
	    	ServiceClient currSubscriber = (ServiceClient)SubscriberList.get(i);	    	
	    	RouterList.get(0).addOutport(currSubscriber.getName());
	    	BrokerList.get(0).addOutport(currSubscriber.getName());			
			
			addCoupling(SubscriberList.get(i), "request", RouterList.get(0), "in");
			addCoupling(SubscriberList.get(i), "request", this, SubscriberList.get(i).getName()+"Out");
			addCoupling(SubscriberList.get(i), "request", this, RouterList.get(0).getName()+"In");
			addCoupling(SubscriberList.get(i), "lookup", BrokerList.get(0), "subscribe");
			addCoupling(SubscriberList.get(i), "lookup", this, BrokerList.get(0).getName()+"In");
			addCoupling(RouterList.get(0), currSubscriber.getName(), SubscriberList.get(i), currSubscriber.getPort());
			addCoupling(RouterList.get(0), currSubscriber.getName(), this, SubscriberList.get(i).getName()+"In");
			addCoupling(RouterList.get(0), currSubscriber.getName(), this, RouterList.get(0).getName()+"Out");
			addCoupling(BrokerList.get(0), currSubscriber.getName(), SubscriberList.get(i), "found");
			addCoupling(BrokerList.get(0), currSubscriber.getName(), this, BrokerList.get(0).getName()+"Out");
	    }
	    
	    for(int j=0; j<PublisherList.size(); j++){
	    	add(PublisherList.get(j));
	    	this.addOutport(PublisherList.get(j).getName()+"In");
	    	this.addOutport(PublisherList.get(j).getName()+"Out");
	    	ServiceProvider currPublisher = (ServiceProvider)PublisherList.get(j);
	    	for(int k=0; k < currPublisher.getEndpoints().size(); k++){
	    		RouterList.get(0).addOutport(currPublisher.getEndpoints().get(k).key.toString());	    		
	    		addCoupling(RouterList.get(0), currPublisher.getEndpoints().get(k).key.toString(), PublisherList.get(j), currPublisher.getEndpoints().get(k).key.toString()+"In");
	    		addCoupling(RouterList.get(0), currPublisher.getEndpoints().get(k).key.toString(), this, RouterList.get(0).getName()+"Out");
	    		addCoupling(RouterList.get(0), currPublisher.getEndpoints().get(k).key.toString(), this, PublisherList.get(j).getName()+"In");
	    		addCoupling(PublisherList.get(j), currPublisher.getEndpoints().get(k).key.toString()+"Out", RouterList.get(0), "in");
	    		addCoupling(PublisherList.get(j), currPublisher.getEndpoints().get(k).key.toString()+"Out", this, RouterList.get(0).getName()+"In");
	    		addCoupling(PublisherList.get(j), currPublisher.getEndpoints().get(k).key.toString()+"Out", this, PublisherList.get(j).getName()+"Out");
	    		addCoupling(PublisherList.get(j), "publish", BrokerList.get(0), "publish");
	    		addCoupling(PublisherList.get(j), "publish", this, BrokerList.get(0).getName()+"In");
	    	}
	    }
	    
	}
	
	public void BrokerRouterConstruct(){}
	public void PublisherConstruct(){}	
	public void SubscriberConstruct(){}	
	
}
