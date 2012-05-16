package com.github.franzmedia.LoyaltyPoints;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

public class LPFileManager {
	
	private static LoyaltyPoints plugin;

	public LPFileManager(LoyaltyPoints pluginet) {
		plugin = pluginet;
	}

	public static boolean load(String playerName) {
		YamlConfiguration yConfig = YamlConfiguration.loadConfiguration(plugin.getMapFile());
		if (yConfig.contains(playerName + ".points")) { // old guy...
			int points = yConfig.getInt(playerName + ".points");
			int time = 0;
			time = yConfig.getInt(playerName + ".time");
			int totalTime = yConfig.getInt(playerName + ".totalTime");
			LPUser user = new LPUser(playerName, points, time, totalTime, new Date().getTime());
			plugin.insertUser(user);
			return true;
		} else { // must be a new guy!
			return false;
		}
	}

	public static void save() {
		YamlConfiguration yConfig = YamlConfiguration.loadConfiguration(plugin.getMapFile());
		int i;
		List<String> userList = new ArrayList<String>(plugin.getUsers().keySet());
		for (i = 0; i < userList.size(); i++) {
			String playerName = userList.get(i);
			LPUser user = plugin.getUsers().get(userList.get(i));
			
			int points = user.getPoint();
			int time = user.getTime();
			int totalTime = user.getTotalTime();
			yConfig.set(playerName + ".points", points);
			yConfig.set(playerName + ".time", time);
			yConfig.set(playerName + ".totalTime", totalTime);
		}
		try { // Trying to save it!
			yConfig.save(plugin.getMapFile());
		} catch (IOException e) { // couldn't
			e.printStackTrace();
		}
	}
}
