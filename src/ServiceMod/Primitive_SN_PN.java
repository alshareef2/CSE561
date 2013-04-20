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
	
	public class Primitive_SN_PN extends ApplicationComposition{
		
		public final static double observation = 70;
		public ServiceBroker Broker;
		public ServiceRouter Router;
		
		public Primitive_SN_PN(){
			super("Voice Communication Service");
		}
		
		public void BrokerRouterConstruct(){
			
			//attributes 
			double available = 60;     //available time for broker
			double startTime = 0.5; 
		 	double bandwidth = 10;      //bandwidth for the network or router
            
		 	//Ceate an unique component
			Broker = new ServiceBroker("Broker", available, startTime);
			Router = new ServiceRouter("Router Link", bandwidth);			
			Broker.setBackgroundColor(Color.YELLOW);
			Router.setBackgroundColor(Color.PINK);
			
			BrokerList.add(Broker);
			RouterList.add(Router);			
		}
		
		public void PublisherConstruct(){
			
			ArrayList <Pair> Endpoints = new ArrayList <Pair> ();
	 		
			Endpoints.add(new Pair("qRate", "Double"));		
			VoiceComm Service1 = 
				new VoiceComm("VoiceComm", "Voice Communication", "Atomic", Endpoints, 1);
			Service1.setBackgroundColor(Color.CYAN);
			PublisherList.add(Service1);
			
			
			Endpoints = new ArrayList <Pair> ();
			Endpoints.add(new Pair("CityByZip", "Double"));		
			USZipService Service2 = 
				new USZipService("USZip", "City by Zip Service", "Atomic", Endpoints, 1);
			Service2.setBackgroundColor(Color.CYAN);
			PublisherList.add(Service2);
			
			Endpoints = new ArrayList <Pair> ();	 		
			Endpoints.add(new Pair("ResortByCity", "String"));		
			ResortService Service3 = 
				new ResortService("Resort", "Resort by City Service", "Atomic", Endpoints, 1);
			Service3.setBackgroundColor(Color.CYAN);
			PublisherList.add(Service3);				
		}
		
		public void SubscriberConstruct(){
			
			//Construct ServiceLookup information: The list of service to subscribe
			ArrayList <ServiceLookupMessage> lookupList = new ArrayList <ServiceLookupMessage> ();
			lookupList.add(new ServiceLookupMessage("USZip", "CityByZip", new Pair("double", 85281), 1));			
			ServiceClient Subscriber1 = new ServiceClient("Subscriber1", lookupList, 0.1);
			
			lookupList = new ArrayList <ServiceLookupMessage> ();
			lookupList.add(new ServiceLookupMessage("Resort", "ResortByCity", new Pair("String", "tempe"), 1));
			ServiceClient Subscriber2 = new ServiceClient("Subscriber2", lookupList, 0.1);
			
			lookupList = new ArrayList <ServiceLookupMessage> ();
			lookupList.add(new ServiceLookupMessage("VoiceComm", "qRate", new Pair("Hz", 220500), 60));
			ServiceClient Subscriber3 = new ServiceClient("Subscriber3", lookupList, 0.1);
			
			Subscriber1.setBackgroundColor(Color.GREEN);
			Subscriber2.setBackgroundColor(Color.GREEN);
			Subscriber3.setBackgroundColor(Color.GREEN);
			
			//Construct the subscriber list
			SubscriberList.add(Subscriber1);
			SubscriberList.add(Subscriber2);
			SubscriberList.add(Subscriber3);			
		}  
		
		public void TransducerConstruct(){
			BrokerTransd BroTrans  =  new BrokerTransd("BrokerTransd", observation);
			RouterTransd NecTrans  =  new RouterTransd("RouterTransd", observation);
			SubscriberTransd SubTrans1 =  new SubscriberTransd("Sub1Transd", observation);
			SubscriberTransd SubTrans2 =  new SubscriberTransd("Sub2Transd", observation);
			SubscriberTransd SubTrans3 =  new SubscriberTransd("Sub3Transd", observation);
			PublisherTransd PubTrans1 =  new PublisherTransd("VoiceCommTransd", observation);
			PublisherTransd PubTrans2 =  new PublisherTransd("USZipTransd", observation);
			PublisherTransd PubTrans3 =  new PublisherTransd("ResortTransd", observation);
			
			//Always the same order: Broker >> Network >> Subscriber >> Publisher
			TransducerList.add(BroTrans);
			TransducerList.add(NecTrans);
			TransducerList.add(SubTrans1);
			TransducerList.add(SubTrans2);
			TransducerList.add(SubTrans3);
			TransducerList.add(PubTrans1);
			TransducerList.add(PubTrans2);
			TransducerList.add(PubTrans3);
		}   
   
   
    
    /**
     * Automatically generated by the SimView program.
     * Do not edit this manually, as such changes will get overwritten.
     */
    public void layoutForSimView()
    {
        preferredSize = new Dimension(976, 640);
        ((ViewableComponent)withName("Router Link")).setPreferredLocation(new Point(357, 239));
        ((ViewableComponent)withName("Subscriber3")).setPreferredLocation(new Point(11, 374));
        ((ViewableComponent)withName("BrokerTransd")).setPreferredLocation(new Point(347, 35));
        ((ViewableComponent)withName("USZipTransd")).setPreferredLocation(new Point(694, 252));
        ((ViewableComponent)withName("Broker")).setPreferredLocation(new Point(319, 90));
        ((ViewableComponent)withName("Resort")).setPreferredLocation(new Point(641, 437));
        ((ViewableComponent)withName("Sub2Transd")).setPreferredLocation(new Point(31, 204));
        ((ViewableComponent)withName("ResortTransd")).setPreferredLocation(new Point(694, 381));
        ((ViewableComponent)withName("VoiceComm")).setPreferredLocation(new Point(659, 173));
        ((ViewableComponent)withName("VoiceCommTransd")).setPreferredLocation(new Point(682, 117));
        ((ViewableComponent)withName("RouterTransd")).setPreferredLocation(new Point(354, 180));
        ((ViewableComponent)withName("Subscriber1")).setPreferredLocation(new Point(14, 140));
        ((ViewableComponent)withName("USZip")).setPreferredLocation(new Point(654, 306));
        ((ViewableComponent)withName("Sub1Transd")).setPreferredLocation(new Point(34, 85));
        ((ViewableComponent)withName("Subscriber2")).setPreferredLocation(new Point(14, 258));
        ((ViewableComponent)withName("Sub3Transd")).setPreferredLocation(new Point(30, 322));
    }
}
