package com.github.franzmedia.LoyaltyPoints;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

public class LPFileManager {
	public static LoyaltyPoints a;

	public LPFileManager(LoyaltyPoints isCool) {
		a = isCool;
	}

	public static boolean load(String playerName) {
		YamlConfiguration yConfig = loadList();
		if (yConfig.contains(playerName + ".Points")) { // old guy...
			int points = yConfig.getInt(playerName + ".Points");
			a.loyaltyMap.put(playerName, points);
			return true;
		} else { // must be a new guy!
			return false;
		}
	}

	public static void save() {
		YamlConfiguration yConfig = loadList();
		int i;
		List<String> usesList = new ArrayList<String>(a.loyaltyMap.keySet());
		for (i = 0; i < usesList.size(); i++) {
			String playerName = usesList.get(i);
			Integer points = a.loyaltyMap.get(playerName);
			yConfig.set(playerName + ".Points", points);
		}
		try {
			yConfig.save(a.mapFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static YamlConfiguration loadList() {
		YamlConfiguration cyril = YamlConfiguration
				.loadConfiguration(a.mapFile);
		return cyril;
	}

}
