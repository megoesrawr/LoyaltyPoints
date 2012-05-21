package com.github.franzmedia.LoyaltyPoints;

import java.io.IOException;
import java.util.Date;
import org.bukkit.configuration.file.YamlConfiguration;

public class LPFileManager {
	
	private static LoyaltyPoints plugin;
	private YamlConfiguration yConfig;
	public LPFileManager(LoyaltyPoints pluginet) {
		plugin = pluginet;
		 yConfig = YamlConfiguration.loadConfiguration(plugin.getMapFile());
	}

	public boolean load(String playerName) {
		
		if (yConfig.contains(playerName + ".points")) { // old guy...
			int points = yConfig.getInt(playerName + ".points");
			int time = 0;
			time = yConfig.getInt(playerName + ".time");
			int totalTime = yConfig.getInt(playerName + ".totalTime");
			LPUser user = new LPUser(plugin,playerName, points, time, totalTime, new Date().getTime());
			plugin.insertUser(user);
			return true;
		} else { // must be a new guy!
			return false;
		}
	}

	public void save() {
		
		 for(LPUser user : plugin.getUsers().values()){
			String playerName = user.getName();
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
