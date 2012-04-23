package com.github.franzmedia.LoyaltyPoints;


public class LPUser{
	private String name;
	private int point;
	private int time;
	private int totalTime;
	private long timeComparison;
	private Milestone[] milestones;
	
	
	public LPUser(String name, int point, int time, int totalTime, long timeComparison){
		this.name = name;
		this.point = point;
		this.time  = time;
		this.totalTime = totalTime;
		this.setTimeComparison(timeComparison);
		
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
	
}