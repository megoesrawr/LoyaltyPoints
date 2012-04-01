package com.github.franzmedia.LoyaltyPoints;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

public class LPFileManager {
	private static File mapFile;
	public static LoyaltyPoints plugin;

	public LPFileManager(LoyaltyPoints isCool) {
		plugin = isCool;
		mapFile = new File(plugin.getDataFolder(), "points.yml");
	}

	public static boolean load(String playerName) {
		YamlConfiguration yConfig = YamlConfiguration.loadConfiguration(mapFile);
		if (yConfig.contains(playerName + ".points")) { // old guy...
			int points = yConfig.getInt(playerName + ".points");
			
			int time = 0;
			time = yConfig.getInt(playerName + ".time");
			int totalTime = yConfig.getInt(playerName + ".totalTime");
			plugin.getLoyaltTotalTime().put(playerName, totalTime);
			plugin.getLoyaltyPoints().put(playerName, points);
			plugin.debug("FM TIME"+time);
			plugin.getLoyaltTime().put(playerName, time );
			return true;
		} else { // must be a new guy!
			return false;
		}
	}

	public static void save() {
		YamlConfiguration yConfig = YamlConfiguration.loadConfiguration(mapFile);
		int i;
		List<String> userList = new ArrayList<String>(plugin.getLoyaltyPoints().keySet());
		for (i = 0; i < userList.size(); i++) {
			String playerName = userList.get(i);
			
			
			int points = plugin.getLoyaltyPoints().get(playerName);
			plugin.debug(plugin.getLoyaltTime().get(playerName)+"");
			int time = plugin.getLoyaltTime().get(playerName);
			int totalTime = plugin.getLoyaltTotalTime().get(playerName);
			yConfig.set(playerName + ".points", points);
			yConfig.set(playerName + ".time", time);
			yConfig.set(playerName + ".totalTime", totalTime);
		}
		try { // COULDN'T SAVE IT
			yConfig.save(mapFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
