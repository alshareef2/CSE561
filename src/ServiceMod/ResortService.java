package ServiceMod;

import view.simView.*;
import model.modeling.*;
import model.simulation.*;
import view.modeling.*;
import GenCol.*;
import GenService.*;
import util.*;
import java.util.*;

public class ResortService extends ServiceProvider{	

	public ResortService(String name, String descpt, String svType, ArrayList <Pair> endpts, int processingTime){		
		super(name, descpt, svType, endpts, processingTime);
	}
	
	public Pair performService(Pair data){
		double sizeOfmsgs = 32;
		Pair returnVal = new Pair();
		
		//if argument is tempe, 
		//then return pheonix resort
		if(data.value.toString().equals("tempe")){
			returnVal.key = "String";
			returnVal.value = "Pheonix Resort";
		}
		else{
			returnVal.key = "String";
			returnVal.value = "No Found";
		}
		
		ServiceReturn.setSize(sizeOfmsgs);
		return returnVal;
	}
}
