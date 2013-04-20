
package ServiceMod;

import view.simView.*;
import model.modeling.*;
import model.simulation.*;
import view.modeling.*;
import GenCol.*;
import GenService.*;
import util.*;
import java.util.*;

	public class BasicService extends ServiceProvider{
	
		public BasicService(String name, 
							String descpt, 
							String svType, 
							ArrayList <Pair> endpts, 
							double processingTime){		
			super(name, descpt, svType, endpts, processingTime);	
		}
		
		public Pair performService(Pair data){
			//doing nothing, just return
			return data;
		}
	}

	