	package ServiceMod.Scalability;
	
	import view.simView.*;
import ServiceMod.*;
	import model.modeling.*;
	import model.simulation.*;
	import view.modeling.*;
	import java.awt.*;
	import java.io.*;
	import GenCol.*;
	import GenService.*;	

import java.util.*;
	
	public class Scalability_Test_SN_P1 extends ApplicationComposition{
		
		public final static double observation = 70;
		public final static int numOfsub = 100;
		
		public ServiceBroker Broker;
		public ServiceRouter Router;
		
		public Scalability_Test_SN_P1(){
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
		}	
		
		
		public void SubscriberConstruct(){
			
			ArrayList <ServiceLookupMessage> lookupList;
			
			for(int i=0; i< numOfsub; i++){
				lookupList = new ArrayList <ServiceLookupMessage> ();
				lookupList.add(new ServiceLookupMessage("VoiceComm", "qRate", new Pair("Hz", 220500), 60));
				ServiceClient temp = new ServiceClient("Subscriber"+i, lookupList, 0.1);
				SubscriberList.add(temp);
			}
			
					
		}  
		
		public void TransducerConstruct(){
			BrokerTransd BroTrans  =  new BrokerTransd("BrokerTransd", observation);
			RouterTransd NecTrans  =  new RouterTransd("RouterTransd", observation);
			//Always the same order: Broker >> Network >> Subscriber >> Publisher
			TransducerList.add(BroTrans);
			TransducerList.add(NecTrans);
			
			for(int i=0; i<numOfsub; i++){
				SubscriberTransd temp =  new SubscriberTransd("SubTransd"+i, observation);
				TransducerList.add(temp);
			}
			
			PublisherTransd PubTrans1 =  new PublisherTransd("VoiceCommTransd", observation);
			
			TransducerList.add(PubTrans1);
		}      
   
  
}
