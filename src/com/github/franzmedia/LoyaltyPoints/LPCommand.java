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

	public LPCommand(LoyaltyPoints plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String zhf, String[] args) {
		String playerName = sender.getName();
		//permission check
		if(sender.hasPermission("loyaltypoints.general")) {
		 if (args.length == 0) {
			if (sender instanceof Player){
				if(sender.hasPermission("loyaltypoints.check.self")) {
					sender.sendMessage(plugin.selfcheckMessage.replaceAll("%PLAYERNAME%", playerName).replaceAll("%POINTS%",String.valueOf(plugin.getUsers().get(playerName).getPoint())));
				}else{
					plugin.debug("tesxt else");
				}
				 
			} else {
				sender.sendMessage(plugin.consoleCheck);
				 
			}
		}else{
			if(args[0].equalsIgnoreCase("toSQL")){
				if(sender instanceof Player) {
				}else{
					sender.sendMessage(plugin.pluginTag + " We are now moving your file browsers to SQLite/mysql (this may take a while, please wait!" );
					plugin.transformToSQL();
					
				}
			}else if(args[0].equalsIgnoreCase("add")){
				
			if(sender instanceof Player){
				if(sender.hasPermission("loyaltypoints.add")){
				
					add(sender,args);
					}else{
				
					
					sender.sendMessage(plugin.pluginTag + ChatColor.RED	+" You dont have access to this command");
					}
					
					}else{
						add(sender, args);
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
			}else if((args[0].equalsIgnoreCase("playtime") || args[0].equalsIgnoreCase("time")) && sender.hasPermission("loyaltypoints.playtime")){ 
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
						sender.sendMessage(plugin.checkotherMessage.replaceAll("%PLAYERNAME%", other1).replaceAll("%POINTS%",String.valueOf(plugin.getUsers().get(other1).getPoint())));
				
					} else{
						if (plugin.getUsers().containsKey(args[0])) {
							sender.sendMessage(plugin.checkotherMessage.replaceAll("%PLAYERNAME%", args[0]).replaceAll("%POINTS%",String.valueOf(plugin.getUsers().get(args[0]).getPoint())));
			
						} else {
							sender.sendMessage(plugin.pluginTag + ChatColor.WHITE + " Player not found.");
			
						}
					}
				}else{
					sender.sendMessage(plugin.pluginTag + ChatColor.WHITE + "You can't compare");
			
				}
			}
		}
		}
		 return true;
		
		
	
	}
			
		

		
	
	private void playtime(CommandSender sender, String[] args) {
		LPUser u = plugin.getUsers().get(sender.getName());
		int time = u.timeSinceLastRun()+u.getTotalTime();
		
		String daten = plugin.getNiceNumber(time);
		sender.sendMessage(plugin.colorize(plugin.pluginTag + "&3 You have been online for &b"+ daten ));
	
		
	}

	private void next(CommandSender sender) {
		plugin.debug(""+plugin.getTimeLeft(sender.getName()));
		int time = plugin.getTimeLeft(sender.getName());
		String daten;
		if(time >= 0){
		daten = plugin.getNiceNumber(time);
		}else{
			 daten = "very soon";
		}
		
		sender.sendMessage(plugin.colorize(plugin.pluginTag +"&3 There are around &b"+ daten+ "&3 until next payout"));
		
	}

	
private void add(CommandSender sender, String[] args){
	try{			
		plugin.getUsers().get(args[1]).increasePoint(Integer.parseInt(args[2]));
		sender.sendMessage(plugin.colorize(plugin.pluginTag +"&3 there have been added &b"+Integer.parseInt(args[2]) + "&3 points to &b"+args[1]));
	
	
	}catch(Exception exception){
		sender.sendMessage(plugin.colorize(plugin.pluginTag +"It Looks like the user aren't found"));
	}
	
	
	
	
}

private void reload(CommandSender sender) {
	plugin.onDisable();
	plugin.onEnable();
	sender.sendMessage(plugin.colorize(plugin.pluginTag  
			+ " &3reloaded points data & configuration."));
	
	}

private void version(CommandSender sender) {
	

		sender.sendMessage(plugin.colorize(plugin.pluginTag+ "&3 version &b" + plugin.getDescription().getVersion()));
		
		
		if(sender.isOp()){
			plugin.getNewestVersion();
			
			if(!plugin.upToDate()){
				sender.sendMessage(plugin.colorize(plugin.pluginTag + "&3 there are a newer version of LoyaltyPoints "+ plugin.newVersion));
				
			}
			
			
		}
}

