	package ServiceMod;
	
	import view.simView.*;
	import model.modeling.*;
	import model.simulation.*;
	import view.modeling.*;
	import java.awt.*;
	import java.io.*;

	import GenCol.*;
	import GenService.*;	

import java.util.*;
	
	public class FedexRateByZipService extends CompositeService{
		
		public final static double observation = 70;
		
		
		public FedexRateByZipService(){
			super("Fedex Rate By Zip");
		}	
		
		public void EndpointsConstruct(){
			
			Endpoints.add(new Pair("FedexRate", "Double"));
			
		}
		
		public void PublisherConstruct(){			
			
			ArrayList <Pair> Endpoints = new ArrayList <Pair> ();	 		
			Endpoints.add(new Pair("CityByZip", "Double"));		
			USZipService Service1 = 
				new USZipService("USZip", "City by Zip Service", "Atomic", Endpoints, 1);
			Service1.setBackgroundColor(Color.CYAN);
			//Construct the publisher list
			PublisherList.add(Service1);
			
			Endpoints = new ArrayList <Pair> ();	 		
			Endpoints.add(new Pair("FedexRate", "Double"));		
			GetFedexRateService Service2 = 
				new GetFedexRateService("FedexRate", "Fedex Rate By Zip", "Atomic", Endpoints, 1);
			Service2.setBackgroundColor(Color.CYAN);
			PublisherList.add(Service2);
				
		}
		
		
		public void TransducerConstruct(){
			RouterTransd routerTrans  =  new RouterTransd("RouterTransd", observation);
			PublisherTransd PubTrans1 =  new PublisherTransd("USZipTransd", observation);
			PublisherTransd PubTrans2 =  new PublisherTransd("FedexTransd", observation);
			//Add a transducer for each publisher
			
			TransducerList.add(routerTrans);
			TransducerList.add(PubTrans1);
			TransducerList.add(PubTrans2);
		}
		
		public void CompositeConstruction(){
			//FedexRateByZipServices Service2 = new FedexRateByZipServices();
			//CoupledPublishersList.add(Service2);
			
		}
   
   
    /**
     * Automatically generated by the SimView program.
     * Do not edit this manually, as such changes will get overwritten.
     */
    public void layoutForSimView()
    {
        preferredSize = new Dimension(597, 253);
        ((ViewableComponent)withName("RouterTransd")).setPreferredLocation(new Point(136, 25));
        ((ViewableComponent)withName("Router")).setPreferredLocation(new Point(140, 77));
        ((ViewableComponent)withName("USZip")).setPreferredLocation(new Point(2, 183));
        ((ViewableComponent)withName("FedexRate")).setPreferredLocation(new Point(204, 189));
        ((ViewableComponent)withName("FedexTransd")).setPreferredLocation(new Point(256, 133));
        ((ViewableComponent)withName("USZipTransd")).setPreferredLocation(new Point(1, 125));
    }
}
