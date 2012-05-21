package com.github.franzmedia.LoyaltyPoints;

import java.util.Date;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LCListener implements Listener {
	private final LoyaltyPoints plugin;

	public LCListener(LoyaltyPoints plugin) {
		this.plugin = plugin;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void Velociraptor(final PlayerJoinEvent event) { 	
		
		
		// plugin.debug("PLayer kom ind:" + user.getTime() + " Starttid:" + user.getTimeComparison());
		plugin.kickStart(event.getPlayer().getName());
		LPUser user = plugin.getUsers().get(event.getPlayer().getName());
		plugin.debug("PLayer kom ind:" + user.getTime() + " Starttid:" + user.getTimeComparison() + "starttime"+user.getTime() + "total "+ user.getTotalTime());
		user.setOnline(true);
	}
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerLogout(PlayerQuitEvent event) {
		LPUser user = plugin.getUsers().get(event.getPlayer().getName());
		plugin.debug("player logout"+ user.getTime());
		Long now = new Date().getTime();
		
		if ((now - user.getTimeComparison()) >= (plugin.getCycleNumber()*1000)) { // cycleNumber amount of seconds has passed
			user.setPoint(user.getPoint() + plugin.getIncrement());
			user.setTime(0);
					
	}else{
		user.setTime(user.getTime() + (int) ((now - user.getTimeComparison())/1000));
			
	}
		user.setOnline(false);
		user.setTotalTime(user.getTotalTime() + (int) ((now-user.getTimeComparison())/1000));
		user.setTimeComparison(0);
	
	}
}
