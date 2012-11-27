/* 
 * AUTHOR: Kasper Franz
 * Loyalty Points 1.0.9
 * Last Changed: Added general permission 
 */
package com.github.franzmedia.LoyaltyPoints;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LPCommand implements CommandExecutor {

    private final LoyaltyPoints plugin;
    private LPTexts lptext;

    public LPCommand(final LoyaltyPoints plugin, LPTexts lptexts) {
        this.plugin = plugin;
        lptext = lptexts;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd,
            final String zhf, final String[] args) {
        String playerName = sender.getName();
        // permission check
        if (sender.hasPermission("loyaltypoints.general")) {
            if (args.length == 0) {
                if (sender instanceof Player) {
                    if (sender.hasPermission("loyaltypoints.check.self")) {
                        sender.sendMessage(lptext.getSelfcheckMessage().replaceAll("%PLAYERNAME%", playerName).replaceAll(
                                "%POINTS%",
                                plugin.getUser(playerName).getPoint()
                                + ""));
                    }

                } else {
                    sender.sendMessage(lptext.getText("consoleCheck", 1));
                }
            } else {
                if (args[0].equalsIgnoreCase("toSQL")) {
                    if (sender instanceof Player) {
                    } else {
                        sender.sendMessage(lptext.getToMySQL());
                        sender.sendMessage(plugin.getlpConfig().getDatabase().transformToSQL());
                    }
                } else if (args[0].equalsIgnoreCase("add")) {

                    if (sender instanceof Player) {
                        if (sender.hasPermission("loyaltypoints.add")) {
                            add(sender, args);
                        } else {
                            sender.sendMessage(lptext.getAddToUser());
                        }

                    } else {
                        add(sender, args);
                    }

                } else if (args[0].equalsIgnoreCase("help")) {
                    /*
                     * HELP COMMAND
                     */
                    help(sender);

                } else if (args[0].equalsIgnoreCase("top")
                        && sender.hasPermission("loyaltypoints.top")) {
                    /*
                     * TOP COMMAND
                     */
                    top(sender, args);

                } else if (args[0].equalsIgnoreCase("set")
                        && sender.hasPermission("loyaltypoints.set")) {
                    /*
                     * SET COMMAND
                     */
                    set(sender, args);

                } else if (args[0].equalsIgnoreCase("version")
                        && sender.hasPermission("loyaltypoints.version")) {
                    /*
                     * VERSION COMMAND
                     */
                    version(sender);

                } else if (args[0].equalsIgnoreCase("reload")
                        && sender.hasPermission("loyaltypoints.reload")) {
                    /*
                     * RELOAD COMMAND
                     */
                    reload(sender);
                } else if (args[0].equalsIgnoreCase("next")
                        && sender.hasPermission("loyaltypoints.next")) {
                    if (sender instanceof Player) {
                        /*
                         * NEXT COMMAND
                         */
                        next(sender);
                    } else { // is cmd
                        sender.sendMessage(lptext.getText("consoleCheck", 1));

                    }
                } else if ((args[0].equalsIgnoreCase("playtime") || args[0].equalsIgnoreCase("time"))
                        && sender.hasPermission("loyaltypoints.playtime")) {
                    if (sender instanceof Player) {
                        /*
                         * PlayTime
                         */

                        playtime(sender, args);
                    } else { // is cmd
                        sender.sendMessage(lptext.getText("consoleCheck", 1));

                    }

                } else {
                    // compare other ppl

                    if (sender.hasPermission("loyaltypoints.check.other")) {


                        if (plugin.areUser(args[0])) {
                            sender.sendMessage(lptext.getCheckotherMessage().replaceAll("%PLAYERNAME%", args[0]).replaceAll(
                                    "%POINTS%",
                                    String.valueOf(plugin.getUser(
                                    args[0]).getPoint())));

                        } else {
                            sender.sendMessage(lptext.getNoUser());

                        }

                    } else {
                        sender.sendMessage(lptext.getErrorPermission());
                    }
                }
            }
        }
        return true;

    }

    private void playtime(final CommandSender sender, final String[] args) {

        final LPUser user = plugin.getUser(sender.getName());
        final int time = user.timeSinceLastRun() + user.getTotalTime();

        final String daten = plugin.getNiceNumber(time);
        sender.sendMessage(lptext.getOnlinetime().replaceAll("%ONLINETIME%",
                daten));

    }

    private void next(final CommandSender sender) {

        final LPUser user = plugin.getUser(sender.getName());
        final int time = user.getTimeLeft();
        if (time >= 0) {
            String daten = plugin.getNiceNumber(time);

            sender.sendMessage(lptext.getNext().replaceAll("%TIME%", daten));
        } else {
            user.givePoint();
            next(sender);
        }

    }

    private void add(final CommandSender sender, final String[] args) {
        try {
            plugin.getUser(args[1]).increasePoint(Integer.parseInt(args[2]));
            sender.sendMessage(lptext.getAddToUser().replaceAll("%POINT%", args[2]).replaceAll("%USER%", args[1]));

        } catch (final Exception exception) {
            sender.sendMessage(lptext.getErrorNoPlayer());
        }

    }

    private void reload(final CommandSender sender) {
        plugin.onDisable();
        plugin.onEnable();
        sender.sendMessage(lptext.getReloadMsg());

    }

    private void version(final CommandSender sender) {

        sender.sendMessage(lptext.getNowVersion().replaceAll("%VERSION%",
                plugin.getDescription().getVersion()));

        if (sender.isOp()) {

            if (!plugin.getlpConfig().upToDate()) {
                sender.sendMessage(lptext.getNewVersionAvalible().replaceAll(
                        "%NEWVERSION%", plugin.getlpConfig().getNewVersion() + ""));

            }

        }
    }

    private void set(final CommandSender sender, final String[] args) {

        if (args.length != 3) {
            sender.sendMessage(lptext.getHelpSet());
        } else if (!plugin.areUserOnline(args[1])) {
            sender.sendMessage(lptext.getErrorUnknownUser());

        } else {

            try {
                final int amount = Integer.parseInt(args[2]);
                plugin.getUser(args[1]).setPoint(amount);
                sender.sendMessage(lptext.getNewSetAmount().replaceAll("%PLAYER%", args[1]).replaceAll("%AMOUNT%", amount + ""));
            } catch (final NumberFormatException e) {
                sender.sendMessage(lptext.getErrorNumber()
                        + "/lp set [username]");
            }
        }

    }

    private void top(final CommandSender sender, final String[] args) {
        int maxTop = 10;
        if (args.length == 2) {
            try {
                maxTop = Integer.parseInt(args[1]);
            } catch (final NumberFormatException nfe) { // if args contains
                // other that integers.
                sender.sendMessage(lptext.getErrorNumber() + " /lp top");
                maxTop = 10;
            }

            if (maxTop < 10) {
                maxTop = 10;
            }
        }

        int from = maxTop - 10;

        LPUser[] users = plugin.getlpConfig().getDatabase().getTop(maxTop - 10, 10);
        plugin.debug("From: " + from + " MaxTop" + maxTop + users.length);
        if (users.length == 0) {
            sender.sendMessage(lptext.getErrorNoUsers());
        } else {

            if (maxTop > users.length) {
                maxTop = users.length;
            }
            sender.sendMessage(ChatColor.DARK_AQUA + "---------"
                    + ChatColor.GOLD + " LoyaltyPoints Top Players "
                    + ChatColor.DARK_AQUA + "---------");

            for (int i = 0; i < users.length; i++) {
                int pos = i + from + 1;

                plugin.debug(pos + "" + i + "" + maxTop + " -9");
                sender.sendMessage(ChatColor.GOLD + String.valueOf(pos)
                        + ". " + ChatColor.AQUA + users[i].getName()
                        + " - " + ChatColor.DARK_AQUA + users[i].getPoint()
                        + " points");

            }
        }

    }

    private void help(final CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_AQUA + "--------- " + ChatColor.GOLD
                + "LoyaltyPoints Help " + ChatColor.DARK_AQUA + "---------");

        final ChatColor chatColor = ChatColor.GOLD;
        final ChatColor chatColor2 = ChatColor.AQUA;

        if (sender.hasPermission("loyaltypoints.check.self")) {
            sender.sendMessage(chatColor + " /loyaltypoints [/lp]" + chatColor2
                    + "- Checks your Loyalty Points");
        }
        if (sender.hasPermission("loyaltypoints.check.other")) {
            sender.sendMessage(chatColor + "/lp [username]" + chatColor2
                    + "- checks the specified player's Loyalty Points");
        }
        if (sender.hasPermission("loyaltypoints.next")) {
            sender.sendMessage(chatColor + "/lp next " + chatColor2
                    + "- shows time to next payout");
        }
        if (sender.hasPermission("loyaltypoints.help")) {
            sender.sendMessage(chatColor + "/lp help " + chatColor2
                    + " - shows you this menu for help");

        }
        if (sender.hasPermission("loyaltypoints.top")) {
            sender.sendMessage(chatColor + "/lp top (amount)" + chatColor2
                    + " - shows the top 10 players");
        }
        if (sender.hasPermission("loyaltypoints.version")) {
            sender.sendMessage(chatColor + "/lp version " + chatColor2
                    + "- shows the version of LP");
        }
        if (sender.hasPermission("loyaltypoints.reload")) {
            sender.sendMessage(chatColor + "/lp reload " + chatColor2
                    + "- reloads LP");
        }
        if (sender.hasPermission("loyaltypoints.set")) {
            sender.sendMessage(chatColor + "/lp set [username] [amount] "
                    + chatColor2 + "- sets the username to amount LP.");
        }
        if (sender.hasPermission("loyaltypoints.playtime")) {
            sender.sendMessage(chatColor + "/lp playtime " + chatColor2
                    + "- shows your playtime");
        }
        if (sender.hasPermission("loyaltypoints.add")) {
            sender.sendMessage(chatColor + "/lp add [username] (amont) "
                    + chatColor2 + "- adds amount to username");
        }

        sender.sendMessage(ChatColor.DARK_AQUA + "---------" + ChatColor.GOLD
                + " LoyaltyPoints Help " + ChatColor.DARK_AQUA + "---------");

    }
}
