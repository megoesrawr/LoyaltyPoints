package com.github.franzmedia.LoyaltyPoints;

import java.util.Date;


public class LPUser{
	private String name;
	private int point;
	private int time;
	private int totalTime;
	private long timeComparison;
	private Milestone[] milestones;
	private LoyaltyPoints lp;
	private boolean online;
	
	
	public boolean isOnline() {
		return online;
	}


	public void setOnline(boolean online) {
		this.online = online;
	}


	public LPUser(LoyaltyPoints lp, String name, int point, int time, int totalTime, long timeComparison){
		this.name = name;
		this.point = point;
		this.time  = time;
		this.totalTime = totalTime;
		this.lp = lp;
		this.setTimeComparison(timeComparison);
		this.online = true;
		
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public int getPoint() {
		return point;
	}


	public void setPoint(int point) {
		this.point = point;
	}


	public int getTime() {
		return time;
	}


	public void setTime(int time) {
		this.time = time;
	}


	public int getTotalTime() {
		return totalTime;
	}


	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}


	public Milestone[] getMilestones() {
		return milestones;
	}


	public long getTimeComparison() {
		return timeComparison;
	}


	public void setTimeComparison(long timeComparison) {
		this.timeComparison = timeComparison;
	}
	
	public int timeSinceLastRun(){
	
		int sinceLast = (int) (new Date().getTime()-timeComparison)/1000;
		lp.debug("TIME SINCE LAST RUN:::: "+ sinceLast );
		return sinceLast;
	}
	
	public int getTimeLeft(){
		lp.debug("now: "+ new Date().getTime() + " TC: "+ timeComparison);
		lp.debug("cycle: " +lp.getCycleNumber() + "date - timecompa" + (new Date().getTime()-timeComparison) + " time: " + getTime());
		return (int) lp.getCycleNumber()-timeSinceLastRun()-getTime();
		
	}
	
	
	public void runTime(){
		Long before = timeComparison;
		Long now = new Date().getTime();
		int rest = getTimeLeft();
		timeComparison = now;
		int diff = (int) ((now-before)/1000);
		lp.debug("DIF"+diff+""+ "REST: "+rest);
		
		lp.debug("LOYALTY TIME"+time+"rest:"+rest+ "DIF"+ diff);
		if (rest <= 0){ 
			setPoint(point+lp.getIncrement());
			
/* DEBUG */		lp.debug(name+ "REST:"+rest);
/* DEBUG */		lp.debug(name+": Time before: "+ getTime());
			time = 0-rest;				
/* DEBUG */		lp.debug(name+":Time after: "+ getTime());		
		}else{
			
			time = time + diff;
				
		}
		lp.debug(totalTime+"");
		totalTime = totalTime + diff;
		lp.debug(totalTime+"");
		}
		
	
}