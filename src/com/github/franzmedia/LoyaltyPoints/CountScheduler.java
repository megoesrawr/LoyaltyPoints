package com.github.franzmedia.LoyaltyPoints;
import java.util.Date;
import org.bukkit.entity.Player;

public class CountScheduler implements Runnable {
	private LoyaltyPoints plugin;
	private long updateTimer;

	public CountScheduler(LoyaltyPoints isCool) {
		updateTimer = new Date().getTime();
		plugin = isCool;
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
		for (Player players : plugin.getServer().getOnlinePlayers()) {
			plugin.getUsers().get(players.getName()).runTime();
		}
		
		// saves the Scores!!!!
		
		plugin.debug(now + "-"+ updateTimer + ">=" + plugin.getUpdateTimer());
		if(now - updateTimer >= plugin.getUpdateTimer()){
			updateTimer = new Date().getTime();
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

				   public void run() {
					   plugin.save();
					   
				   }
				});
			
		}
	}
	
}
