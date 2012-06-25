/* 
 * AUTHOR: Kasper Franz
 * Loyalty Points 1.0.9
 * Last Changed: Made some performance changes
 */ 

package com.github.franzmedia.LoyaltyPoints;
import java.util.Date;
import org.bukkit.entity.Player;

public class LPScheduler implements Runnable {
	private LoyaltyPoints plugin;
	private long updateTimer;

	//Main class for LPScheduler
	public LPScheduler(LoyaltyPoints plugin) {
		updateTimer = new Date().getTime();
		this.plugin = plugin;
	}
	

	public void run() {
		
		Long now = new Date().getTime();
		//Run once for every online player!
		for (Player players : plugin.getServer().getOnlinePlayers()) {
			plugin.getUsers().get(players.getName()).givePoint();
		}
		
		
		// if it's time to save to the database/file it does this here in a new thread.
		if((now - updateTimer)/1000 >= plugin.getUpdateTimer()){
			updateTimer = new Date().getTime();
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

				   public void run() {
					   plugin.save();
					   
				   }
				});
			
		}
	}
	
}
