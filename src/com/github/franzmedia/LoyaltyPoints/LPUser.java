/* 
 * AUTHOR: Kasper Franz
 * Loyalty Points 1.0.9
 * Last Changed: Made the AFK system
 */ 

package com.github.franzmedia.LoyaltyPoints;

import java.util.Date;
import org.bukkit.Location;


public class LPUser{
	private String name;
	private int point;
	private int time;
	private int totalTime;
	private long timeComparison;
	private Milestone[] milestones;
	private LoyaltyPoints lp;
	private boolean online;
	private boolean moved;
	private Location location;


	public LPUser(LoyaltyPoints lp, String name, int point, int time, int totalTime, long timeComparison){
		this.name = name;
		this.point = point;
		this.time  = time;
		this.totalTime = totalTime;
		this.lp = lp;
		this.setTimeComparison(timeComparison);
		this.online = true;
		
	}

	public boolean isOnline() {
		return online;
	}


	public void setOnline(boolean online) {
		this.online = online;
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
	
	public void setPoint(int point){
		this.point = point;
	}


	public void increasePoint(int point){
		this.point = this.point+point;
	}
	
	public boolean removePoint(int point){
		boolean rtnb;
		if(point >= this.point){
			rtnb = true;
			this.point = this.point-point; 
		}else{
			rtnb = true;
		}
		
		return rtnb;
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
	
	
	public void givePoint(){
		//IF AFK SYSTEM == ON
		boolean go = true;
		if(lp.AfkTrackingSystem() && !moved){
			go = false;
		}
		
		if(go){
		Long before = timeComparison;
		Long now = new Date().getTime();
		int rest = getTimeLeft();
		int diff = (int) ((now-before)/1000);
		
		if (rest <= 0){ 
			setPoint(point+lp.getIncrement());
			time = 0-rest;						
		}else{
			time = time + diff;
		}
		
		totalTime = totalTime + diff;
		moved = false;
		timeComparison = now;
		}
	}


	public void setMoved(boolean moved) {
this.moved = moved;
		
	}


	public boolean getMoved() {
		return moved;
	}

	public void setLocation(Location location) {
		this.location = location;
			
			}
	
	
	public Location getLocation() {
return location;
	
	}
		
	
}