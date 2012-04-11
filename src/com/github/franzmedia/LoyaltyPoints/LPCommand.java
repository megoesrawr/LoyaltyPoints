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

	public LPCommand(LoyaltyPoints plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String zhf, String[] args) {
		String playerName = sender.getName();
		if (args.length == 0) {
			if (sender instanceof Player){
				if(sender.hasPermission("loyaltypoints.check.self")) {
					sender.sendMessage(plugin.selfcheckMessage.replaceAll("%PLAYERNAME%", playerName).replaceAll("%POINTS%",String.valueOf(plugin.getLoyaltyPoints().get(playerName))));
				}else{
					plugin.debug("tesxt else");
				}
				 
			} else {
				sender.sendMessage(plugin.consoleCheck);
				 
			}
		}else{
			
			if(args[0].equalsIgnoreCase("add")){
				
				if(sender instanceof Player && sender.hasPermission("loyaltypoints.add")){
					add(sender,args);
				}else{
					add(sender,args);	
				}

				
			
			}else if(args[0].equalsIgnoreCase("help")) {
				/* HELP COMMAND */ 
				 help(sender);
				
			}else if(args[0].equalsIgnoreCase("top") && sender.hasPermission("loyaltypoints.top")) {
				/* TOP COMMAND */ 
				 top(sender,args);
				
			}else if (args[0].equalsIgnoreCase("set") && sender.hasPermission("loyaltypoints.set")) {
				/* SET COMMAND */ 
				set(sender, args);
				
			}else if (args[0].equalsIgnoreCase("version") && sender.hasPermission("loyaltypoints.version")) {
				/* VERSION COMMAND */ 
				version(sender);
				
			}else if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("loyaltypoints.reload")) {
				/* RELOAD COMMAND */ 
				reload(sender);
			}else if(args[0].equalsIgnoreCase("next") && sender.hasPermission("loyaltypoints.next")){
				if (sender instanceof Player){
					/* NEXT COMMAND */ 
					next(sender);
				}else{ // is cmd
					sender.sendMessage(plugin.consoleCheck);
					
				}
			}else if(args[0].equalsIgnoreCase("playtime") && sender.hasPermission("loyaltypoints.playtime")){ 
				if (sender instanceof Player){
					/*	 PlayTime */
								
					 playtime(sender,args);
				}else{ // is cmd
					sender.sendMessage(plugin.consoleCheck);
					
				}
				 
				
				
			}else{	
				// compare other ppl
			
				if (sender.hasPermission("loyaltypoints.check.other")) {
					Player trick = Bukkit.getPlayer(args[0]);
					if (trick != null) {
						String other1 = trick.getName();
						sender.sendMessage(plugin.checkotherMessage.replaceAll("%PLAYERNAME%", other1).replaceAll("%POINTS%",String.valueOf(plugin.getLoyaltyPoints().get(other1))));
				
					} else{
						if (plugin.getLoyaltyPoints().containsKey(args[0])) {
							sender.sendMessage(plugin.checkotherMessage.replaceAll("%PLAYERNAME%", args[0]).replaceAll("%POINTS%",String.valueOf(plugin.getLoyaltyPoints().get(args[0]))));
			
						} else {
							sender.sendMessage(plugin.pluginTag + ChatColor.WHITE + " Player not found.");
			
						}
					}
				}else{
					sender.sendMessage(plugin.pluginTag + ChatColor.WHITE + "You can't compare");
			
				}
			}
		}
		return true;
		

	
	}
			
		

		
	
	private void playtime(CommandSender sender, String[] args) {
		plugin.debug(""+plugin.getPlayTime(sender.getName()));
		String daten = plugin.getNiceNumber(plugin.getPlayTime(sender.getName()));
		
		sender.sendMessage(plugin.pluginTag + ChatColor.WHITE + "You have been online for "+ daten );
	
		
	}

	private void next(CommandSender sender) {
		plugin.debug(""+plugin.getTimeLeft(sender.getName()));
		String daten = plugin.getNiceNumber(plugin.getTimeLeft(sender.getName()));
		
		sender.sendMessage(plugin.pluginTag + ChatColor.WHITE + "There are around "+ daten+ " until next payout" );
		
	}

	
private void add(CommandSender sender, String[] args){
	int beforepoint = plugin.getLoyaltyPoints().get(args[1]);
	int addPoints = Integer.parseInt(args[2]);
	try{	
		String player = args[1];
		int newpoints = beforepoint+addPoints;
		plugin.debug("bef:"+beforepoint+" add:"+addPoints+ " newP: "+newpoints);
		
		plugin.getLoyaltyPoints().put(player, newpoints);
		sender.sendMessage("THERE ARE NOW added "+Integer.parseInt(args[2]) + " points to "+args[1]);
	
	
	}catch(Exception exception){
		sender.sendMessage("ooops error");
	}
	
	
	
	
}

private void reload(CommandSender sender) {
	plugin.onDisable();
	plugin.onEnable();
	sender.sendMessage(plugin.pluginTag + ChatColor.WHITE
			+ " reloaded points data & configuration.");
	
	}

private void version(CommandSender sender) {
	

		sender.sendMessage(plugin.pluginTag + ChatColor.WHITE + " version " + plugin.getDescription().getVersion());

}

private void set(CommandSender sender, String[] args) {
	
	
	if (args.length != 3) {
		sender.sendMessage(ChatColor.RED  + "/lp set [username] [amount]");
	}else 	if(!plugin.getLoyaltyPoints().containsKey(args[1])) {
		sender.sendMessage(ChatColor.RED  + "Player not found.");
		
	}else{
		String setPlayer = args[1];
		try {
			int amount = Integer.parseInt(args[2]);
			plugin.getLoyaltyPoints().put(setPlayer, amount);
			sender.sendMessage(plugin.pluginTag + ChatColor.WHITE + setPlayer+"s Loyalty Points was changed to "+ amount);
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + "Number expected after /lp delete [username]");
		}
	}
	
}

private void top(CommandSender sender,String[] args) {
	int maxTop = 10;
	if (args.length == 2) {
		try {
			maxTop = Integer.parseInt(args[1]);
		} catch (NumberFormatException nfe) { // if args contains other that integers.
			sender.sendMessage(plugin.pluginTag + ChatColor.RED	+ "Number expected after /lp top");
		}
	}

	List<User> users = new ArrayList<User>();
	Iterator<String> it = plugin.getLoyaltyPoints().keySet().iterator();
	while(it.hasNext()) {
		String player = it.next();
		users.add(new User(player, plugin.getLoyaltyPoints().get(player)));
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
	
	
	}

private void help(CommandSender sender){
	sender.sendMessage(ChatColor.DARK_AQUA + "---------" + ChatColor.AQUA + " LoyaltyPoints Help " + ChatColor.DARK_AQUA + "---------");
	sender.sendMessage(ChatColor.AQUA + "/loyaltypoints [/lp] "	+ ChatColor.GREEN + " - Checks your Loyalty Points");
	sender.sendMessage(ChatColor.AQUA + "/lp [username]" + ChatColor.GREEN	+ "- checks the specified player's Loyalty Points");
	sender.sendMessage(ChatColor.AQUA + "/lp top (amount)" + ChatColor.GREEN + " - shows the top 10 players");
	
	sender.sendMessage(ChatColor.DARK_AQUA + "---------"
			+ ChatColor.AQUA + " LoyaltyPoints Help "
			+ ChatColor.DARK_AQUA + "---------");
	
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


