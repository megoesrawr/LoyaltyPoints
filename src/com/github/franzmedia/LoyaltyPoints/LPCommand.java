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

	public LPCommand(LoyaltyPoints LP) {
		plugin = LP;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String zhf, String[] args) {
		String playerName = sender.getName();
		boolean returnstr = false;
		if (args.length == 0) {
			if (sender instanceof Player && sender.hasPermission("loyaltypoints.check")) {
				sender.sendMessage(plugin.selfcheckMessage.replaceAll("%PLAYERNAME%", playerName).replaceAll("%POINTS%",String.valueOf(plugin.loyaltyMap.get(playerName))));
				returnstr = true;
			} else {
				sender.sendMessage(plugin.consoleCheck);
				returnstr = false;
			}
		}else{
			if(args[0].equalsIgnoreCase("help")) {
				/* HELP COMMAND */ 
				returnstr = help(sender);
				
			}else if(args[0].equalsIgnoreCase("top") && sender.hasPermission("loyaltypoints.top")) {
				/* TOP COMMAND */ 
				returnstr = top(sender,args);
				
			}else if (args[0].equalsIgnoreCase("set") && sender.hasPermission("loyaltypoints.set")) {
				/* SET COMMAND */ 
				returnstr = set(sender, args[1], args);
				
			}else if (args[0].equalsIgnoreCase("version") && sender.hasPermission("loyaltypoints.version")) {
				/* VERSION COMMAND */ 
				returnstr = version(sender);
				
			}else if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("loyaltypoints.reload")) {
				/* RELOAD COMMAND */ 
				returnstr = reload(sender);
				
			}else if(args[0].equalsIgnoreCase("next") && sender.hasPermission("loyaltypoints.next")){
				/* NEXT COMMAND */ 
				returnstr = next(sender);
	/*		}else if(args[0].equalsIgnoreCase("playtime") && sender.hasPermission("loyaltypoints.playtime")){
			//	 PlayTime 
				returnstr = playtime(sender,args); 
				
	*/			
			}else{	
				// compare other ppl
			
				if (sender.hasPermission("loyaltypoints.check.other")) {
					Player trick = Bukkit.getPlayer(args[0]);
					if (trick != null) {
						String other1 = trick.getName();
						sender.sendMessage(plugin.checkotherMessage.replaceAll("%PLAYERNAME%", other1).replaceAll("%POINTS%",String.valueOf(plugin.loyaltyMap.get(other1))));
						returnstr = true;
					} else{
						if (plugin.loyaltyMap.containsKey(args[0])) {
							sender.sendMessage(plugin.checkotherMessage.replaceAll("%PLAYERNAME%", args[0]).replaceAll("%POINTS%",String.valueOf(plugin.loyaltyMap.get(args[0]))));
						} else {
							sender.sendMessage(plugin.pluginTag + ChatColor.WHITE + " Player not found.");
						}
					}
				}
			}
		}
	return returnstr;
	}
			
		

		
	/*
	private boolean playtime(CommandSender sender, String[] args) {
		
		sender.sendMessage(plugin.pluginTag + ChatColor.WHITE + "Currently: " + plugin.getNiceNumber(plugin.getPlayTime(sender.getName())));
				
		
		return true;
	} */

	private boolean next(CommandSender sender) {
		String daten = plugin.getNiceNumber((int) (plugin.cycleNumber*1000-(new Date().getTime() - plugin.timeComparison.get(sender.getName()))));
		sender.sendMessage(plugin.pluginTag + ChatColor.WHITE + daten );
	return true;
		
	}

	


private boolean reload(CommandSender sender) {
	plugin.onDisable();
	plugin.onEnable();
	sender.sendMessage(plugin.pluginTag + ChatColor.WHITE
			+ " reloaded points data & configuration.");
	return true;
	}

private boolean version(CommandSender sender) {
	

		sender.sendMessage(plugin.pluginTag + ChatColor.WHITE + " version " + plugin.getDescription().getVersion());
return true;
}

private boolean set(CommandSender sender, String setPlayer, String[] args) {
	boolean returnstr = true;
	
	if (!(args.length == 3)) {
		sender.sendMessage(ChatColor.RED  + "/lp set [username] [amount]");
	}else 	if(!plugin.loyaltyMap.containsKey(setPlayer)) {
		sender.sendMessage(ChatColor.RED  + "Player not found.");
		
	}else{	
		try {
			int amount = Integer.parseInt(args[2]);
			plugin.loyaltyMap.put(setPlayer, amount);
			sender.sendMessage(plugin.pluginTag + ChatColor.WHITE + setPlayer+"s Loyalty Points was changed to "+ amount);
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + "Number expected after /lp delete [username]");
		}
	}
	return returnstr;
}

private boolean top(CommandSender sender,String[] args) {
boolean returnstr = true;
	int maxTop = 10;
	if (args.length == 2) {
		try {
			maxTop = Integer.parseInt(args[1]);
		} catch (NumberFormatException nfe) { // if args contains other that integers.
			sender.sendMessage(plugin.pluginTag + ChatColor.RED	+ "Number expected after /lp top");
		}
	}

	List<User> users = new ArrayList<User>();
	Iterator<String> it = plugin.loyaltyMap.keySet().iterator();
	while(it.hasNext()) {
		String player = it.next();
		users.add(new User(player, plugin.loyaltyMap.get(player)));
	 }

	if (users.isEmpty()) {
		sender.sendMessage(plugin.pluginTag + ChatColor.RED + " No players in record.");
	}

	Collections.sort(users, new PointsComparator());

	if (maxTop > users.size()) { maxTop = users.size(); 	}
	sender.sendMessage(ChatColor.DARK_AQUA + "---------" 	+ ChatColor.AQUA + " LoyaltyPoints Top Players " + ChatColor.DARK_AQUA + "---------");
	for (int a = 0; a < maxTop; a++) {
				sender.sendMessage(ChatColor.GREEN + String.valueOf(a+1) + ". "
				+ ChatColor.DARK_AQUA + users.get(a).name + " - "
				+ ChatColor.BLUE + users.get(a).points + " points");
	}
	
		return returnstr;
	}

private boolean help(CommandSender sender){
	sender.sendMessage(ChatColor.DARK_AQUA + "---------" + ChatColor.AQUA + " LoyaltyPoints Help " + ChatColor.DARK_AQUA + "---------");
	sender.sendMessage(ChatColor.AQUA + "/loyaltypoints [/lp] "	+ ChatColor.GREEN + " - Checks your Loyalty Points");
	sender.sendMessage(ChatColor.AQUA + "/lp [username]" + ChatColor.GREEN	+ "- checks the specified player's Loyalty Points");
	sender.sendMessage(ChatColor.AQUA + "/lp top (amount)" + ChatColor.GREEN + " - shows the top 10 players");
	
	sender.sendMessage(ChatColor.DARK_AQUA + "---------"
			+ ChatColor.AQUA + " LoyaltyPoints Help "
			+ ChatColor.DARK_AQUA + "---------");
	return true;
}

class PointsComparator implements Comparator<User> {
	public int compare(User a, User b) {
		return (b.points - a.points);
	}
}

class User {
	private String name;
	private int points;

	public User(String name, int points) {
		this.name = name;
		this.points = points;
	}
}
}


