/* 
 * AUTHOR: Kasper Franz
 * Loyalty Points 1.0.9
 * Last Changed: Made implements of permission general
 */

package com.github.franzmedia.LoyaltyPoints;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LCListener implements Listener {
	private final LoyaltyPoints plugin;
	private final boolean AFKTrack;

	// Main class for LCListener
	public LCListener(final LoyaltyPoints plugin) {
		this.plugin = plugin;
		AFKTrack = plugin.AfkTrackingSystem();

	}

	// HANDLER FOR PLAYER JOIN
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void Velociraptor(final PlayerJoinEvent event) {
		// permission check
		if (event.getPlayer().hasPermission("loyaltypoints.general")) {
			final LPUser user = plugin.getUser(event.getPlayer().getName());
			plugin.debug("PLayer kom ind:" + user.getTime() + " Starttid:"
					+ user.getTimeComparison() + "starttime" + user.getTime()
					+ "total " + user.getTotalTime());
			user.setOnline(true);
			user.setLocation(event.getPlayer().getLocation());
			user.setMoved(false);
		}
	}

	// HANDLER FOR PLAYER QUIT
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerLogout(final PlayerQuitEvent event) {
		// permission check
		if (event.getPlayer().hasPermission("loyaltypoints.general")) {
			final LPUser user = plugin.getUser(event.getPlayer().getName());
			plugin.debug("player logout" + user.getTime());
			user.givePoint();
			user.setOnline(false);
			user.setTimeComparison(0);
		}
	}

	// HANDLER FOR PLAYER MOVING!!!!
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerMove(final PlayerMoveEvent event) {
		// permission check
		if (event.getPlayer().hasPermission("loyaltypoints.general")) {
			plugin.kickStart(event.getPlayer().getName());
			if (AFKTrack) {
				final LPUser user = plugin.getUser(event.getPlayer().getName());
				final Player now = event.getPlayer();
				try{
					
				
				final int xdif = user.getLocation().getBlockX()-now.getLocation().getBlockX();
				final int ydif = user.getLocation().getBlockY()-now.getLocation().getBlockY();
				final int zdif = user.getLocation().getBlockZ()-now.getLocation().getBlockZ();
				if (xdif >= 1 || xdif <= -1 || ydif >= 2 || ydif <= -2
						|| zdif >= 1 || zdif <= -1) {
					user.setLocation(event.getPlayer().getLocation());
					if (!user.getMoved()) {
						user.setMoved(true);
					}
				}
				}catch(Exception io){
					user.setLocation(event.getPlayer().getLocation());
				}
				
			}
		}
	}

}
