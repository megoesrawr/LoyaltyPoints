package com.github.franzmedia.LoyaltyPoints;

import com.github.franzmedia.LoyaltyPoints.Database.DatabaseHandler;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;
/**
 * This is the method for holding information about the configration and all in this.
 * @author Franz
 */
public class LPConfig {

    private int increment = 1;
    private int cycleNumber = 600;
    private int updateTimer = cycleNumber / 4;
    private int startingPoints = 0;
    private int SaveTimer = 3600;
    private int check = -10;
    private boolean shopActive = true;
    private int type;
    private String newVersion;
    private int version, newestVersion;
    private DatabaseHandler database;
    private FileConfiguration config;
    private Logger logger;
    private LoyaltyPoints plugin;
/**
 * The normal constructor for LPConfig.
 * @param fileConfiguration The place of the config file.
 * @param loyaltyPoints a reference to the plugin main folder
 * @param logger the logger, so we can log things if anything goes wrong.
 */
    public LPConfig(FileConfiguration fileConfiguration, LoyaltyPoints loyaltyPoints, Logger logger) {
        config = fileConfiguration;
        plugin = loyaltyPoints;
        this.logger = logger;
        load();
    }
/**
 * Loads the variable from the config file.
 */
    public void load() {
        checkConfig();
        check = checkVariable("shopActive");
        if (check >= 0) {
            if (check == 0) {
                shopActive = false;
            } else {
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
        if (check != 0) {
              version = VersionFormat(plugin.getDescription().getVersion());
        newestVersion = version;

        if (!upToDate()) {
            logger.info("------------");
            logger.warning(plugin.getLptext().getPluginNotUpToDate());
            logger.info("------------");
        } else {
            logger.info(plugin.getLptext().getPluginUpToDate());
        }
        }

    }
/**
 * This method is for getting  string value from the config file.
 * @param name the key you want to get from the config file
 * @return The string variable from the config file.
 */
    public String checkStringVariable(String name) {
        String str = "";
        if (config.contains(name)) {
            str = config.getString(name);
            plugin.debug(str + "" + name);
        } else {
            logger.info(plugin.getLptext().getConsoleConfigError().replace("%ERROR%", name));
        }
        return str;
    }
/**
 * This method is for getting int value from the config file.
 * @param name the key you want to get from the config file
 * @return The int variable from the config file.
 */
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
/**
 * Creates the databas connection for later use :)
 * @param type what type of database you want 1 == mySQL 2 == SQLite
 */
    public void createDatabase(int type) {

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

        } else {
            database = new DatabaseHandler(plugin, logger, type, null,
                    null, null, null, null);
        }
    }
    
    
    
/**
 * method to check if the version is up to date (going to github for this checks.
 * @return returns true/false if it's up to date.
 */
    public boolean upToDate() {
        loadNewestVersion();
        boolean returnstr;
        plugin.debug("uptoDATE" + newestVersion + ">" + version);
        if (newestVersion > version) {
            returnstr = false;
        } else {
            returnstr = true;
        }

        return returnstr;
    }
/**
 * Checks the config file if it excists then use that else create it.
 */
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
                    int length;
                    while ((length = input.read(buf)) > 0) {
                        output.write(buf, 0, length);
                    }
                    logger.info("[LoyaltyPoints] Loading the Default config: " + name);
                } catch (IOException e) {
                   logger.warning("Error while loading the file!!!!!!"+e.getMessage());
                } finally {
                    try {
                        if (input != null) {
                            input.close();
                        }
                    } catch (IOException e) {
                    }

                    try {
                        if (output != null) {
                            output.close();
                        }
                    } catch (IOException e) {
                    }
                }
            }
        }
    }
/**
 * Goes onto github to look at version.txt for the latest version number.
 * @return the newest version number.
 */
    private int loadNewestVersion() {
        try {
            final URL url = new URL("https://raw.github.com/franzmedia/LoyaltyPoints/master/version.txt");
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
    
    /**
     * This is for konverting version number 1.1.2.2 to 1122 or 
     * @param Version the String you want to get converted.
     * @return the version into something lookable / comparable
     */
    private int VersionFormat(String Version) {
        String NV = Version.replaceAll("\\.", "");
        final char arr[] = NV.toCharArray();
        NV = "";
        for (int i = 0; i < arr.length; i++) {
            NV = NV + arr[i];

        }
        final int miss = 4 - arr.length;

        for (int i = 0; i < miss; i++) {
            NV = NV + 0;

        }
        return Integer.parseInt(NV);
    }
    ///GETTERS AND SETTERS UNDER THIS

    /**
     * Function to get the databasehandler (which is allready made)
     * @return the databaseHandler.
     */
    public DatabaseHandler getDatabase() {
        return database;
    }

    /**
     * Newest version of the plugin.
     * @return the newVersion which is the newest version of the plugin.
     */
    public String getNewVersion() {
        return newVersion;
    }


    /**
     * How much the plugin should increase every time the player have made a cycle.
     * @return the increment.
     */
    public int getIncrement() {
        return increment;
    }

    /**
     * What kind of type the plugin is using for saving.
     * @return the type of saving method is using
     */
    public int getType() {
        return type;
    }

    /**
     * how long there gonna be between 2 update cycles.
     * @return the updateTimer
     */
    public int getUpdateTimer() {
        return updateTimer;
    }

    /**
     * This is for getting how much points the user is starting with.
     * @return the starting point of a neew user.
     */
    public int getStartingPoints() {
        return startingPoints;
    }

    /**
     * How often the plugin should save information about the users online atm.
     * @return the save time of the plugin.
     */
    public int getSaveTimer() {
        return SaveTimer;
    }

    /**
     * How often the cycle is running (time between)
     * @return the time between 2 runs.
     */
    public int getCycleNumber() {
        return cycleNumber;
    }

    /**
     *Method to check if the server is allowing shops 
     * @return true/false
     */
    public boolean shopActive() {
        return shopActive;
    }
}
