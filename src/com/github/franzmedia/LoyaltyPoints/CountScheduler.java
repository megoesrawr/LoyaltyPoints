package com.github.franzmedia.LoyaltyPoints;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CountScheduler implements Runnable {
	private LoyaltyPoints _p;

	public CountScheduler(LoyaltyPoints isCool) {
		_p = isCool;
	}

	/*
	 * update timer! in an attempt to save system resources, this plugin has
	 * only one timer that tracks when to check all updates. this is also in
	 * seconds, and must be less than or equal to the cycle-time-in-seconds the
	 * less the number, updates are checked more often, but more resources are
	 * used the more the number, the updates are checked less often, but less
	 * resources are used.
	 */
	public void run() {
		Long now = new Date().getTime();
		
		for (Player p : _p.getServer().getOnlinePlayers()) {
			String m = p.getName();
			if ((now - _p.timeComparison.get(m)) >= (_p.cycleNumber*1000)) { // cycleNumber
																		// amount
																		// of
																		// seconds
																		// has
																		// passed.
				_p.loyaltyMap.put(m, _p.loyaltyMap.get(m) + _p.increment);
				_p.timeComparison.put(m, now);
			
			}
			
			_p.LoyaltTime.put(m, (_p.LoyaltTime.get(m)+ (int) ((now - _p.LoyaltStart.get(m))/1000) ));
			
			_p.LoyaltStart.put(m, now);
		}
		Bukkit.getServer()
				.getScheduler()
				.scheduleSyncDelayedTask(_p, new CountScheduler(_p),
						(long) _p.updateTimer);
	}

}
