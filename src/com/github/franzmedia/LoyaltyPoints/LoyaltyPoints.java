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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;
import com.github.franzmedia.LoyaltyPoints.Database.DatabaseHandler;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class LoyaltyPoints extends JavaPlugin {
	private DatabaseHandler database;
	private final Logger logger = Logger.getLogger("Minecraft");
	private int increment = 1, cycleNumber = 600,
			updateTimer = cycleNumber / 4, startingPoints = 0,
			SaveTimer = 3600, check = -10, version, newestVersion;
	

	private int debug = 0;
	private boolean afkTrackingSystem = true;
	private int pointType = 2;
	private String newVersion, checkString = "";
	private final Map<String, LPUser> users = new HashMap<String, LPUser>();

	/* Mysql */
	private FileConfiguration config;
	private File mapFile;
//	private FileConfiguration mapFileConfig;
//	private LPFileManager LPFM;
	private LPTexts lptext;

	/* Messages EDITABLE */

	// public List<String> milestones = new ArrayList<String>();
	// public Map<String, List<Integer>> rewardsTracker = new HashMap<String,
	// List<Integer>>(); // For tracking rewards, etc.

	// public static Economy economy = null;
	// public boolean economyPresent = true;3

	@Override
	public void onEnable() {
		// Enable metrics (MCstats.org)
		try {
			Metric metrics = new Metric(this);
			metrics.start();
		} catch (IOException e) {
			// Failed to submit the stats :-(
		}

		// Getting the texts
		lptext = new LPTexts(this);

		// loading the variables from the config!
		loadVariables();

		// loading the texts from the config!
		lptext.loadText();

		// loading the points !!!
		getServer().getScheduler().scheduleSyncDelayedTask(this,
				new Runnable() {

					@Override
					public void run() {
						loadOnlineUsers();

					}
				});

		// Setting up the listener (player login/logout & move)
		this.getServer().getPluginManager()
				.registerEvents(new LCListener(this), this);

		// checking for a new version!
		getServer().getScheduler().scheduleSyncDelayedTask(this,
				new Runnable() {

					@Override
					public void run() {
						startUpdateCheck();
					}
				});

		// Commands setup!
		getCommand("lp").setExecutor(new LPCommand(this, lptext));

		// Telling the server we are up and running
		info(getDescription(), "enabled");

		// Making scheduler (giving points after the update timer)
		getServer().getScheduler().scheduleAsyncRepeatingTask(this,
				new LPScheduler(this), updateTimer, updateTimer);

		// Every player online on enable is kickstarted!
		
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
			if(player.hasPermission("loyaltypoints.general")){
				users.put(player.getName(), database.GetUser(player.getName()));
			}
		}
	}
	
	public void loadVariables() {
		checkConfig();
		config = this.getConfig();
		check = checkVariable("increment-per-cycle");
		if (check > 0) {
			increment = check;
		}

		check = checkVariable("cycle-time-in-seconds");
		if (check > 0) {
			cycleNumber = check;
		}

		check = checkVariable("update-timer");
		if (check > 0) {
			updateTimer = check * 20;
		}

		check = checkVariable("starting-points");
		if (check > 0) {
			startingPoints = check;
		}

		check = checkVariable("SaveTimer");
		if (check > 0) {
			SaveTimer = check;
		}

		if (!config.contains("afk-tracking-system")) {
			config.set("afk-tracking-system", 1);
			try {
				config.save(new File(this.getDataFolder(), "config.yml"));
			} catch (final IOException e) {
				debug(lptext.getConsoleConfigSaveError());
			}
		} else {
			final int gettedValue = config.getInt("afk-tracking-system");
			switch (gettedValue) {
			case 1:
				afkTrackingSystem = true;
				break;
			case 0:
				afkTrackingSystem = false;
				break;

			default:
				afkTrackingSystem = true;
				break;
			}

		}

		check = checkVariable("point-type");
		if (check > 0) {
			pointType = check;
		}
		int type = 0;
		switch (pointType) {
		case 1:
			logger.warning("you can't use File based any more, we are now loading SQlite instead");
			type = 2;
			break;
		case 2:
			type = 2;
			break;
		case 3:
			type = 1;
			break;
		}
		if (type == 1) {
			 String mysql_host = null, mysql_port = "3306", mysql_user = null,
					mysql_pass = null, mysql_database = null;
			String miss = "";
			checkString = checkStringVariable("mysql-host");
			if (!checkString.isEmpty()) {
				mysql_host = checkString;
			} else {
				miss = "host ";
			}

			checkString = checkStringVariable("mysql-port");
			if (!checkString.isEmpty()) {
				mysql_port = checkString;
			}

			checkString = checkStringVariable("mysql-user");
			if (!checkString.isEmpty()) {
				mysql_user = checkString;
			} else {
				miss = miss + "user ";
			}
			checkString = checkStringVariable("mysql-pass");
			if (!checkString.isEmpty()) {
				mysql_pass = checkString;
			} else {
				miss = miss + "pass ";
			}

			checkString = checkStringVariable("mysql-database");
			if (!checkString.isEmpty()) {
				mysql_database = checkString;

			}
			if (miss.length() < 3) {
				database = new DatabaseHandler(this, logger, type, mysql_host,
						mysql_port, mysql_database, mysql_user, mysql_pass);
			} else {
				logger.warning(lptext.getConsoleMysqlError().replace(
						"%MYSQLERROR%", miss));
				setEnabled(false);
			}

		}else{
			database = new DatabaseHandler(this, logger, type, null,
					null, null, null, null);
		}
	}
	
	

	public DatabaseHandler getDBHandler() {
		return database;
	}

	
	public String checkStringVariable(final String name) {
		String str = "";
		if (config.contains(name)) {
			str = config.getString(name);
			debug(str + "" + name);
		} else {
			logger.info(lptext.getConsoleConfigError().replace("%ERROR%", name));
		}
		return str;
	}

	public int checkVariable(final String name) {
		int str = -10;

		if (config.contains(name)) {
			str = config.getInt(name);
			debug(str + "" + name);
		} else {
			logger.info(lptext.getConsoleConfigError().replace("%ERROR%", name));
		}
		return str;
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
		final String name = "config.yml";
		final File actual = new File(getDataFolder(), name);
		if (!actual.exists()) {

			getDataFolder().mkdir();
			final InputStream input = this.getClass().getResourceAsStream(
					"/defaults/config.yml");
			if (input != null) {
				FileOutputStream output = null;

				try {
					output = new FileOutputStream(actual);
					final byte[] buf = new byte[4096]; // [8192]?
					int length = 0;
					while ((length = input.read(buf)) > 0) {
						output.write(buf, 0, length);
					}
					this.logger
							.info("[LoyaltyPoints] Default configuration file written: "
									+ name); // TODO: this
				} catch (final IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (input != null)
							input.close();
					} catch (final IOException e) {
					}

					try {
						if (output != null)
							output.close();
					} catch (final IOException e) {
					}
				}
			}
		}
	}
	
	public void removeUser(LPUser user){
		
		database.saveUser(user);
		users.remove(user.getName());
	}

	public int getCycleNumber() {
		return cycleNumber;
	}

	public int getIncrement() {
		return increment;
	}

	public int getUpdateTimer() {
		return updateTimer;
	}

	public void debug(String txt) {
		if (debug == 1) {
			System.out.println(txt);
		}
	}

	public void startUpdateCheck() {
		version = VersionFormat(getDescription().getVersion());
		newestVersion = version;
		getNewestVersion();

		if (!upToDate()) {
			logger.info("------------");
			logger.warning(lptext.getPluginNotUpToDate());
			logger.info("------------");
		} else {
			logger.info(lptext.getPluginUpToDate());
		}
	}

	

	public void getNewestVersion() {
		try {
			final URL url = new URL(
					"https://raw.github.com/franzmedia/LoyaltyPoints/master/version.txt");
			final URLConnection connection = url.openConnection();
			final BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			newVersion = in.readLine();
			debug(newVersion);

			newestVersion = VersionFormat(newVersion);

			in.close();
		} catch (final MalformedURLException e) {
			logger.warning(lptext.getErrorLoadingNewVersion());
		} catch (final IOException e) {
			logger.warning(lptext.getErrorLoadingNewVersion());
		}
	}

	private int VersionFormat(final String Version) {
		String NV = Version.replaceAll("\\.", "");
		debug("AFTER NV" + NV);
		final char arr[] = NV.toCharArray();
		NV = "";
		for (int i = 0; i < arr.length; i++) {
			NV = NV + arr[i];

		}
		final int miss = 4 - arr.length;

		for (int i = 0; i < miss; i++) {
			NV = NV + 0;

		}
		debug("newest:" + NV);
		return Integer.parseInt(NV);
	}

	public int getSaveTimer() {
		return SaveTimer;
	}

	public void insertUser(final LPUser user) {
		users.put(user.getName(), user);

	}

	public LPUser getUser(String username) {
		LPUser user = null;
		if (areUser(username)) {
			user = users.get(username);
		} else {
			user = database.GetUser(username);
		}
		return user;

	}


	public boolean areUser(String username) {
		return users.containsKey(username);
	}

	public boolean upToDate() {
		boolean returnstr = false;
		debug("uptoDATE" + newestVersion + ">" + version);
		if (newestVersion > version) {
			returnstr = false;
		} else {
			returnstr = true;
		}

		return returnstr;
	}
	
	public void giveOnlineUsersPoints(){
		
		Iterator<String> u = users.keySet().iterator();
		while(u.hasNext()){
			users.get(u.next()).givePoint();
			
		}
	}
	public void saveOnlineUsers() {
		LPUser[] savingUsers = new LPUser[users.size()];
		Iterator<String> u = users.keySet().iterator();
		for(int i = 0;u.hasNext();i++){
			savingUsers[i] = users.get(u.next());
		}
		database.saveUsers(savingUsers);
	}

	public File getMapFile() {

		return mapFile;
	}

	public boolean AfkTrackingSystem() {
		return afkTrackingSystem;
	}


	public Map<String, LPUser> getUsers() {
		return users;
	}

	public String getNewVersion() {
		return newVersion;
	}
	public int getStartingPoints() {
		return startingPoints;
	}


	public void loadUser(String name) {
		users.put(name, database.GetUser(name));
		
	}
}