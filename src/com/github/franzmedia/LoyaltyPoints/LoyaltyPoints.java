package com.github.franzmedia.LoyaltyPoints;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/** @author Kasper Franz - HD Solutions */
public class LoyaltyPoints extends JavaPlugin {
	public final Logger logger = Logger.getLogger("Minecraft");

	private int increment = 1, cycleNumber = 600, updateTimer = cycleNumber/4 ,startingPoints = 0;
	private int SaveTimer = 3600; // 1 hour
	private int debug = 0;
	
	private Map<String, Integer> loyaltyPoints = new HashMap<String, Integer>(); //has the points 
	private Map<String, Integer> loyaltTotalTime = new HashMap<String, Integer>(); // has the TOTAL TIME
	private Map<String, Integer> loyaltTime = new HashMap<String, Integer>(); // has the TIME SINCE LAST POINT
	private Map<String, Long> timeComparison = new HashMap<String, Long>(); // the timer to comapare with start / last login
	
	public FileConfiguration config;
	public File mapFile;
	public FileConfiguration mapFileConfig;
	public LPFileManager lcFM = new LPFileManager(this);
	
	/* Messages  EDITABLE					 */ 
	public String pluginTag = colorize("&6[LoyaltyPoints]");
	public String consoleCheck = pluginTag+ " Sorry, I don't track consoles.";
	public String selfcheckMessage =  colorize(pluginTag + " &3You have &b%POINTS% &3Loyalty Points.");
	public String checkotherMessage = colorize(pluginTag + " &3%PLAYERNAME% has &b%POINTS% &3Loyalty Points.");
	
	// public List<String> milestones = new ArrayList<String>();
	// public Map<String, List<Integer>> rewardsTracker = new HashMap<String,
	// List<Integer>>(); // For tracking rewards, etc.
	

	// public static Economy economy = null;
	// public boolean economyPresent = true;

	/*
	 * Planned Features Possibility to pay a defined amount of money when a
	 * player gains a specified amount of LoyaltyPoints Only pay points if the
	 * player is not AFK Server-wide announcements when a player gains a certain
	 * amount of points (reaches a point milestone) Receive item rewards on
	 * specified point milestones
	 */

