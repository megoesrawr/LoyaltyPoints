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
			LPUser user = plugin.getUsers().get(player);
			rest =  plugin.getTimeLeft(player);
			
			plugin.debug("LOYALTY TIME"+user.getTime()+"rest:"+rest);
			if (rest <= 0){ // cycleNumber amount of seconds has passed
				user.setPoint(user.getPoint()+plugin.getIncrement());
				
/* DEBUG */		plugin.debug(player+ "REST:"+rest);
/* DEBUG */		plugin.debug(player+": Time before: "+ user.getTime());
				user.setTime(0-rest);				
/* DEBUG */		plugin.debug(player+":Time after: "+ user.getTime());		
			}else{
				user.setTime((int) (user.getTime()+((now-user.getTimeComparison())/1000)));
					
			}
			user.setTotalTime(user.getTotalTime()+ (int) ((now -user.getTimeComparison())/1000));
			
			plugin.debug("running now:"+ now/1000 + " timecomparison: " + user.getTimeComparison()/1000 +"DIF: "+(now - user.getTimeComparison())/1000 + " cycle:" + cycle );
			user.setTimeComparison(now);
			
			
		}
		
		

		// saves the Scores to the file
		if(now - updateTimer >= (plugin.getUpdateTimer()*20000)){
			updateTimer = now;
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

				   public void run() {
					   plugin.save();
					   
				   }
				});
			
		}
	}
	
}
