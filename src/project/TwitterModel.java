package project;

import java.awt.Dimension;
import java.awt.Point;

import GenCol.entity;

import twitter.debug.Transducer;
import view.modeling.ViewableAtomic;
import view.modeling.ViewableComponent;
import view.modeling.ViewableDigraph;

public class TwitterModel extends ViewableDigraph {

	public TwitterModel(){
		this("Twitter Model");
	}
	
	public TwitterModel(String name) {
		super(name);
		
		addOutport("OUT");
		
		ViewableAtomic g = new TweetG();
		ViewableAtomic tm = new TweetCreator();
		ViewableAtomic tr = new Transducer();
		
		add(g);
		add(tm);
		add(tr);
		
		addInport("putTweetNow");
		addTestInput("putTweetNow", new entity("Getstats"));
		
		addCoupling(this, "putTweetNow", tm, TweetCreator.IN_RETURNSTATSNOW);
		
		addCoupling(g, TweetG.OUT_SETTINGS, tm, TweetCreator.IN_CONFIG);
		addCoupling(g, TweetG.OUT_TWTCMD, tm, TweetCreator.IN_TWEETCOMMAND);
		
//		addCoupling(tm, TweetCreator.OUT_TWEET, this, "OUT_TWEET");
		addCoupling(tm, TweetCreator.OUT_TWEET, tr, "lists");
		addCoupling(tr, "stat", this, "OUT");
	}

	
    /**
     * Automatically generated by the SimView program.
     * Do not edit this manually, as such changes will get overwritten.
     */
    public void layoutForSimView()
    {
        preferredSize = new Dimension(591, 332);
        ((ViewableComponent)withName("Tansducer")).setPreferredLocation(new Point(238, 245));
        ((ViewableComponent)withName("ViewableAtomic")).setPreferredLocation(new Point(214, 85));
        ((ViewableComponent)withName("Tweet Gen")).setPreferredLocation(new Point(50, 50));
    }
}
