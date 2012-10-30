package com.github.franzmedia.LoyaltyPoints;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *  
 * @AUTHOR: Kasper Franz
 * @version: 1.1.4
 * Last Changed: JavaDoc for this.
 *
 */
public class LPListener implements Listener {

    private final LoyaltyPoints plugin;
    private boolean shop_active;

    
    /**
     * The main of the listener, which listen on events from the player.
     * @param plugin Reference to the plugin (to get access to users and so on.)
     */
    public LPListener(final LoyaltyPoints plugin) {
        this.plugin = plugin;
        
        shop_active = plugin.getlpConfig().shopActive();
    }

    /**
     * 
     * @param event the join event when a play joins the server.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerLogin(final PlayerJoinEvent event) {
        // permission check
        if (event.getPlayer().hasPermission("loyaltypoints.general")) {
            plugin.insertUser(plugin.getUser(event.getPlayer().getName()));
        }
    }


    /**
     * The handler for when a player logout of the server.
     * @param event the PlayerQuitEvent when the player leaves.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerLogout(final PlayerQuitEvent event) {
        // permission check
        if (event.getPlayer().hasPermission("loyaltypoints.general")) {
            LPUser user = plugin.getUser(event.getPlayer().getName());
            user.givePoint();
            plugin.removeUser(user);
        }
    }


    /**
     * HANDLER FOR the playerInteractEvent (player right click)
     * @param event PlayerInteractEvent
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (shop_active) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (event.getClickedBlock().getType() == Material.SIGN_POST
                        || event.getClickedBlock().getType() == Material.WALL_SIGN) {
                    Sign sign = (Sign) event.getClickedBlock().getState();
                    if (sign.getLine(0).toLowerCase().contains("[loyaltyshop]")) {
                        event.getPlayer().sendMessage(
                                "This is just to shop that the shop is running everything from here is controlled by the shop.action");
                        plugin.getShop().action(event.getPlayer(), sign);
                    } else {
                        event.getPlayer().sendMessage(sign.getLine(0));
                    }
                }
            }
        }


    }

   /**
    * Handler for when a sign is placed.
    * @param event  the signChangeEvent
    */
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEditShop(final SignChangeEvent event) {
        plugin.getShop().createSign(event);
    }
}
