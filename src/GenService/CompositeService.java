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

public class CompositeService extends ViewableDigraph{	
 	
	/**
	 * The list of each component
	 */
	protected ArrayList <ServiceProvider>  		PublisherList;
 	protected ArrayList <CompositeService> 	CoupledPublishersList;
	protected ArrayList <ServiceTransducer>  		TransducerList;
	
	/**
	 * Endpoints for this coupled service
	 */
	protected ArrayList <Pair> Endpoints;
	
	protected ServiceRouter Router;	
	protected double bandwidth = 10;    //bandwidth for the Router: 10Mbps

	public CompositeService(){
		this("Composite Service");
	}

	public CompositeService(String nm){
	    super(nm);    
	    
	    //Router construction
	    Router = new ServiceRouter("Router", bandwidth);
	    Router.setBackgroundColor(Color.PINK);
	    
	    //Publisher construction
	    PublisherList  			= new ArrayList <ServiceProvider>        ();
	    CoupledPublishersList  	= new ArrayList <CompositeService>();
	    TransducerList          = new ArrayList <ServiceTransducer>       ();
	    
	    //Endpoints for this composite servcie
	    Endpoints               =  new ArrayList <Pair>            		  ();  	   	
	    
	    /**
	     * This function call construct the list of endpoints
	     */
	    EndpointsConstruct();
	    
	    /**
	     * This function call construct the list of publishers
	     */
	    PublisherConstruct();
	    
	    /**
	     * This function call construct the list of transducers
	     */
	    TransducerConstruct();
	    
	    /**
	     * This function call construct the list of composite services
	     */
	    CompositeConstruction();
	    
	    /**
	     * This function call construct the coupling of each component
	     */
	    CouplingConstruct();
	}
	
	/**
	 * Construction of each component is a role of service developer
	 */
	public void EndpointsConstruct(){}
	public void PublisherConstruct(){}	
	public void TransducerConstruct(){}
	public void CompositeConstruction(){}
	
	/**
	 * Default coupling is defined here
	 */
	public void CouplingConstruct(){
		
		addOutport("publish");		
		
		//Add a router for composite service
		add(Router);
		add(TransducerList.get(0));		
			   	
		//Coupling Atomic services with the router and the coupled service itself
	   	for(int i=0; i< Endpoints.size(); i++){
	   		
	   		Router.addOutport(Endpoints.get(i).key.toString());	   		
	   		addCoupling(Router, Endpoints.get(i).key.toString(), this, Endpoints.get(i).key.toString()+"Out");
	   		addCoupling(Router, Endpoints.get(i).key.toString(), TransducerList.get(0), "out");
	   		
	   		//Depending on the service providers
	   		addInport(Endpoints.get(i).key.toString()+"In");
	   		addOutport(Endpoints.get(i).key.toString()+"Out");
	   		
		    for(int j=0; j< PublisherList.size(); j++){
		    	
		    	ServiceProvider currPublisher = (ServiceProvider)PublisherList.get(j);
		    	ServiceTransducer currTransducer = (ServiceTransducer)TransducerList.get(j+1);
		    	
		    	add(currPublisher);
		    	add(currTransducer);		    	
		    	
		    	addCoupling(this, this.Endpoints.get(i).key.toString()+"In", currTransducer, "in");   	
		    	addCoupling(currPublisher, "publish", this, "publish");
		    	
		    	for(int k=0; k < currPublisher.getEndpoints().size(); k++){	    		
		    		
		    		Router.addOutport(currPublisher.getEndpoints().get(k).key.toString());
		    		
		    		addCoupling(this, this.Endpoints.get(i).key.toString()+"In", currPublisher, currPublisher.getEndpoints().get(k).key.toString()+"In");
		    		addCoupling(Router, currPublisher.getEndpoints().get(k).key.toString(), currPublisher, currPublisher.getEndpoints().get(k).key.toString()+"In");
		    		addCoupling(Router, currPublisher.getEndpoints().get(k).key.toString(), currTransducer, "in");
		    		addCoupling(Router, currPublisher.getEndpoints().get(k).key.toString(), TransducerList.get(0), "out");
		    		
		    		addCoupling(currPublisher, currPublisher.getEndpoints().get(k).key.toString()+"Out", Router, "in");
		    		addCoupling(currPublisher, currPublisher.getEndpoints().get(k).key.toString()+"Out", TransducerList.get(0), "in");
		    		addCoupling(currPublisher, currPublisher.getEndpoints().get(k).key.toString()+"Out", currTransducer, "out");
		    	}
		    }
		    
		    //Coupling sub coupled services with the router and the coupled service itself
		    for(int j=0; j< CoupledPublishersList.size(); j++){
		    	
		    	CompositeService currCoupled = (CompositeService)CoupledPublishersList.get(j);		    	
		    	add(currCoupled);
		    			    	
		    	addCoupling(currCoupled, "publish", this, "publish");
		    	
		    	for(int k = 0; k < currCoupled.getEndpoints().size(); k++){
		    		Router.addOutport(currCoupled.getEndpoints().get(k).key.toString());
		    		addCoupling(this, this.Endpoints.get(i).key.toString()+"In", currCoupled, currCoupled.getEndpoints().get(k).key.toString()+"In");
		    		
		    		addCoupling(Router, currCoupled.getEndpoints().get(k).key.toString(), currCoupled, currCoupled.getEndpoints().get(k).key.toString()+"In");
		    		addCoupling(Router, currCoupled.getEndpoints().get(k).key.toString(), TransducerList.get(0), "out");
		    		
		    		addCoupling(currCoupled, currCoupled.getEndpoints().get(k).key.toString()+"Out", Router, "in");
		    		addCoupling(currCoupled, currCoupled.getEndpoints().get(k).key.toString()+"Out", TransducerList.get(0), "in");
		    	}		    	
		    }
	   	}		
	}	
	
	/**
	 * This function return endpoints
	 * @return endpoints
	 */
	public ArrayList <Pair> getEndpoints(){
		return Endpoints;
	}
	
}
