/* 
 * AUTHOR: Kasper Franz
 * Loyalty Points 1.0.9
 * Last Changed: Made the PlayerMoveEvent for the AFK systems
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
	private LoyaltyPoints plugin;
	private boolean AFKTrack;
	
	//Main class for LCListener
	public LCListener(LoyaltyPoints plugin) {
		this.plugin = plugin;
		AFKTrack = plugin.AfkTrackingSystem();
		
	}

	
	//HANDLER FOR PLAYER JOIN
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void Velociraptor(final PlayerJoinEvent event) { 	
		plugin.kickStart(event.getPlayer().getName());
		LPUser user = plugin.getUsers().get(event.getPlayer().getName());
		plugin.debug("PLayer kom ind:" + user.getTime() + " Starttid:" + user.getTimeComparison() + "starttime"+user.getTime() + "total "+ user.getTotalTime());
		user.setOnline(true);
		user.setLocation(event.getPlayer().getLocation());
		user.setMoved(false);
	}
	
	//HANDLER FOR PLAYER QUIT
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerLogout(PlayerQuitEvent event) {
		LPUser user = plugin.getUsers().get(event.getPlayer().getName());
		plugin.debug("player logout"+ user.getTime());
		user.givePoint();
		user.setOnline(false);
		
		user.setTimeComparison(0);
	
	}
	//HANDLER FOR PLAYER MOVING!!!!
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerMove(PlayerMoveEvent event){
		if(AFKTrack){
			LPUser user = plugin.getUsers().get(event.getPlayer().getName());
			Player now = event.getPlayer();
			int xdif = user.getLocation().getBlockX()-now.getLocation().getBlockX();
			int ydif = user.getLocation().getBlockY()-now.getLocation().getBlockY();
			int zdif = user.getLocation().getBlockZ()-now.getLocation().getBlockZ();
			
			if(xdif >= 1 || xdif <= -1  || ydif >= 2 || ydif <= -2 || zdif >= 1 || zdif <= -1 ){
				user.setLocation(event.getPlayer().getLocation());	
				if(!user.getMoved()){
					user.setMoved(true);
				}
		
			}
		}
	}
	
}

	