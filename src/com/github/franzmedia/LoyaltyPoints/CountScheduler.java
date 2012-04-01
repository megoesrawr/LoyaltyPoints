package com.github.franzmedia.LoyaltyPoints;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CountScheduler implements Runnable {
	private int debug = 1;
	private LoyaltyPoints plugin;

	public CountScheduler(LoyaltyPoints isCool) {
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
		
		
		for (Player player : plugin.getServer().getOnlinePlayers()) {
			String m = player.getName();
			if ((now - plugin.getTimeComparison().get(m)) >= (plugin.getCycleNumber()*1000)) { // cycleNumber amount of seconds has passed
			
				plugin.getLoyaltyPoints().put(m, plugin.getLoyaltyPoints().get(m) + plugin.getIncrement());
				plugin.getTimeComparison().put(m, now);
				debug("loyalt før: "+ plugin.getLoyaltTime().get(m));
				plugin.getLoyaltTime().put(m, 0);
				debug("loyalt efter: "+ plugin.getLoyaltTime().get(m));
				
		}
			debug("kører now:"+ now + " timecomparison: " + plugin.getTimeComparison().get(m) +"DIF: "+(now - plugin.getTimeComparison().get(m)) + " cycle:" + plugin.getCycleNumber()*1000 );
			plugin.getLoyaltTime().put(m, (plugin.getLoyaltTime().get(m)+ (int) ((now - plugin.getLoyaltStart().get(m))/1000) ));
			plugin.getLoyaltTotalTime().put(m, (plugin.getLoyaltTotalTime().get(m)+ (int) ((now - plugin.getLoyaltStart().get(m))/1000) ));
			plugin.getLoyaltStart().put(m, now);
		}
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new CountScheduler(plugin), (long) plugin.getUpdateTimer());
	}
	
	public void debug(String txt){
		
		
		if(debug == 1){
			System.out.println(txt);
		}
		
		
	}

}
