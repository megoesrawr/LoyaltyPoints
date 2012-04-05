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
		plugin.kickStart(event.getPlayer().getName());
		
	}
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerLogout(PlayerQuitEvent event) {
		Long now = new Date().getTime();
		String m = event.getPlayer().getName();
		if ((now - plugin.getTimeComparison().get(m)) >= (plugin.getCycleNumber()*1000)) { // cycleNumber amount of seconds has passed
			plugin.getLoyaltyPoints().put(m, plugin.getLoyaltyPoints().get(m) + plugin.getIncrement());
			plugin.getTimeComparison().put(m, now);
			plugin.getLoyaltTime().put(m, 0);		
	}else{
		plugin.getLoyaltTime().put(m, (plugin.getLoyaltTime().get(m)+ (int) ((now - plugin.getLoyaltStart().get(m))/1000) ));	
	}
		plugin.getLoyaltTotalTime().put(m, (plugin.getLoyaltTotalTime().get(m)+ (int) ((now - plugin.getLoyaltStart().get(m))/1000) ));
		plugin.getLoyaltStart().put(m, now);
	
	}
}
