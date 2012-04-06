package com.github.franzmedia.LoyaltyPoints;
import java.util.Date;
import org.bukkit.Bukkit;
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
		
		if(now - updateTimer >= (plugin.getUpdateTimer()*1000)){
			LPFileManager.save();
			updateTimer = now;
		}
		int rest = 0;
		int cycle = plugin.getCycleNumber()*1000;
		for (Player players : plugin.getServer().getOnlinePlayers()) {
			
			String player = players.getName();
			if(plugin.getTimeComparison().get(player) == 0){
				plugin.getTimeComparison().put(player, now);
			}
			
			if ((now - plugin.getTimeComparison().get(player)) >= cycle) { // cycleNumber amount of seconds has passed
				rest = (int) (now - plugin.getTimeComparison().get(player))-cycle;
				plugin.getLoyaltyPoints().put(player, plugin.getLoyaltyPoints().get(player) + plugin.getIncrement());
				plugin.getTimeComparison().put(player, (now+rest));
				plugin.debug("loyalt before: "+ plugin.getLoyaltTime().get(player));
				plugin.getLoyaltTime().put(player, rest);
				plugin.debug("loyalt after: "+ plugin.getLoyaltTime().get(player));		
			}else{
				plugin.getLoyaltTime().put(player, (plugin.getLoyaltTime().get(player)+ (int) ((now - plugin.getLoyaltStart().get(player))/1000) ));	
			}
			plugin.debug("running now:"+ now + " timecomparison: " + plugin.getTimeComparison().get(player) +"DIF: "+(now - plugin.getTimeComparison().get(player)) + " cycle:" + cycle );
			plugin.getLoyaltTotalTime().put(player, (plugin.getLoyaltTotalTime().get(player)+ (int) ((now - plugin.getLoyaltStart().get(player))/1000) ));
			plugin.getLoyaltStart().put(player, now);
		}	
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new CountScheduler(plugin), (long) plugin.getUpdateTimer());
	}
	
}
