package dsoadExample;

import GenService.*;

public class Transducer extends ServiceTransducer{
	 
	//private logFile log;
	private double startTime =0.0;
	private double endTime=0.0;
	
	
	public Transducer(){
		super("ServiceTransd", 60);
	//	log = new logFile("pubTP");
	}
	
	public void  show_state(){
			//Override this method at the sub class
		
		double itp=compute_instantaneousTP();
		//double ctp=compute_cumTp();	
			//compute_TP(); /// computes aggregate TP so chang
	//	if(itp> 0.0)
		//    log.append(clock+","+Double.toString(itp)+"\n");
	
	}
	
	public double compute_instantaneousTP(){
		
		double tp = 0.0;
		double size =0.0;
		
		endTime = super.clock;
		
		if(((int) startTime) ==((int)endTime))
			return tp;
	    //if((endTime - startTime) < 0.75 )
		//	return tp;
		for (int i=0;i<out.size();i++) {
			double eTime = Double.parseDouble(out.get(i).getValue().toString());
			
			if(eTime >= startTime && eTime < endTime){
			 ServiceCallMessage tmsg = (ServiceCallMessage)out.get(i).getKey();
			 size +=tmsg.getSize();
			 //size ++;
			}
		}
		
		if(size>0){
			tp = ((double)size*8/(1000*1000))/(endTime - startTime);
			startTime = endTime;
		}
		return tp;
	}
	
	public double compute_final(){
	
		double dbl=0.0;
		
		for (int i = 0; i< out.size(); i++) {
			
		}
		
		return dbl;
	}
}
