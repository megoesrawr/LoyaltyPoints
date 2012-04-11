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
		int rest = 0;
		int cycle = plugin.getCycleNumber();
		Long now = new Date().getTime();
		
		// for every player saves the time to the 
		for (Player players : plugin.getServer().getOnlinePlayers()) {
			String player = players.getName();
			
			rest =  plugin.getTimeLeft(player);
			plugin.debug("LOYALTY TIME"+plugin.getLoyaltTime().get(player)+"rest:"+rest);
			if (rest <= 0){ // cycleNumber amount of seconds has passed
				
				plugin.getLoyaltyPoints().put(player, plugin.getLoyaltyPoints().get(player) + plugin.getIncrement());
/* DEBUG */		plugin.debug(player+ "REST:"+rest);
/* DEBUG */		plugin.debug(player+": loyalt before: "+ plugin.getLoyaltTime().get(player));
				plugin.getLoyaltTime().put(player, 0-rest);
/* DEBUG */		plugin.debug(player+":loyalt after: "+ plugin.getLoyaltTime().get(player));		
			}else{
				plugin.getLoyaltTime().put(player, (int) (plugin.getLoyaltTime().get(player)+ ((now-plugin.getTimeComparison().get(player))/1000)));	
			}
			plugin.getLoyaltTotalTime().put(player, (plugin.getLoyaltTotalTime().get(player)+ (int) ((now - plugin.getTimeComparison().get(player))/1000) ));
			plugin.debug("running now:"+ now/1000 + " timecomparison: " + plugin.getTimeComparison().get(player)/1000 +"DIF: "+(now - plugin.getTimeComparison().get(player))/1000 + " cycle:" + cycle );
			plugin.getTimeComparison().put(player, now);
			
		}
		
		

		// saves the Scores to the file
		if(now - updateTimer >= (plugin.getUpdateTimer()*1000)){
			LPFileManager.save();
			updateTimer = now;
		}
		
		
		// Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new CountScheduler(plugin), (long) plugin.getUpdateTimer());
	}
	
}
