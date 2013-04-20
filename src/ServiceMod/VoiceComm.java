
package ServiceMod;

import view.simView.*;
import model.modeling.*;
import model.simulation.*;
import view.modeling.*;
import GenCol.*;
import GenService.*;
import util.*;
import java.util.*;

	public class VoiceComm extends ServiceProvider{
	
		public VoiceComm(String name, 
						 String descpt, 
						 String svType, 
						 ArrayList <Pair> endpts, 
						 double processingTime){		
			super(name, descpt, svType, endpts, processingTime);	
		}
		
		public Pair performService(Pair data){
			double buffersize = 16;   //buffer size (Kbytes)  - 32 48 and ....user choice
			double avgNumOfDatagram = 260;  //average number of datagrams				
			double sizeOfmsgs = (avgNumOfDatagram * buffersize);
			ServiceReturn.setSize(sizeOfmsgs);
			return data;
		}
	}

	