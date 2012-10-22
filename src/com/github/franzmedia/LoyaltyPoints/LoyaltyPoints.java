/* 
 * AUTHOR: Kasper Franz
 * Loyalty Points 1.1.3
 * Last Changed: moved ALL with database to the databaseHandler 
 * 
 */

/*
 * Planned Features Possibility to pay a defined amount of money when a
 * player gains a specified amount of LoyaltyPoints Only pay points if the
 * Server-wide announcements when a player gains a certain
 * amount of points (reaches a point milestone) Receive item rewards on
 * specified point milestones
 */

package com.github.franzmedia.LoyaltyPoints;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.franzmedia.LoyaltyPoints.Metrics.Metric;
import com.github.franzmedia.LoyaltyPoints.Shop.LPShop;

public class LoyaltyPoints extends JavaPlugin {
	private Logger logger;

	private int debug = 0;
	private final Map<String, LPUser> users = new HashMap<String, LPUser>();
	private LPConfig config;
	private LPTexts lptext;
	private LPShop shop;

	
	@Override
	public void onEnable() {
		logger = Logger.getLogger("Minecraft");

		// Getting the texts
		lptext = new LPTexts(this);

		// loading the variables from the config! + making the config object
		config = new LPConfig(this.getConfig(), this, logger);
		
		// new Shop if active
		if(config.shopActive()){
			shop = new LPShop(this);
		}
		
		// loading the texts from the config!
		lptext.loadText();

		// loading the points (If there are any online)!!!
		loadOnlineUsers();

		// Setting up the listener (player login/logout & move)
		this.getServer().getPluginManager()
				.registerEvents(new LPListener(this), this);

		// Commands setup!
		getCommand("lp").setExecutor(new LPCommand(this, lptext));

		// Telling the server we are up and running
		info(getDescription(), "enabled");

		// Making scheduler (giving points after the update timer)
		getServer().getScheduler().scheduleAsyncRepeatingTask(this,
				new LPScheduler(this), config.getUpdateTimer(),
				config.getUpdateTimer());

		// Enable metrics (MCstats.org)
		Metrics();

	}

	@Override
	public void onDisable() {

		giveOnlineUsersPoints();
		saveOnlineUsers();
		users.clear();
		info(this.getDescription(), "disabled");
	}

	public void loadOnlineUsers() {
		for (final Player player : getServer().getOnlinePlayers()) {
			if (player.hasPermission("loyaltypoints.general")) {
				users.put(player.getName(),
						config.getDatabase().GetUser(player.getName()));
			}
		}
	}

	public void stop() {
		setEnabled(false);
	}

	public String getNiceNumber(int millsec) {
		String str = "";
		int g = 0;

		if (millsec >= 31556926) { // year
			g = 1;
			str = str + (millsec / 31556926) + "y";
			millsec = millsec - ((millsec / 31556926) * 31556926);

		}

		if (millsec >= 2629744) { // month
			if (g == 1) {
				str = str + ", ";
				g = 0;
			}
			str = str + (millsec / 2629744) + "m";
			millsec = millsec - ((millsec / 2629744) * 2629744);
			g = 1;
		}

		if (millsec >= 86400) { // day
			if (g == 1) {
				str = str + ", ";
				g = 0;
			}
			str = str + (millsec / 86400) + "d";
			millsec = millsec - ((millsec / 86400) * 86400);
			g = 1;
		}
		if (millsec >= 3600) { // time
			if (g == 1) {
				str = str + ", ";
				g = 0;
			}
			str = str + (millsec / 3600) + "h";
			millsec = millsec - ((millsec / 3600) * 3600);
			g = 1;
		}
		if (millsec >= 60) { // min
			if (g == 1) {
				str = str + ", ";
				g = 0;
			}
			str = str + (millsec / 60) + "m";
			millsec = millsec - ((millsec / 60) * 60);
			g = 1;
		}

		if (millsec >= 1) {
			if (g == 1) {
				str = str + ", ";

			}
			str = str + millsec + "s";
			millsec = millsec - millsec;

		}

		return str;
	}

	public void info(final PluginDescriptionFile pdf, final String status) {
		this.logger.info("[LoyaltyPoints] Version " + pdf.getVersion()
				+ " by Franzmedia is now " + status + "!");
	}

	public void removeUser(LPUser user) {

		config.getDatabase().saveUser(user);
		users.remove(user.getName());
	}

	public void debug(String txt) {
		if (debug == 1) {
			System.out.println(txt);
		}
	}

	public void insertUser(final LPUser user) {
		user.setTimeComparison(new Date().getTime());
		user.setOnline(true);
		users.put(user.getName(), user);

	}

	public LPUser getUser(String username) {

		LPUser user = null;
		if (areUser(username)) {
			user = users.get(username);
		} else {
			user = config.getDatabase().GetUser(username);
		}
		return user;

	}

	public boolean areUser(String username) {
		return users.containsKey(username);
	}

	public void giveOnlineUsersPoints() {

		Iterator<String> u = users.keySet().iterator();
		while (u.hasNext()) {
			users.get(u.next()).givePoint();

		}
	}

	public void saveOnlineUsers() {
		LPUser[] savingUsers = new LPUser[users.size()];
		Iterator<String> u = users.keySet().iterator();
		for (int i = 0; u.hasNext(); i++) {
			savingUsers[i] = users.get(u.next());
		}
		config.getDatabase().saveUsers(savingUsers);
	}

	public Map<String, LPUser> getUsers() {
		return users;
	}

	public void loadUser(String name) {
		users.put(name, config.getDatabase().GetUser(name));

	}

	public LPTexts getLptext() {
		return lptext;
	}

	private void Metrics() {

		try {
		
			Metric metrics = new Metric(this);
			
			metrics.addCustomData(new Metric.Plotter("PointType") {

			        @Override
			        public int getValue() {
			            return config.getType();
			        }

			    });

			metrics.start();
			
		} catch (IOException e) {
			// Failed to submit the stats :-(
		}

	}

	
	public LPConfig getlpConfig() {
		return config;
	}

	public LPShop getShop() {
		return shop;
	}
}