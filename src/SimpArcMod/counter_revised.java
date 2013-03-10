/*     
 *  Author     : Abdurrahman Alshareef - 561 CSE - M&S course 
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 03-04-13 
 */

package SimpArcMod;

import view.modeling.ViewableAtomic;
import model.modeling.content;
import model.modeling.message;
import GenCol.doubleEnt;
import GenCol.entity;

public class counter_revised extends ViewableAtomic{
	int count;
	double step_time;
	boolean resume;
	
	public void AddTestPortValue(double input){
		addTestInput("in",new doubleEnt((double)input));
	}

	public counter_revised(){
		super("counter_revised");
		step_time = 1 ;
		addInport("in");
		addOutport("out");
		AddTestPortValue(0);
		AddTestPortValue(1);
	}

	public counter_revised(String name, double step_time){
		super(name);
		this.step_time = step_time ;
		addInport("in");
		addOutport("out");
		AddTestPortValue(0);
		AddTestPortValue(1);
	}

	public void initialize(){
		phase = "passive";
		sigma = INFINITY;
		holdIn(phase, sigma);
		count  = 0;
		resume = false;
		super.initialize();
	}

	public void  deltext(double e,message x){
		Continue(e);
		content c0 = new content("in",new doubleEnt(0.0));

		for (int i=0; i< x.getLength();i++)
			if (messageOnPort(x,"in",i))
			{
				content c = new content("in",x.getValOnPort("in",i)); 

				if( !c.equals(c0) && phaseIs("passive")){
					phase = "active";
					sigma = step_time;
					count ++;

					holdIn(phase, sigma);
				}
				else if(c.equals(c0) && phaseIs("passive")){
					phase = "respond";
					sigma = step_time;

					holdIn(phase, sigma);					
				} 
				else if(c.equals(c0) && phaseIs("active")){
					resume = true;
					holdIn("respond",0);
				}
			}
	}

	public void  deltint( ){
		if(!resume){
			phase = "passive";
			sigma = INFINITY;
			holdIn(phase, sigma);
		} else {
			resume = false;
			phase ="active";
			sigma = step_time;
			holdIn(phase, sigma);
		}
	}

	public message out(){
		message m = new message( );
		if (phaseIs("respond")) {//|| print_output){
			System.out.println("out: -->" + count);
			showState();
			m.add(makeContent("out", new entity(Integer.toString(count))));
		}
		return m;
	}

	public void showState(){
		super.showState();
		System.out.println("State: " + phase + "," + sigma + ","+ count);
	}

}
