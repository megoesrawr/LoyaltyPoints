package com.github.franzmedia.LoyaltyPoints;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.bukkit.configuration.file.YamlConfiguration;

public class LPFileManager {

	private static LoyaltyPoints plugin;
	private final YamlConfiguration yConfig;

	public LPFileManager(final LoyaltyPoints pluginet) {
		plugin = pluginet;
		yConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(),"points.yml"));
	}

	public boolean load(final String playerName) {

		if (yConfig.contains(playerName + ".points")) { // old guy...
			final int points = yConfig.getInt(playerName + ".points");
			int time = 0;
			time = yConfig.getInt(playerName + ".time");
			final int totalTime = yConfig.getInt(playerName + ".totalTime");
			final LPUser user = new LPUser(plugin, playerName, points, time,
					totalTime, new Date().getTime());
			plugin.insertUser(user);
			return true;
		} else { // must be a new guy!
			return false;
		}
	}

	public void save() {

		for (final LPUser user : plugin.getUsers().values()) {
			final String playerName = user.getName();
			final int points = user.getPoint();
			final int time = user.getTime();
			final int totalTime = user.getTotalTime();
			yConfig.set(playerName + ".points", points);
			yConfig.set(playerName + ".time", time);
			yConfig.set(playerName + ".totalTime", totalTime);
		}
		try { // Trying to save it!
			yConfig.save(new File(plugin.getDataFolder(),"points.yml"));
		} catch (final IOException e) { // couldn't
			e.printStackTrace();
		}
	}
}