	public void onDisable() {
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new CountScheduler(this),(long) 0);
		LPFileManager.save();
		getLoyaltyPoints().clear();
		getLoyaltTotalTime().clear();
		// milestones.clear();
		info(this.getDescription(), "disabled");
	}

	public void onEnable() {
		
		mapFile = new File(this.getDataFolder(), "points.yml");
		mapFileConfig = YamlConfiguration.loadConfiguration(mapFile);
		loadPointsData();
		checkConfig();
		loadVariables();
		getCommand("lp").setExecutor(new LPCommand(this));
		this.getServer().getPluginManager().registerEvents(new LCListener(this), this);

		/*
		 * if (!setupEconomy()) {
		 * this.logger.severe("[LoyaltyPoints] Vault dependency not found!");
		 * this
		 * .logger.severe("[LoyaltyPoints] Milestones paying feature disabled."
		 * ); economyPresent = false; }
		 */
		this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new CountScheduler(this),(long) 60L, updateTimer);
		info(this.getDescription(), "enabled");
	}

	public void loadPointsData() {
		for (String s : this.mapFileConfig.getKeys(false)) {
			kickStart(s);
			
		}
	}

	public void loadVariables() {
		config = this.getConfig();
		increment = config.getInt("increment-per-cycle");
		cycleNumber = config.getInt("cycle-time-in-seconds");
		 updateTimer = config.getInt("update-timer") * 20;
		startingPoints = config.getInt("starting-points");
		pluginTag = colorize(config.getString("plugin-tag"));
		selfcheckMessage = colorize(config.getString("self-check-message").replaceAll("%TAG%", pluginTag));
		checkotherMessage = colorize(config.getString("check-otherplayer-message").replaceAll("%TAG%", pluginTag));
		
		if(!config.contains("SaveTimer")){
			config.set("SaveTimer", "3600");
			try {
				config.save(new File(this.getDataFolder(), "config.yml"));
			} catch (IOException e) {
			System.out.println("ERROR while loading new variable");
			}
		}else{
			SaveTimer = config.getInt("SaveTimer");
		}

		// ConfigurationSection milestonesCS =
		// config.getConfigurationSection("points-milestones.Amounts");
		// List<String> l = new ArrayList<String>(milestonesCS.getKeys(false));
		// milestones.addAll(l);
	}

	public void kickStart(String player) { //get's the users elements and if new creates him
		
		if (!getLoyaltyPoints().containsKey(player) && !LPFileManager.load(player)) { //if player don't excists 
			// we put startting points, TotalTime, and time since last point
				getLoyaltyPoints().put(player, startingPoints);
				getLoyaltTotalTime().put(player, 0);
				getLoyaltTime().put(player, 0);
		}

		if(!getTimeComparison().containsKey(player)){
			getTimeComparison().put(player, new Date().getTime());
		}
		
		Long time = new Date().getTime();
		debug(time+"");
		debug(getLoyaltTime().get(player)+"");
		getTimeComparison().put(player, (time-(getLoyaltTime().get(player)*1000))); 
		debug(getTimeComparison().get(player)+"");
	}

	public String colorize(String message) {
		return message.replaceAll("&([a-f0-9])", ChatColor.COLOR_CHAR + "$1");
	}
	public int getTimeLeft(String player){
long now = new Date().getTime();

// cycle = 300 
// getLoyaltTime = 11
// now - timecomparision (time spent) 
		int str1= (int) (getCycleNumber()-(((now-timeComparison.get(player))/1000)+loyaltTime.get(player)));
debug(str1+"");
		return str1;
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
	public void info(PluginDescriptionFile pdf, String status) {
		this.logger.info("[LoyaltyPoints] version " + pdf.getVersion()
				+ " by Franzmedia is now " + status + "!");
	}

	/*
	 * private boolean setupEconomy() { if
	 * (getServer().getPluginManager().getPlugin("Vault") == null) { return
	 * false; } RegisteredServiceProvider<Economy> rsp =
	 * getServer().getServicesManager().getRegistration(Economy.class); if (rsp
	 * == null) { return false; } economy = rsp.getProvider(); return economy !=
	 * null; }
	 */

	/*
	 * public void checkReward(String player) { int pointsAmount =
	 * this.loyaltyMap.get(player); List<Integer> i = new ArrayList<Integer>();
	 * for (Iterator<String> it = this.milestones.iterator(); it.hasNext();) {
	 * int msAmount = Integer.parseInt(it.next()); if (pointsAmount > msAmount)
	 * { i.add(msAmount); } } if (i.isEmpty()) { return; } int isMax; for (int f
	 * = 0; f < i.size(); f++) { EconomyResponse paid =
	 * LoyaltyPoints.economy.depositPlayer(player, i.get(f)); // You have been
	 * payed so and so amount... // Broadcast here.. } // int topSize =
	 * i.size()-1; /*while
	 * (this.getConfig().getBoolean("points-milestones.Amounts." +
	 * String.valueOf(i.get(topSize)) + ".broadcast") == false) {
	 * 
	 * // } }
	 */

	private void checkConfig() {
debug("hmm checkconfig");
		String name = "config.yml";
		File actual = new File(getDataFolder(), name);
		if (!actual.exists()) {
			
			getDataFolder().mkdir();
			InputStream input = this.getClass().getResourceAsStream(
					"/defaults/config.yml");
			if (input != null) {
				FileOutputStream output = null;

				try {
					output = new FileOutputStream(actual);
					byte[] buf = new byte[4096]; // [8192]?
					int length = 0;
					while ((length = input.read(buf)) > 0) {
						output.write(buf, 0, length);
					}
					this.logger
							.info("[LoyaltyPoints] Default configuration file written: "
									+ name);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (input != null)
							input.close();
					} catch (IOException e) {
					}

					try {
						if (output != null)
							output.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}

	public int getPlayTime(String name) {
		return getLoyaltTotalTime().get(name);
		
	}

	public Map<String, Long> getTimeComparison() {
		return timeComparison;
	}

	public Map<String, Integer> getLoyaltTime() {
		return loyaltTime;
	}


	public Map<String, Integer> getLoyaltyPoints() {
		return loyaltyPoints;
	}



	public int getCycleNumber() {
		return cycleNumber;
	}

	

	public int getIncrement() {
		return increment;
	}

	public Map<String, Integer> getLoyaltTotalTime() {
		return loyaltTotalTime;
	}

	
	public int getUpdateTimer() {
		return updateTimer;
	}
	
public void debug(String txt){
		
		
		if(debug == 1){
			System.out.println(txt);
		}
		
		
	}

public int getSaveTimer() {
	return SaveTimer;
}

	

}
