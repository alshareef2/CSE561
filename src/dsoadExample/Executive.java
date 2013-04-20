package dsoadExample;

import model.modeling.message;
import view.modeling.*;
import GenService.*;


public class Executive extends ViewableAtomic{

	int state;
	//private logFile exLog;
	public Executive (){
		super("Executor");
		//exLog = new logFile("exLog");
	}
	
	
	public void initialize(){
	    super.initialize();
	    holdIn("creating",1);
	    state =0;
	    //passivate();
	 }
	
	public void  deltext(double e,message x)
	  {
	    Continue(e);
	  }
	
	public void  deltint( )
	  {
		//double Time = this.getSimulator().getTL();
		state ++;
		
		if(state  < 4)
			holdIn("creating",10);
		else if(state < 5 ) 
			holdIn("removing",10);
		else 
		   passivate();
	  }
	public message  out( )
	  {

	    message  m = new message();

	    if (phaseIs("creating")){
		    switch (state){  
		    case 0:
		        addServiceEncrypted(60);
		        break;
		    case 1:
		    	addServiceEncrypted(30);
		    	addServiceEncrypted(30);
		    	addServiceEncrypted(60);
		    	break;
		    default:
		    		break;
		    }
	    }
	    else if(phaseIs("removing")){
	    	    removeService();
	    }
	    
	    return m;
	  }

	
	  private void addService(double duration){
		  
		  
		 VoiceCommunicationSystem parent = (VoiceCommunicationSystem) getParent();
		  
	//	 exLog.append(parent.getSimulator().getTL()+","+"client"+parent.subCount+","+"created"+"\n");
		 ServiceClient sb = parent.addSubscriber(duration);
		 addModel(sb);
		 
		 // subcscriber to router
		 addCoupling(sb.getName(),"request",parent.router.getName(),"in");
		 addOutport(parent.router.getName(),sb.getName());
		 addCoupling(parent.router.getName(),sb.getName(),sb.getName(),"service");
		 
		 // subcscriber to broker
		 addCoupling(parent.broker.getName(),sb.getName(),sb.getName(),"found");
		 addOutport(parent.broker.getName(),sb.getName());
		 addCoupling(sb.getName(),"lookup",parent.broker.getName(),"subscribe");
		 
	  }
	  
	  private void addServiceEncrypted(double duration){
		  
		  
			 VoiceCommunicationSystem parent = (VoiceCommunicationSystem) getParent();
			  
		//	 exLog.append(parent.getSimulator().getTL()+","+"client"+parent.subCount+","+"created"+"\n");
			 ServiceClient sb = parent.addSubscriber(duration);
			 addModel(sb);
			 
			 // subcscriber to router
			 addCoupling(sb.getName(),"request",parent.router.getName(),"in");
			 addOutport(parent.router.getName(),sb.getName());
			 addCoupling(parent.router.getName(),sb.getName(),sb.getName(),"service");
			 
			 // subcscriber to broker
			 addCoupling(parent.broker.getName(),sb.getName(),sb.getName(),"found");
			 addOutport(parent.broker.getName(),sb.getName());
			 addCoupling(sb.getName(),"lookup",parent.broker.getName(),"subscribe");
			 
		  }
	 private void removeService(){
		  
		  
		VoiceCommunicationSystem parent = (VoiceCommunicationSystem) getParent();
		  
		  /*
		 removeCoupling("service1","out","modelExecutor","add");
		 removeCoupling("service2","out","modelExecutor","remove");
		 removeOutport("service1","out");
		 removeOutport("service2","out");
		 */

		removeModel("Subscriber2");
		//exLog.append(parent.getSimulator().getTL()+","+"client"+"2"+","+"removed"+"\n");
		removeModel("Subscriber3");
		//exLog.append(parent.getSimulator().getTL()+","+"client"+"3"+","+"removed"+"\n");
		//removeModel("Subscriber3");
		//removeModel("Subscriber4");
		
	//	removeModel("service1");
	//	removeModel("service2");
		 

	  }
	 
}

