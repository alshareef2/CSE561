package GenService;

import view.simView.*;
import model.modeling.*;
import model.simulation.*;
import view.modeling.*;
import java.awt.*;
import java.io.*;
import GenCol.*;
import GenService.*;

import java.util.*;

/**
 * This class is to build the most high level of SOA model, 
 * including Broker
 * @author Sungung
 *
 */
public class ApplicationComposition extends ViewableDigraph{	
 	
	/**
	 * Construct the list of each component
	 */
 	protected ArrayList <ServiceBroker>  			BrokerList;
	protected ArrayList <ServiceRouter>  			RouterList;
 	protected ArrayList <ServiceProvider>  		PublisherList;
 	protected ArrayList <CompositeService> 	CoupledPublishersList;
	protected ArrayList <ServiceClient>  		SubscriberList;
	protected ArrayList <ServiceTransducer>  		TransducerList;

	public ApplicationComposition(){
		this("ServiceComposition");
	}

	public ApplicationComposition(String nm){
	    super(nm);
	    
	    BrokerList     			= new ArrayList <ServiceBroker>           ();
	    RouterList     			= new ArrayList <ServiceRouter>           ();
	    PublisherList  			= new ArrayList <ServiceProvider>        ();
	    CoupledPublishersList  	= new ArrayList <CompositeService>();
	    SubscriberList 			= new ArrayList <ServiceClient>       ();
	    TransducerList 			= new ArrayList <ServiceTransducer>       ();	    
	    
	    //This function call construct the list of brokers and routers
	    BrokerRouterConstruct();	    
	    
	    //This function call construct the list of subscribers
	    SubscriberConstruct();	    
	  
	    //This function call construct the list of publishers
	    PublisherConstruct();	    
	    
	    //This function call construct the list of composite services
	    CompositeConstruct();
	    	    
	    //This function call construct the list of transducers
	    TransducerConstruct();
	    
	    //This function call construct the coupling of each component
	    CouplingConstruct();
	}
	
	/**
	 * Construction of each component is a role of service developer
	 */
	public void BrokerRouterConstruct(){}
	public void PublisherConstruct(){}
	public void CompositeConstruct(){}
	public void SubscriberConstruct(){}
	public void TransducerConstruct(){}
	
	/**
	 * Default coupling is defined here
	 */
	public void CouplingConstruct(){		
				
		/**
		 * Add a Broker and a Router
		 * For now, we assume there are only one broker and one router
		 */
		
		add(BrokerList.get(0));
		add(RouterList.get(0));	
		
		/** 
		 * add transducers for broker and Router
		 * I assume that these two transducers are always at Transducerlist[0] and Transducerlist[1]
		 */
		
		add(TransducerList.get(0));   //Broker
		addCoupling(BrokerList.get(0), "active", TransducerList.get(0), "out");
		add(TransducerList.get(1));   //Router
	   	
		/**
		 * Coupling subscribers with other components
		 */
	    for(int i=0; i<SubscriberList.size(); i++){
	    	
	    	//Current subscriber and transducer
	    	ServiceClient currSubscriber = SubscriberList.get(i);
	    	ServiceTransducer currTransducer = TransducerList.get(i+2);
	    	
	    	add(currSubscriber);
	    	add(currTransducer);	    		    	
			
	    	RouterList.get(0).addOutport(currSubscriber.getName());
	    	BrokerList.get(0).addOutport(currSubscriber.getName());			
			
			addCoupling(currSubscriber, "request", RouterList.get(0), "in");
			addCoupling(currSubscriber, "request", TransducerList.get(1), "in");
			addCoupling(currSubscriber, "request", currTransducer, "out");
			
			addCoupling(currSubscriber, "lookup", BrokerList.get(0), "subscribe");
			addCoupling(currSubscriber, "lookup", TransducerList.get(0), "in");
			
			addCoupling(RouterList.get(0), currSubscriber.getName(), currSubscriber, currSubscriber.getPort());
			addCoupling(RouterList.get(0), currSubscriber.getName(), TransducerList.get(1), "out");
			addCoupling(RouterList.get(0), currSubscriber.getName(), currTransducer, "in");
			
			addCoupling(BrokerList.get(0), currSubscriber.getName(), currSubscriber, "found");
			addCoupling(BrokerList.get(0), currSubscriber.getName(), TransducerList.get(0), "out");
	    }	    
	    
	    /**
		 * Coupling publishers with other components
		 */
	    for(int j=0; j<PublisherList.size(); j++){
	    	
	    	ServiceProvider currPublisher = PublisherList.get(j);
	    	ServiceTransducer currTransducer = TransducerList.get(2+SubscriberList.size()+j);
	    	
	    	add(currPublisher);
	    	add(currTransducer);
	    	
	    	for(int k=0; k < currPublisher.getEndpoints().size(); k++){
	    		RouterList.get(0).addOutport(currPublisher.getEndpoints().get(k).key.toString());
	    		
	    		addCoupling(RouterList.get(0), currPublisher.getEndpoints().get(k).key.toString(), currPublisher, currPublisher.getEndpoints().get(k).key.toString()+"In");
	    		addCoupling(RouterList.get(0), currPublisher.getEndpoints().get(k).key.toString(), currTransducer, "in");
	    		addCoupling(RouterList.get(0), currPublisher.getEndpoints().get(k).key.toString(), TransducerList.get(1), "out");
	    		
	    		addCoupling(currPublisher, currPublisher.getEndpoints().get(k).key.toString()+"Out", RouterList.get(0), "in");
	    		addCoupling(currPublisher, currPublisher.getEndpoints().get(k).key.toString()+"Out", TransducerList.get(1), "in");
	    		addCoupling(currPublisher, currPublisher.getEndpoints().get(k).key.toString()+"Out", currTransducer, "out");
	    		
	    		addCoupling(currPublisher, "publish", BrokerList.get(0), "publish");
	    		addCoupling(currPublisher, "publish", TransducerList.get(0), "in");
	    	}
	    }
	    
	    /**
		 * Coupling coupled publishers with other components
		 */
	    for(int j=0; j< CoupledPublishersList.size(); j++){
	    	
	    	CompositeService currCoupled = CoupledPublishersList.get(j);		    	
	    	add(currCoupled);
	    	
	    	addCoupling(currCoupled, "publish", BrokerList.get(0), "publish");
	    	addCoupling(currCoupled, "publish", TransducerList.get(0), "in");
	    	
	    	for(int k = 0; k < currCoupled.getEndpoints().size(); k++){
	    		RouterList.get(0).addOutport(currCoupled.getEndpoints().get(k).key.toString());
	    		
	    		addCoupling(RouterList.get(0), currCoupled.getEndpoints().get(k).key.toString(), currCoupled, currCoupled.getEndpoints().get(k).key.toString()+"In");
	    		addCoupling(RouterList.get(0), currCoupled.getEndpoints().get(k).key.toString(), TransducerList.get(1), "out");
	    		addCoupling(currCoupled, currCoupled.getEndpoints().get(k).key.toString()+"Out", RouterList.get(0), "in");
	    		addCoupling(currCoupled, currCoupled.getEndpoints().get(k).key.toString()+"Out", TransducerList.get(1), "in");
	    	}	  	
	    }	    
	}
}
