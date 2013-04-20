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

public class TransducerComposition extends ViewableDigraph{	
	protected ArrayList <ViewableAtomic> TransducerList;

	public TransducerComposition(){
		this("ServiceComposition");
	}

	public TransducerComposition(String nm){
	    super(nm);    
	    
	    TransducerList = new ArrayList <ViewableAtomic> ();	  
	    
	    //This function call construct the transducers
	    TransducerConstruct();
	    
	    //This function call construct the coupling
	    CompositionConstruct();
	}
	public void CompositionConstruct(){		
		for(int i=0; i<TransducerList.size(); i++){
			add(TransducerList.get(i));
			this.addInport(TransducerList.get(i).getName()+"In");
			addCoupling(this, TransducerList.get(i).getName()+"In", TransducerList.get(i), "in");
			this.addInport(TransducerList.get(i).getName()+"Out");
			addCoupling(this, TransducerList.get(i).getName()+"Out", TransducerList.get(i), "out");			
		}
	}	
	public void TransducerConstruct(){}
	
}
