package com.github.franzmedia.LoyaltyPoints;

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
		
	LPFileManager.save();
	System.out.println("Spiller gik ud"+ event.getPlayer().getName());
	
	}
}
