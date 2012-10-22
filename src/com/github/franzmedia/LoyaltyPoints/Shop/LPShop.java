package com.github.franzmedia.LoyaltyPoints.Shop;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import com.github.franzmedia.LoyaltyPoints.*;

public class LPShop {
	private LoyaltyPoints plugin;

	public LPShop(LoyaltyPoints plugin) {
		this.plugin = plugin;
	}

	public void action(Player player, Sign sign) {

		// lets first make the vars needed for this price, type, and execute the
		// selected
		int price = getprice(sign.getLine(1), 7, player);
		if (price != -1) {
			LPUser user = plugin.getUser(player.getName());
			if (user.getPoint() >= price) {
				int type = getType(sign.getLine(3), player);

				if (type != -1) {
					String execution = getExecution(sign.getLine(3), type);
					if (execution.length() != 0) {

						if(execute(type, player, execution) != 0){
							plugin.getUser(player.getName()).removePoint(price);
						}else{
							player.sendMessage("yfs");
						}

					}else{
						player.sendMessage("there was a error with the execute");
					}
				}else{
					player.sendMessage("There was a error on the type");	
				}
			}else{
				player.sendMessage("you dont have enough Loyalty Points");
			}
		}else{
			player.sendMessage("There was a Error with the price, please contakt a op");
		}

	}

	private String getExecution(String line, int type) {
		String execution;
		switch(type){
		case 1: execution = line.substring(5); break;
		case 2: execution = line.substring(6); break;
		case 3: execution = line.substring(6);break;
		default: execution = ""; 
		}
		return execution;
	}

	private int getprice(String line, int start, Player player) {
		char[] chars = line.toCharArray();
		String s = "";

		for (int i = start; i < chars.length; i++) {
			s = s + chars[i];
		}
		int price = -1;
		try {
			price = Integer.parseInt(s);
		} catch (NumberFormatException ex) {
			player.sendMessage("There are a Error with this sign, please contact a op");
		}
		return price;
	}

	private int getType(String line, Player player) {
		int type = -1;
		
		if(line.toLowerCase().startsWith("[cmd]")){
			type = 1;
		}else if(line.toLowerCase().startsWith("[give]")){
			type = 2;
		}else if(line.toLowerCase().startsWith("[perm]")){
			type = 3;
		}
		
		return type;
	}

	private int execute(int type, Player player, String execution) {
		int result = -1;
		switch (type) {
		case 1:
			result = executeCMD(player, execution);
			break;
		case 2:
			result = executeGive(player, execution);
			break;
		}

		return result;
	}

	private int executeGive(Player player, String item) {
		int result = -1;
		try {
			String cmd = "give " + player.getName() + item;
			player.sendMessage(cmd);
			plugin.getServer().dispatchCommand(
					plugin.getServer().getConsoleSender(),
					cmd);
			result = 1;
		} catch (Exception ex) {
			player.sendMessage(plugin.getLptext().getPluginTag()
					+ " There have happend a error, please contact a op :(");
		}

		return result;
	}

	private int executeCMD(Player player, String CMD) {

		return 0;
	}

	public void createSign(SignChangeEvent event) {
		
		if(event.getLine(0).equalsIgnoreCase("[LoyaltyShop]")){
			if(!event.getPlayer().hasPermission("loyaltypoints.shop.create")){
				event.setCancelled(true);
				event.getBlock().breakNaturally();
				event.getPlayer().sendMessage("You dont have permission to create this shop.");
			}else{
				event.setLine(0,ChatColor.GOLD+event.getLine(0));	
				event.getPlayer().sendMessage("Your shop has been created :)");
			}
		}
		
	}

}
