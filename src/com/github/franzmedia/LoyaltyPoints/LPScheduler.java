/* 
 * AUTHOR: Kasper Franz
 * Loyalty Points 1.0.9
 * Last Changed: added general permission
 */

package com.github.franzmedia.LoyaltyPoints;

import java.util.Date;

import org.bukkit.entity.Player;

public class LPScheduler implements Runnable {
	private final LoyaltyPoints plugin;
	private long updateTimer;

	// Main class for LPScheduler
	public LPScheduler(final LoyaltyPoints plugin) {
		updateTimer = new Date().getTime();
		this.plugin = plugin;
	}

	@Override
	public void run() {

		final Long now = new Date().getTime();
		// Run once for every online player!
		for (final Player player : plugin.getServer().getOnlinePlayers()) {
			if (player.hasPermission("loyaltypoints.check.self")) {
				plugin.getUsers().get(player.getName()).givePoint();
			}
		}

		// if it's time to save to the database/file it does this here in a new
		// thread.
		if ((now - updateTimer) / 1000 >= plugin.getUpdateTimer()) {
			updateTimer = new Date().getTime();
			plugin.getServer().getScheduler()
					.scheduleSyncDelayedTask(plugin, new Runnable() {

						@Override
						public void run() {
							plugin.save();

						}
					});

		}
	}

}
