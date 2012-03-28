package com.github.franzmedia.LoyaltyPoints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

	public LPCommand(LoyaltyPoints isCool) {
		plugin = isCool;
		
	}

	public boolean onCommand(CommandSender sender, Command cmd, String zhf,
			String[] args) {
		String leo = sender.getName();
		if (args.length == 0) {
			if (sender instanceof Player
					& sender.hasPermission("loyaltypoints.check")) {
				sender.sendMessage(plugin.selfcheckMessage.replaceAll(
						"%PLAYERNAME%", leo).replaceAll("%POINTS%",
						String.valueOf(plugin.loyaltyMap.get(leo))));
				return true;
			} else {
				sender.sendMessage("[LoyaltyPoints] Sorry, I don't track consoles.");
				return true;
			}
		}

		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("help")) {
				sender.sendMessage(ChatColor.DARK_AQUA + "---------"
						+ ChatColor.AQUA + " LoyaltyPoints Help "
						+ ChatColor.DARK_AQUA + "---------");
				sender.sendMessage(ChatColor.AQUA + "/loyaltypoints [/lp] "
						+ ChatColor.GREEN + " - Ser dine Loyaltychecks your Loyalty Points");
				sender.sendMessage(ChatColor.AQUA + "/lp [username]"
						+ ChatColor.GREEN
						+ "- checks the specified player's Loyalty Points");
				sender.sendMessage(ChatColor.AQUA + "/lp top (amount)"
						+ ChatColor.GREEN + " - shows the top 10 players");
				return true;
			}

			if (args[0].equalsIgnoreCase("top")	&& sender.hasPermission("loyaltypoints.top")) {

				int b = 10;
				if (args.length == 2) {
					try {
						b = Integer.parseInt(args[1]);
					} catch (NumberFormatException nfe) {
						sender.sendMessage(ChatColor.RED
								+ "Number expected after /lp top");
						return true;
					}
				}

				List<User> users = new ArrayList<User>();
				for (Iterator<String> it = plugin.loyaltyMap.keySet()
						.iterator(); it.hasNext();) {
					String player = it.next();
					users.add(new User(player, plugin.loyaltyMap.get(player)));
				}

				if (users.isEmpty()) {
					sender.sendMessage(plugin.pluginTag + ChatColor.RED
							+ " No players in record.");
					return true;
				}

				Collections.sort(users, new PointsComparator());

				if (b > users.size()) {
					b = users.size();
				}
				sender.sendMessage(ChatColor.DARK_AQUA + "---------"
						+ ChatColor.AQUA + " LoyaltyPoints Top Players "
						+ ChatColor.DARK_AQUA + "---------");
				for (int a = 0; a < b; a++) {
					String c = String.valueOf(a + 1);
					sender.sendMessage(ChatColor.GREEN + c + ". "
							+ ChatColor.DARK_AQUA + users.get(a).name + " - "
							+ ChatColor.BLUE + users.get(a).points + " points");
				}
				return true;
			} // end of command argument

			if (args[0].equalsIgnoreCase("set")) {
				if (sender.hasPermission("loyaltypoints.set")) {
					if (!(args.length == 3)) {
						sender.sendMessage(ChatColor.RED
								+ "/lp set [username] [amount]");
						return true;
					}
					try {
						int amount = Integer.parseInt(args[2]);
						if (!plugin.loyaltyMap.containsKey(args[1])) {
							sender.sendMessage(ChatColor.RED
									+ "Player not found.");
							return true;
						}
						plugin.loyaltyMap.put(args[1], amount);
						return true;
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED
								+ "Number expected after /lp delete [username]");
					}
				}
			}

			if (args[0].equalsIgnoreCase("version")) {
				if (sender.hasPermission("loyaltypoints.version")) {
					sender.sendMessage(plugin.pluginTag + ChatColor.WHITE
							+ " version "
							+ plugin.getDescription().getVersion());
					return true;
				}
			}

			if (args[0].equalsIgnoreCase("reload")) {
				if (sender.hasPermission("loyaltypoints.reload")) {
					LPFileManager.save();
					plugin.loadPointsData();
					plugin.loadVariables();
					sender.sendMessage(plugin.pluginTag + ChatColor.WHITE
							+ " reloaded points data & configuration.");
					return true;
				}
			}
			
			if(args[0].equalsIgnoreCase("next")){
				if(sender.hasPermission("loyaltypoints.next")){
				String daten = plugin.getNiceNumber((int) (plugin.cycleNumber*1000-(new Date().getTime() - plugin.timeComparison.get(leo))));
					sender.sendMessage(plugin.pluginTag + ChatColor.WHITE + daten );
				return true;
				}
						
			}

			if (sender.hasPermission("loyaltypoints.check.other")) {
				Player trick = Bukkit.getPlayer(args[0]);
				if (trick != null) {
					String other1 = trick.getName();
					sender.sendMessage(plugin.checkotherMessage.replaceAll(
							"%PLAYERNAME%", other1).replaceAll("%POINTS%",
							String.valueOf(plugin.loyaltyMap.get(other1))));
					return true;
				} else if (trick == null) {
					if (plugin.loyaltyMap.containsKey(args[0])) {
						sender.sendMessage(plugin.checkotherMessage.replaceAll(
								"%PLAYERNAME%", args[0]).replaceAll("%POINTS%",
								String.valueOf(plugin.loyaltyMap.get(args[0]))));
					} else {
						sender.sendMessage(plugin.pluginTag + ChatColor.WHITE
								+ " Player not found.");
					}
					return true;
				}
			}
		}

		return false;
	}

}

class PointsComparator implements Comparator<User> {
	public int compare(User a, User b) {
		return (b.points - a.points);
	}
}

class User {
	public String name;
	public int points;

	public User(String name, int points) {
		this.name = name;
		this.points = points;
	}

}