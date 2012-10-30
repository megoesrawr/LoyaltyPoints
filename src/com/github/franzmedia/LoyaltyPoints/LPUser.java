package com.github.franzmedia.LoyaltyPoints;

import java.util.Date;
/**
 * Handler to hold and control information about the user. 
 * @author Franz
 * @version 1.1.4
 *  Changed: Made java doc for it 
 *
 */
public class LPUser {
	private String name;
	private int point;
	private int time;
	private int totalTime;
	private long timeComparison;
	private final LoyaltyPoints lp;
	/**
	 * Creates a object with all information about the user is needed for the class to be used.
	 * 
	 * @param lp a reference to the plugin (needed to get to the config and for debug).
	 * @param name The name of the user, also used as the key in the lists.
	 * @param point The amount of points the player have.
	 * @param time The time the player have been online since last point round (in sec)
	 * @param totalTime The Totaltime the player have been online.
	 * @param timeComparison The last time the player have been seen.
	 */
	public LPUser(final LoyaltyPoints lp, final String name, final int point,
			final int time, final int totalTime, final long timeComparison) {
		this.name = name;
		this.point = point;
		this.time = time;
		this.totalTime = totalTime;
		this.lp = lp;
		this.timeComparison = timeComparison;

	}
	
	
	/**
	 * This constructor is used for creationg a new user (without points, time and so on.
	 * @param name The name of the user
	 * @param lp Reference to the main class.
	 */
	public LPUser(String name, LoyaltyPoints lp) {
		this.name = name;
		this.lp = lp;
		point = lp.getlpConfig().getStartingPoints();
		time = 0;
		totalTime = 0;
		timeComparison = new Date().getTime();
		
	}
	/**
	 * 
	 * @return The Users name.
	 */
	public String getName() {
		return name;
	}

	
	/**
	 * A method to get the users points
	 * 
	 * @return the amount of points the user haves
	 */
	public int getPoint() {
		return point;
	}

	/**
	 * A command to set the points for the user
	 * @param point Sets the user points to this.
	 */
	public void setPoint(final int point) {
		this.point = point;
	}
/**
 * A method to increate the users point.
 * @param point the amount to increase the points with.
 */
	public void increasePoint(final int point) {
				this.point = this.point + point;
			}
	/**
	 * A method to remove points from the user.
	 * @param removeNumb the amout of points to remove from the user.
	 * @return true if success else false. (he dont have the points)
	 */
	public boolean removePoint(final int removeNumb) {
		boolean rtnb;
		if (removeNumb <= point) {
			rtnb = true;
			point = point - removeNumb;
		} else {
			rtnb = true;
		}

		return rtnb;
	}

	/**
	 * 
	 * @return the amount of time the player is on atm.
	 */
	public int getTime() {
		return time;
	}
/**
 * 
 * @param time the new time the player have been playing.
 */
	public void setTime(final int time) {
		this.time = time;
	}
/**
 * 
 * @return the amount of playtime the user have (total)
 */
	public int getTotalTime() {
		return totalTime;
	}
/**
 * 
 * 
 * @param totalTime The new total playtime.
 */
	public void setTotalTime(final int totalTime) {
		this.totalTime = totalTime;
	}

	
	/***
	 * 
	 * @return get the time that last cycle was run.
	 */
	
	public long getTimeComparison() {
		return timeComparison;
	}
/**
 * 
 * @param timeComparison The new time of the cycle.
 */
	public void setTimeComparison(final long timeComparison) {
		this.timeComparison = timeComparison;
	}
/**
 * 
 * @return The amount of time since this last was run.
 */
	public int timeSinceLastRun() {
		lp.debug(  (new Date().getTime()+ "-"+ timeComparison) +"/"+ "1000");
		return  (int) (new Date().getTime() - timeComparison) / 1000;
		
	}
/**
 * 
 * @return Time left for new points
 */
	public int getTimeLeft() {
		
		return lp.getlpConfig().getCycleNumber() - timeSinceLastRun() - getTime();

	}
	/***
	 * Method to see if the player should get points and if he needs points he gets it, else we just add the new time  to him.
	 */
	public void givePoint() {
		
			if (getTimeLeft() <= 0) {
				setPoint(point + lp.getlpConfig().getIncrement());
				time = 0 - getTimeLeft();
			} else {
				time = time + timeSinceLastRun();
			}

			totalTime = totalTime + timeSinceLastRun();
			
			timeComparison = new Date().getTime();;
		}
}