private void set(CommandSender sender, String[] args) {
	
	
	if (args.length != 3) {
		sender.sendMessage(plugin.colorize(plugin.pluginTag + " &3/lp set &b[username] [amount]"));
	}else 	if(!plugin.getUsers().containsKey(args[1])) {
		sender.sendMessage(plugin.colorize(plugin.pluginTag + "&3 Player not found."));
		
	}else{
		
		try {
			int amount = Integer.parseInt(args[2]);
			plugin.getUsers().get(args[1]).setPoint(amount);
			sender.sendMessage(plugin.colorize(plugin.pluginTag + "&3" + args[1]+"'s&b Loyalty Points was changed to &3"+ amount));
		} catch (NumberFormatException e) {
			sender.sendMessage(plugin.colorize(plugin.pluginTag + "&rNumber expected after &b/lp set [username]"));
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

	
	 List<LPUser> users = new ArrayList<LPUser>();
	 Iterator<String> it = plugin.getUsers().keySet().iterator();
	 	  	
	 while(it.hasNext()) {
	 String player = it.next();
	users.add(plugin.getUsers().get(player));
	 }
	
	


	if (users.isEmpty()) {
		sender.sendMessage(plugin.pluginTag + ChatColor.RED + " No players in record.");
	}
	
	Collections.sort(users, new PointsComparator());


	if (maxTop > users.size()) { maxTop = users.size(); 	}
	sender.sendMessage(ChatColor.DARK_AQUA + "---------" 	+ ChatColor.GOLD + " LoyaltyPoints Top Players " + ChatColor.DARK_AQUA + "---------");
	for (int a = 0; a < maxTop; a++) {
				sender.sendMessage(ChatColor.GOLD + String.valueOf(a+1) + ". "
				+ ChatColor.AQUA + users.get(a).getName() + " - "
				+ ChatColor.DARK_AQUA + users.get(a).getPoint() + " points");
	}
	
	
	}

private void help(CommandSender sender){
	sender.sendMessage(ChatColor.DARK_AQUA + "--------- "+ChatColor.GOLD + "LoyaltyPoints Help "+ChatColor.DARK_AQUA+"---------");
	
	ChatColor chatColor = ChatColor.GOLD;
	ChatColor chatColor2 = ChatColor.AQUA;
	
	if(sender.hasPermission("loyaltypoints.check.self")){
		sender.sendMessage( chatColor + " /loyaltypoints [/lp]"+ chatColor2+ "- Checks your Loyalty Points");		
	}
	if(sender.hasPermission("loyaltypoints.check.other")){
	sender.sendMessage(chatColor + "/lp [username]" + chatColor2	+ "- checks the specified player's Loyalty Points");
	}
	if(sender.hasPermission("loyaltypoints.next")){
		sender.sendMessage(chatColor + "/lp next " + chatColor2 + "- shows time to next payout");
	}
	if(sender.hasPermission("loyaltypoints.help")){
		sender.sendMessage(chatColor + "/lp help " + chatColor2 + " - shows you this menu for help");
		
	}
	if(sender.hasPermission("loyaltypoints.top")){
		sender.sendMessage(chatColor + "/lp top (amount)" + chatColor2 + " - shows the top 10 players");	
	}
	if(sender.hasPermission("loyaltypoints.version")){
		sender.sendMessage(chatColor + "/lp version "+chatColor2 + "- shows the version of LP");
	}
	if(sender.hasPermission("loyaltypoints.reload")){
		sender.sendMessage(chatColor + "/lp reload "+chatColor2 + "- reloads LP");
	}
	if(sender.hasPermission("loyaltypoints.set")){
		sender.sendMessage(chatColor + "/lp set [username] [amount] "+chatColor2 + "- sets the username to amount LP.");
	}
	if(sender.hasPermission("loyaltypoints.playtime")){
		sender.sendMessage(chatColor + "/lp playtime "+chatColor2 + "- shows your playtime");
	}
	if(sender.hasPermission("loyaltypoints.add")){
		sender.sendMessage(chatColor + "/lp add [username] (amont) "+chatColor2 + "- adds amount to username");
	}
	
	sender.sendMessage(ChatColor.DARK_AQUA + "---------"
			+ ChatColor.GOLD + " LoyaltyPoints Help "
			+ ChatColor.DARK_AQUA + "---------");
	
}

class PointsComparator implements Comparator<LPUser> {
	public int compare(LPUser a, LPUser b) {
		return (b.getPoint() - a.getPoint());
	}
}

}


