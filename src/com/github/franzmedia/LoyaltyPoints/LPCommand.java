/* 
 * AUTHOR: Kasper Franz
 * Loyalty Points 1.0.9
 * Last Changed: Added general permission 
 */

package com.github.franzmedia.LoyaltyPoints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
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
						sender.sendMessage(lptext.getSelfcheckMessage().replaceAll("%PLAYERNAME%", playerName).replaceAll("%POINTS%", plugin.getUsers().get(playerName).getPoint()+""));
					}

				} else {
					sender.sendMessage(lptext.getConsoleCheck());
				}
			} else {
				if (args[0].equalsIgnoreCase("toSQL")) {
					if (sender instanceof Player) {
					} else {
						sender.sendMessage(lptext.getToMySQL());
						plugin.transformToSQL();
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
					/* HELP COMMAND */
					help(sender);

				} else if (args[0].equalsIgnoreCase("top")
						&& sender.hasPermission("loyaltypoints.top")) {
					/* TOP COMMAND */
					top(sender, args);

				} else if (args[0].equalsIgnoreCase("set")
						&& sender.hasPermission("loyaltypoints.set")) {
					/* SET COMMAND */
					set(sender, args);

				} else if (args[0].equalsIgnoreCase("version")
						&& sender.hasPermission("loyaltypoints.version")) {
					/* VERSION COMMAND */
					version(sender);

				} else if (args[0].equalsIgnoreCase("reload")
						&& sender.hasPermission("loyaltypoints.reload")) {
					/* RELOAD COMMAND */
					reload(sender);
				} else if (args[0].equalsIgnoreCase("next")
						&& sender.hasPermission("loyaltypoints.next")) {
					if (sender instanceof Player) {
						/* NEXT COMMAND */
						next(sender);
					} else { // is cmd
						sender.sendMessage(lptext.getConsoleCheck());

					}
				} else if ((args[0].equalsIgnoreCase("playtime") || args[0]
						.equalsIgnoreCase("time"))
						&& sender.hasPermission("loyaltypoints.playtime")) {
					if (sender instanceof Player) {
						/* PlayTime */

						playtime(sender, args);
					} else { // is cmd
						sender.sendMessage(lptext.getConsoleCheck());

					}

				} else {
					// compare other ppl

					if (sender.hasPermission("loyaltypoints.check.other")) {
						final Player trick = Bukkit.getPlayer(args[0]);
						if (trick != null) {
							final String other1 = trick.getName();
							sender.sendMessage(lptext
									.getCheckotherMessage()
									.replaceAll("%PLAYERNAME%", other1)
									.replaceAll(
											"%POINTS%",
											String.valueOf(plugin.getUsers()
													.get(other1).getPoint())));

						} else {
							if (plugin.getUsers().containsKey(args[0])) {
								sender.sendMessage(lptext
										.getCheckotherMessage()
										.replaceAll("%PLAYERNAME%", args[0])
										.replaceAll(
												"%POINTS%",
												String.valueOf(plugin
														.getUsers()
														.get(args[0])
														.getPoint())));

							} else {
								sender.sendMessage(lptext.getNoUser());

							}
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
		final LPUser u = plugin.getUsers().get(sender.getName());
		final int time = u.timeSinceLastRun() + u.getTotalTime();

		final String daten = plugin.getNiceNumber(time);
		sender.sendMessage(lptext.getOnlinetime().replaceAll("%ONLINETIME%", daten));

	}

	private void next(final CommandSender sender) {
		
		LPUser user = plugin.getUsers().get(sender.getName());
		final int time = user.getTimeLeft();
		String daten;
		if (time >= 0) {
			daten = plugin.getNiceNumber(time);
			
			sender.sendMessage(lptext.getNext().replaceAll("%TIME%", daten));
		} else {
			user.givePoint();
			next(sender);
		}

		

	}

	private void add(final CommandSender sender, final String[] args) {
		try {
			plugin.getUsers().get(args[1])
					.increasePoint(Integer.parseInt(args[2]));
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

		sender.sendMessage(lptext.getNowVersion().replaceAll("%VERSION%", plugin.getDescription().getVersion()));

		if (sender.isOp()) {
			plugin.getNewestVersion();

			if (!plugin.upToDate()) {
				sender.sendMessage(lptext.getNewVersionAvalible().replaceAll("%NEWVERSION%", plugin.newVersion));

			}

		}
	}

	private void set(final CommandSender sender, final String[] args) {

		if (args.length != 3) {
			sender.sendMessage(lptext.getHelpSet());
		} else if (!plugin.getUsers().containsKey(args[1])) {
			sender.sendMessage(lptext.getErrorUnknownUser());

		} else {

			try {
				final int amount = Integer.parseInt(args[2]);
				plugin.getUsers().get(args[1]).setPoint(amount);
				sender.sendMessage(lptext.getNewSetAmount().replaceAll("%PLAYER%", args[1]).replaceAll("%AMOUNT%", amount+""));
			} catch (final NumberFormatException e) {
				sender.sendMessage(lptext.getErrorNumber() + "/lp set [username]");
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
			}
		}

		final List<LPUser> users = new ArrayList<LPUser>();
		final Iterator<String> it = plugin.getUsers().keySet().iterator();

		while (it.hasNext()) {
			final String player = it.next();
			users.add(plugin.getUsers().get(player));
		}

		if (users.isEmpty()) {
			sender.sendMessage(lptext.getErrorNoUsers());
		}

		Collections.sort(users, new PointsComparator());

		if (maxTop > users.size()) {
			maxTop = users.size();
		}
		sender.sendMessage(ChatColor.DARK_AQUA + "---------" + ChatColor.GOLD
				+ " LoyaltyPoints Top Players " + ChatColor.DARK_AQUA
				+ "---------");
		for (int a = 0; a < maxTop; a++) {
			sender.sendMessage(ChatColor.GOLD + String.valueOf(a + 1) + ". "
					+ ChatColor.AQUA + users.get(a).getName() + " - "
					+ ChatColor.DARK_AQUA + users.get(a).getPoint() + " points");
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

	class PointsComparator implements Comparator<LPUser> {
		@Override
		public int compare(final LPUser a, final LPUser b) {
			return (b.getPoint() - a.getPoint());
		}
	}

}
