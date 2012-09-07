/* 
 * AUTHOR: Kasper Franz
 * Loyalty Points 1.0.9
 * Last Changed: Made the AFK system
 */

package com.github.franzmedia.LoyaltyPoints;

import java.util.Date;

import org.bukkit.Location;

public class LPUser {
	private String name;
	private int point;
	private int time;
	private int totalTime;
	private long timeComparison;
	private final LoyaltyPoints lp;
	private boolean online;
	private boolean moved;
	private Location location;

	public LPUser(final LoyaltyPoints lp, final String name, final int point,
			final int time, final int totalTime, final long timeComparison) {
		this.name = name;
		this.point = point;
		this.time = time;
		this.totalTime = totalTime;
		this.lp = lp;
		this.setTimeComparison(timeComparison);
		this.online = true;

	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(final boolean online) {
		this.online = online;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(final int point) {
		this.point = point;
	}

	public boolean increasePoint(final int point) {
			boolean rtnb;
			if (point <= this.point) {
				rtnb = true;
				this.point = this.point + point;
			} else {
				rtnb = true;
			}
			return rtnb;
			}
	
	public boolean removePoint(final int point) {
		boolean rtnb;
		if (point >= this.point) {
			rtnb = true;
			this.point = this.point - point;
		} else {
			rtnb = true;
		}

		return rtnb;
	}

	public int getTime() {
		return time;
	}

	public void setTime(final int time) {
		this.time = time;
	}

	public int getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(final int totalTime) {
		this.totalTime = totalTime;
	}

	public long getTimeComparison() {
		return timeComparison;
	}

	public void setTimeComparison(final long timeComparison) {
		this.timeComparison = timeComparison;
	}

	public int timeSinceLastRun() {

		final int sinceLast = (int) (new Date().getTime() - timeComparison) / 1000;
		lp.debug("TIME SINCE LAST RUN:::: " + sinceLast);
		return sinceLast;
	}

	public int getTimeLeft() {
		lp.debug("now: " + new Date().getTime() + " TC: " + timeComparison);
		lp.debug("cycle: " + lp.getCycleNumber() + "date - timecompa"
				+ (new Date().getTime() - timeComparison) + " time: "
				+ getTime());
		return lp.getCycleNumber() - timeSinceLastRun() - getTime();

	}

	public void givePoint() {
		// IF AFK SYSTEM == ON
		boolean go = true;
		lp.debug(lp.AfkTrackingSystem() + " " + moved);
		if (lp.AfkTrackingSystem() && !moved) {
			go = false;
		}

		if (go) {
			final Long before = timeComparison;
			final Long now = new Date().getTime();
			final int rest = getTimeLeft();
			final int diff = (int) ((now - before) / 1000);

			if (rest <= 0) {
				setPoint(point + lp.getIncrement());
				time = 0 - rest;
			} else {
				time = time + diff;
			}

			totalTime = totalTime + diff;
			moved = false;
			timeComparison = now;
		}
	}

	public void setMoved(final boolean moved) {
		this.moved = moved;

	}

	public boolean getMoved() {
		return moved;
	}

	public void setLocation(final Location location) {
		this.location = location;

	}

	public Location getLocation() {
		return location;

	}

}