/* 
 * AUTHOR: Kasper Franz
 * Loyalty Points 1.1.3
 * Last Changed: implented the shop system and some small tweaks
 */

package com.github.franzmedia.LoyaltyPoints;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.block.Sign;

public class LPListener implements Listener {
	private final LoyaltyPoints plugin;
	private boolean shop_active;

	// Main class for LCListener
	public LPListener(final LoyaltyPoints plugin) {
		this.plugin = plugin;
		// AFKTrack = plugin.AfkTrackingSystem();
		shop_active = plugin.getlpConfig().shopActive();
	}

	// HANDLER FOR PLAYER JOIN
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerLogin(final PlayerJoinEvent event) {
		// permission check
		if (event.getPlayer().hasPermission("loyaltypoints.general")) {
			plugin.insertUser(plugin.getUser(event.getPlayer().getName()));
		}
	}

	// HANDLER FOR PLAYER QUIT
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerLogout(final PlayerQuitEvent event) {
		// permission check
		if (event.getPlayer().hasPermission("loyaltypoints.general")) {
			LPUser user = plugin.getUser(event.getPlayer().getName());
			user.givePoint();
			plugin.removeUser(user);
		}
	}

	// HANDLER FOR the playerInteractEvent (player right click)
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerInteract(final PlayerInteractEvent event) {
		if (shop_active) {
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (event.getClickedBlock().getType() == Material.SIGN_POST
						|| event.getClickedBlock().getType() == Material.WALL_SIGN) {
					Sign sign = (Sign) event.getClickedBlock().getState();
					if (sign.getLine(0).toLowerCase().contains("[loyaltyshop]")) {
						event.getPlayer()
								.sendMessage(
										"This is just to shop that the shop is running everything from here is controlled by the shop.action");
						plugin.getShop().action(event.getPlayer(), sign);
					}else{
						event.getPlayer().sendMessage(sign.getLine(0));					}
				}
			} 
		}
		

	}

	// HANDLER FOR THE SIGN PLACEMENT EVENT()
	// Used to check if the sign is a shop sign and
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onEditShop(final SignChangeEvent event) {
		plugin.getShop().createSign(event);
	}
}
