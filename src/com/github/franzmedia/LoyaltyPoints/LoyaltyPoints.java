package com.github.franzmedia.LoyaltyPoints;
import lib.PatPeter.SQLibrary.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	public SQLite sqlite;
	private int increment = 1, cycleNumber = 600, updateTimer = cycleNumber/4 ,startingPoints = 0;
	private int SaveTimer = 3600; // 1 hour
	private int debug = 1;
	private int check = -10;
	private int version, newestVersion;
	public String newVersion;
	private String checkString = "";
	private Map<String, LPUser> users = new HashMap<String, LPUser>();
	

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
		save();
		sqlite.close();
		users.clear();
		// milestones.clear();
		info(this.getDescription(), "disabled");
	}

	public void onEnable() {
		mapFile = new File(this.getDataFolder(), "points.yml");
		mapFileConfig = YamlConfiguration.loadConfiguration(mapFile);
		sqlite = new SQLite(this.getLogger(), pluginTag, "LP", this.getDataFolder().toString());
		sqlite.open();
		
		loadPointsData();
		checkConfig();
		startUpdateCheck();
		loadVariables();
		getCommand("lp").setExecutor(new LPCommand(this));
		this.getServer().getPluginManager().registerEvents(new LCListener(this), this);
		 
		 
		 
		 /*
		 * if (!setupEconomy()) {
		 * this.logger.severe("[LoyaltyPoints] Vault dependency not found!");
		 * th
		 * .logger.severe("[LoyaltyPoints] Milestones paying feature disabled."
		 * ); economyPresent = false; }
		 */
		this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new CountScheduler(this),(long) 60L, updateTimer);
		info(this.getDescription(), "enabled");
	}

	public void loadPointsData() {
		
		if(!sqlite.checkTable("users")){
			sqlite.createTable("CREATE TABLE users (username varchar(15) not null, point int(15), time int(25), totaltime int(25))");
			sqlite.query("INSERT INTO users values('kasperFranz', '22', '22', '0')");
			System.out.println(sqlite.lastUpdate);
			sqlite.query("INSERT INTO users values('Franz488', '022', '50', '022')");
			System.out.println(sqlite.lastUpdate);
			sqlite.query("INSERT INTO users values('famfranz5', '1', '2', '220')");
			System.out.println(sqlite.lastUpdate);
		}else{
			
		Long now = new Date().getTime();	
	
		ResultSet rs = sqlite.query("SELECT count(username) as c FROM users");
		try {
			rs.next();
			int usersCount = rs.getInt("c");
			System.out.println(usersCount);
			 rs = sqlite.query("SELECT * FROM users");
			 for(int i = 0; i < usersCount; i++){
				 rs.next();
					System.out.println("user insert" + rs.getString("username"));
					users.put(rs.getString("username"), new LPUser(rs.getString("username"), rs.getInt("point"), rs.getInt("time"), rs.getInt("totaltime"), now));
				}
			
		} catch (SQLException e1) {
System.out.println("error with loading users");
		}

		
						
			
		}
	}
	
	public String checkStringVariable(String name){
		String str = "";
		if(config.contains(name)){
			str = config.getString(name);
			debug(str + ""+name);
		}else{
			System.out.println(pluginTag + " You have a error with you config file around: " + name + " we use default option.");
		}
		return str;
	}
	

	public int checkVariable(String name){
		int str = -10;
		
		if(config.contains(name)){
			str = config.getInt(name);
			debug(str + ""+name);
		}else{
			System.out.println(pluginTag + " You have a error with you config file around: " + name + " we use default option.");
		}
		return str;
	}
	
	
	
	
	public void loadVariables() {
		
		
		 
			config = this.getConfig();
			 check = checkVariable("increment-per-cycle");
			 if(check > 0){
				 increment = check;
			 }
			
			 check = checkVariable("cycle-time-in-seconds");
			 if(check > 0){
				 debug(cycleNumber+"");
				 cycleNumber = check;
				 debug(cycleNumber+"");
			 } 
		
		 check = checkVariable("update-timer");
				 
		 if(check > 0){
			 updateTimer = check*20;
		 }
		 check = checkVariable("starting-points");
		 
		 if(check > 0){
			 startingPoints = check;
		 }
		 
		checkString = checkStringVariable("plugin-tag");
		 if(!checkString.isEmpty()){
			 pluginTag = colorize(checkString);
		 }
		 checkString = checkStringVariable("self-check-message");
		
		 if(!checkString.isEmpty()){
			 selfcheckMessage = colorize(checkString.replaceAll("%TAG%", pluginTag));
		 }
		
		 checkString = checkStringVariable("check-otherplayer-message");
			
		 if(!checkString.isEmpty()){
			 selfcheckMessage = colorize(checkString.replaceAll("%TAG%", pluginTag));
		 }
		
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
		
		if(users.containsKey(player)){
			
			users.get(player).setTimeComparison(new Date().getTime());
		}else{
		
		if (!users.containsKey(player) && !LPFileManager.load(player)) { //if player don't excists 
			// we put starting points, TotalTime, and time since last point
			
			users.put(player, new LPUser(player, startingPoints, 0, 0, new Date().getTime()));
			debug("NEW USER INSERTED"+player);
		}
		
		}
		
		debug(users.get(player).getTime()+"");
		
		
		
	}

	public String colorize(String message) {
		return message.replaceAll("&([a-f0-9])", ChatColor.COLOR_CHAR + "$1");
		
		
	}
	
	
	public int getTimeLeft(String player){
long now = new Date().getTime();

// cycle = 300 
// getLoyaltTime = 11
// now - timecomparision (time spent) 
		int str1= (int) (getCycleNumber()-(((now-users.get(player).getTimeComparison())/1000)+users.get(player).getTime()));
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

	public int getCycleNumber() {
		return cycleNumber;
	}

	

	public int getIncrement() {
		return increment;
	}

	
	
	public int getUpdateTimer() {
		return updateTimer;
	}
	
	public void debug(String txt){
			
		if(debug == 1){
			System.out.println(txt);
		}
			
	}
	
	
	public void startUpdateCheck() {
		version = Integer.parseInt(getDescription().getVersion().replaceAll("\\.", ""));
		newestVersion = version;
			getNewestVersion();	
			
			if(!upToDate()){
				this.logger.info(colorize(pluginTag) +" is not up to date, the new version:" + newVersion);
			}else{
				this.logger.info(colorize(pluginTag) +" the plugin is up to date...");
			}
	}
	
	
	
	public void getNewestVersion(){
		
		try {
			// open HTTP connection
			URL url = new URL("https://raw.github.com/franzmedia/LoyaltyPoints/master/version.txt");
			URLConnection connection = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			newVersion = in.readLine();
			newestVersion = Integer.parseInt(newVersion.replaceAll("\\.", ""));
			
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	

public int getSaveTimer() {
	return SaveTimer;
}


public void insertUser(LPUser user){
	users.put(user.getName(), user);
	
}

public Map<String, LPUser> getUsers() {
	return users;
}

public boolean upToDate() {
boolean returnstr = false;
	if(newestVersion > version){
		returnstr = false;
	}else{
		returnstr = true;
	}
	
	return returnstr;
}

public void save() {

for(LPUser user : users.values()){
	
		
		System.out.println(user.getName());
		
		try {
			String sql1 = "SELECT * FROM 'users' WHERE 'username'=\""+user.getName()+"\"";
			debug(sql1);
			ResultSet rs = sqlite.query(sql1);
			debug("before rs.next");
			if(rs.next()){
				debug("doing something in a row");
				String sql = "UPDATE 'users' SET 'point' = \""+user.getPoint() + "\", 'time' = \""+user.getTime() + "\", 'totaltime' = \""+ user.getTotalTime() + "\" WHERE 'username' = \""+user.getName()+"\"";
				debug("sql kode for "+ user.getName() + "  " +sql);
				rs = sqlite.query(sql);
				
			}else{
				rs = sqlite.query("INSERT INTO users VALUES ("+user.getName()+ ","+user.getPoint()+"," +user.getTime() +"," + user.getTotalTime()+")");
			}
			
			
		} catch (SQLException e) {
			// 
			e.printStackTrace();
			debug(e.getSQLState());
		}
		
		
	}
	
}
}