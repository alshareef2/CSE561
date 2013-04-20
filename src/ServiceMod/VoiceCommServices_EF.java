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

public class VoiceCommServices_EF extends ViewableDigraph{	
	
	public VoiceCommServices_EF(){
	    super("VoiceComm Services with Transducers");
	    
	   CompositionConstruct();
	    
	}
	public void CompositionConstruct(){/*
		
		 ViewableDigraph ConstServices = new VoiceCommServices_Transd("VoiceComm Services");
		 ViewableDigraph ConstTransducers = new VoiceCommTransducers("VoiceComm Transducers");
		
		 add(ConstServices);
		 add(ConstTransducers);
		    
		 addCoupling(ConstServices, "BrokerIn", ConstTransducers, "BrokerTransdIn");
		 addCoupling(ConstServices, "BrokerOut", ConstTransducers, "BrokerTransdOut");
		 addCoupling(ConstServices, "RouterIn", ConstTransducers, "RouterTransdIn");
		 addCoupling(ConstServices, "RouterOut", ConstTransducers, "RouterTransdOut");
		 addCoupling(ConstServices, "Sub1In", ConstTransducers, "Sub1TransdIn");
		 addCoupling(ConstServices, "Sub1Out", ConstTransducers, "Sub1TransdOut");
		 addCoupling(ConstServices, "Sub2In", ConstTransducers, "Sub2TransdIn");
		 addCoupling(ConstServices, "Sub2Out", ConstTransducers, "Sub2TransdOut");
		 addCoupling(ConstServices, "Sub3In", ConstTransducers, "Sub3TransdIn");
		 addCoupling(ConstServices, "Sub3Out", ConstTransducers, "Sub3TransdOut");
		 addCoupling(ConstServices, "VoiceCommIn", ConstTransducers, "VoiceCommTransdIn");
		 addCoupling(ConstServices, "VoiceCommOut", ConstTransducers, "VoiceCommTransdOut");
		
	*/
	    
	}
    /**
     * Automatically generated by the SimView program.
     * Do not edit this manually, as such changes will get overwritten.
     */
    public void layoutForSimView()
    {
        preferredSize = new Dimension(984, 641);
        ((ViewableComponent)withName("VoiceComm Services")).setPreferredLocation(new Point(85, 30));
        ((ViewableComponent)withName("VoiceComm Transducers")).setPreferredLocation(new Point(115, 332));
    }
}
