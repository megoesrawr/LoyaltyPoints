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
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;

import com.github.franzmedia.LoyaltyPoints.Database.DatabaseHandler;

public class LPConfig {
	private int increment = 1;
	private int cycleNumber = 600;
			private int updateTimer = cycleNumber / 4;
			private int startingPoints = 0;
			private int SaveTimer = 3600; 
			private int check = -10;
			private boolean shopActive = true;
			private int type;
			// VERSION config!!
			private String newVersion;
					private int version, newestVersion;
					private DatabaseHandler database;
					private FileConfiguration config;
					private Logger logger;
					private LoyaltyPoints plugin;
					
	
	public LPConfig(FileConfiguration fileConfiguration, LoyaltyPoints loyaltyPoints, Logger logger){
		config = fileConfiguration;
		plugin = loyaltyPoints;
		this.logger = logger;
		load();
	}

	public void load() {
		checkConfig();
		check = checkVariable("shopActive");
		if(check >= 0){
			if(check == 0){
				shopActive = false;
			}else{
				shopActive = true;
			}
		}
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

		check = checkVariable("point-type");
		if (check <= 0) {
			check = 2;
		}
		type = 0;
		switch (check) {
		case 1:
			logger.warning("you can't use File based any more, we are now loading SQLite instead, you can use \"lp tosql\" to get it transformed to SQLite");
			type = 2;
			break;
		case 2:
			type = 2;
			break;
		case 3:
			type = 1;
			break;
		}
		
		createDatabase(type);
		check = checkVariable("checking-update");
		if(check != 0){
			startUpdateCheck();
		}
		
	}
	
	public String checkStringVariable(final String name) {
		String str = "";
		if (config.contains(name)) {
			str = config.getString(name);
			plugin.debug(str + "" + name);
		} else {
			logger.info(plugin.getLptext().getConsoleConfigError().replace("%ERROR%", name));
		}
		return str;
	}

	public int checkVariable(final String name) {
		int str = -10;

		if (config.contains(name)) {
			str = config.getInt(name);
			plugin.debug(str + "" + name);
		} else {
			logger.info(plugin.getLptext().getConsoleConfigError().replace("%ERROR%", name));
		}
		return str;
	}
	
	public void createDatabase(int type){
		
		if (type == 1) {
			 String mysql_host = null, mysql_port = "3306", mysql_user = null,
					mysql_pass = null, mysql_database = null;
			String miss = "";
			String checkString = checkStringVariable("mysql-host");
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
				database = new DatabaseHandler(plugin, logger, type, mysql_host,
						mysql_port, mysql_database, mysql_user, mysql_pass);
			} else {
				logger.warning(plugin.getLptext().getConsoleMysqlError().replace(
						"%MYSQLERROR%", miss));
				plugin.stop();
			}

		}else{
			database = new DatabaseHandler(plugin, logger, type, null,
					null, null, null, null);
		}
	}
	
	public void startUpdateCheck() {
		version = VersionFormat(plugin.getDescription().getVersion());
		newestVersion = version;
		getNewestVersion();

		if (!upToDate()) {
			logger.info("------------");
			logger.warning(plugin.getLptext().getPluginNotUpToDate());
			logger.info("------------");
		} else {
			logger.info(plugin.getLptext().getPluginUpToDate());
		}
	}
	public boolean upToDate() {
		boolean returnstr = false;
		plugin.debug("uptoDATE" + newestVersion + ">" + version);
		if (newestVersion > version) {
			returnstr = false;
		} else {
			returnstr = true;
		}

		return returnstr;
	}
	private void checkConfig() {
		String name = "config.yml";
		File actual = new File(plugin.getDataFolder(), name);
		if (!actual.exists()) {
			plugin.getDataFolder().mkdir();
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
							.info("[LoyaltyPoints] Loading the Default config: "
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
	
	
	public int getNewestVersion() {
		try {
			final URL url = new URL(
					"https://raw.github.com/franzmedia/LoyaltyPoints/master/version.txt");
			final URLConnection connection = url.openConnection();
			final BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			newVersion = in.readLine();
			plugin.debug(newVersion);

			newestVersion = VersionFormat(newVersion);

			in.close();
		} catch (final MalformedURLException e) {
			logger.warning(plugin.getLptext().getErrorLoadingNewVersion());
		} catch (final IOException e) {
			logger.warning(plugin.getLptext().getErrorLoadingNewVersion());
		}
		return newestVersion;
	}

	private int VersionFormat(final String Version) {
		String NV = Version.replaceAll("\\.", "");
		plugin.debug("AFTER NV" + NV);
		final char arr[] = NV.toCharArray();
		NV = "";
		for (int i = 0; i < arr.length; i++) {
			NV = NV + arr[i];

		}
		final int miss = 4 - arr.length;

		for (int i = 0; i < miss; i++) {
			NV = NV + 0;

		}
		plugin.debug("newest:" + NV);
		return Integer.parseInt(NV);
	}
	///GETTERS AND SETTERS UNDER THIS

	public DatabaseHandler getDatabase(){
		return database;
	}

	public int getIncrement() {
		return increment;
	}

	public int getType() {
		return type;
	}

	public int getUpdateTimer() {
		return updateTimer;
	}

	public int getStartingPoints() {
		return startingPoints;
	}

	public int getSaveTimer() {
		return SaveTimer;
	}

	public int getCycleNumber() {
		return cycleNumber;
	}

	public boolean shopActive() {
		return shopActive;
	}
}
