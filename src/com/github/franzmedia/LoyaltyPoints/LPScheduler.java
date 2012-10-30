/* 
 * AUTHOR: Kasper Franz
 * Loyalty Points 1.0.9
 * Last Changed: added general permission
 */
package com.github.franzmedia.LoyaltyPoints;

import java.util.Date;
import org.bukkit.entity.Player;

/**
 * This is for checking users now and then and update their points.
 *
 * @author Franz
 * @version 1.1.4
 * @see Added javaDoc for this.
 *      Fixed a bug with the permissions, this shouldn't have done anything, as it's also runned when the player logout.
 */
public class LPScheduler implements Runnable {

    private final LoyaltyPoints plugin;
    private long updateTimer;

    /**
     *
     * @param plugin A reference to the plugin so we can get the users and update time and so on.
     */
    public LPScheduler(final LoyaltyPoints plugin) {
        updateTimer = new Date().getTime();
        this.plugin = plugin;
    }

    /**
     * The run method, this is running every time it's called, and checks all
     * the online users and running their givePoints method.
     */
    @Override
    public void run() {
        plugin.debug("running schedular");
        final Long now = new Date().getTime();

        // Run once for every online player!
        for (final Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.hasPermission("loyaltypoints.general")) {
                plugin.getUser(player.getName()).givePoint();
            }
        }


        // if it's time to save to the database it does this here in a new thread.
        if ((now - updateTimer) / 1000 >= plugin.getlpConfig().getUpdateTimer()) {
            updateTimer = new Date().getTime();
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

                @Override
                public void run() {
                    plugin.saveOnlineUsers();

                }
            });

        }
    }
}
