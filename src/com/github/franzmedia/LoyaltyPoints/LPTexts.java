package com.github.franzmedia.LoyaltyPoints;

import org.bukkit.ChatColor;

public class LPTexts {
	private String pluginTag = "[LoyaltyPoints]";
	 private String consoleCheck = "Sorry, I don't track consoles.";
	 private String selfcheckMessage = "&3You have &b%POINTS% &3Loyalty Points.";
	 private String checkotherMessage = " &3%PLAYERNAME% has &b%POINTS% &3Loyalty Points.";
	 private String toMySQL =  " We are now moving your file browsers to SQLite/MySQL (this may take a while, please wait!)";
	 private String MySQLFinish = "The transform to SQL is done, we moved %TOTAL% users";
	 private String noAccess = " You don't have access to this command.";
	 private String addToUser = "&3There have been added &b %POINT% &3points to &b %USER%";
	 private String noUser = "It looks like the user isn't found.";
	 private String reloadMsg = "&3Reloaded points data & configuration.";
	 private String nowVersion = "&3Version &b%VERSION%";
	 private String newVersionAvalible = "&3There are a newer version of LoyaltyPoints %NEWVERSION%";
	 private String setHelpError = " &3/lp set &b[username] [amount]";
	 private String newSetAmount = " &3%PLAYER%'s&b Loyalty Points was changed to &3%AMOUNT%";
	 private String errorNumber = "&cNumber expected after. &b";
	 private String errorPermission = "&cYou don't have permission for this command.";
	 private String errorUnknownUser = "&cThe user couldn't be found.";
	 private String errorNoPlayer = "&cNo players in record.";
	 private String topMsgtop = "&3--------- &6 LoyaltyPoints Top Players &3---------";
	 private String topUsermsg = " &6 %PLACE% &b %PLAYERNAME%  - &3 %POINTS% points";
	 private String helpCheckSelf = "&6/loyaltypoints [/lp]&b- Checks your Loyalty Points.";
	 private String helpCheckOther ="&6/lp [username]&b- Checks the specified player's Loyalty Points.";
	 private String helpNext = "&6/lp next &b- Shows time to next payout.";
	 private String helphelp = "&6/lp help &b- Shows you this menu for help.";
	 private String helpTop = "&6/lp top (amount)&b- Shows the top 10 players.";
	 private String helpVersion = "&6/lp version &b- Shows the version of Loyalty Points.";
	 private String helpReload = "&6/lp reload &b- Reloads the Loyalty Points.";
	 private String helpSet = "&6/lp set [username] [amount] &b- Sets the username to amount LP.";
	 private String helpPlayTime = "&6/lp playtime &b- Shows your playtime.";
	 private String helpAdd = "&6/lp add [username] (amont) &b- Adds amount to username.";
	 private String helpTopBot = "&3--------- &6 LoyaltyPoints Help &3 ---------";
	 private String next = "&3There are around &b %TIME% &3 until next payout.";
	 private String nextGone = "&3You get points in the next pay out. :)";
	 private String onlinetime = "&3You have been online for &b %ONLINETIME%.";
	 private String consolePointTypeError = "It seems like there are a error on your PointType,  1-3 is a allowed value!";
	 private String consoleLoadingUser = "";
	 private String consoleLoadingUserDone = "There have been loaded a total of %TOTAL% users.";
	 private String consoleSQLError = "There is a error with the SQL.";
	 private String consoleConfigError = "You have a error with you config file around: %ERROR% we use default option.";
	 private String consoleConfigSaveError = "There was a error while loading saving variable.";
	 private String consoleMysqlError = "There was a error with your MySQL settings: %MYSQLERROR%";
	 private String pluginNotUpToDate = "The plugin is not up to date, the new version: %NEWVERSION%";
	 private String pluginUpToDate = "The plugin is up to date.";
	 private String transformAmount = "The transform to SQL is done, we moved %TOTAL% users.";
	 private String errorLoadingNewVersion = "There was a error while loading the newest version.";
	 private String errorNoUsers = "There was no users in the record.";
	private LoyaltyPoints plugin;
		
	 public LPTexts(LoyaltyPoints plugin){
		 this.plugin = plugin;
	 }
	 
	 
		
		
		
		
	 
	 private String finalize(String s,int i){
		 if(i == 1){ 
			 //without COLORIZE
			s = pluginTag +" " +s;
		 }
		 else if(i == 2){ 
			 // with COLORIZE
			 
		 
			 s = colorize( "&6" + pluginTag+ " &3"+ s);
		 }
		 return s;
	 }
	 
	public String colorize(String message) {
		return message.replaceAll("&([a-f0-9])", ChatColor.COLOR_CHAR + "$1");
	}

	public String getPluginTag() {
		return finalize(pluginTag,1);
	}
	public String getConsoleCheck() {
		return finalize(consoleCheck,1);
	}
	public String getSelfcheckMessage() {
		return finalize(selfcheckMessage,2);
	}
	public String getCheckotherMessage() {
		
		return finalize(checkotherMessage,2);
	}
	public String getToMySQL() {
		
		return finalize(toMySQL,1);
	}
	public String getMySQLFinish() {
		
		return finalize(MySQLFinish,1);
	}
	public String getNoAccess() {
		
		return finalize(noAccess,2);
	}
	public String getAddToUser() {
		return finalize(addToUser,2);
	}
	public String getNoUser() {
		return finalize(noUser,2);
	}
	public String getReloadMsg() {
		
		return finalize(reloadMsg,2);
	}
	public String getNowVersion() {
		return finalize(nowVersion,2);
	}
	public String getNewVersionAvalible() {
		return finalize(newVersionAvalible,2);
	}
	public String getSetHelpError() {
		return finalize(setHelpError,2);
	}
	public String getNewSetAmount() {

		return finalize(newSetAmount,2);
	}
	public String getErrorNumber() {
		
		return finalize(errorNumber,2);
	}
	public String getErrorNoPlayer() {
		
		return finalize(errorNoPlayer,2);
	}
	public String getTopMsgtop() {
		return finalize(topMsgtop,2);
	}
	public String getTopUsermsg() {
		
		return finalize(topUsermsg,2);
	}
	public String getHelpCheckSelf() {
		
		return finalize(helpCheckSelf,2);
	}
	public String getHelpCheckOther() {
		
		return finalize(helpCheckOther,2);
	}
	public String getHelpNext() {
		
		return finalize(helpNext,2);
	}
	public String getHelphelp() {
		
		return finalize(helphelp,2);
	}
	public String getHelpTop() {
		
		return finalize(helpTop,2);
	}
	public String getHelpVersion() {
		
		return finalize(helpVersion,2);
	}
	public String getHelpReload() {
		
		return finalize(helpReload,2);
	}
	public String getHelpSet() {
		return finalize(helpSet,2);
	}
	public String getHelpPlayTime() {
		
		return finalize(helpPlayTime,2);
	}
	public String getHelpAdd() {
		return finalize(helpAdd,2);
	}
	public String getHelpTopBot() {
		return finalize(helpTopBot,2);
	}
	public String getNext() {
		return finalize(next,2);
	}
	public String getNextGone() {
		return finalize(nextGone,2);
	}
	public String getOnlinetime() {
		return finalize(onlinetime,2);
	}

	public String getConsolePointTypeError() {
		
		return finalize(consolePointTypeError,1);
	}

	public String getConsoleLoadingUser() {
		
		return finalize(consoleLoadingUser,1);
	}

	public String getConsoleLoadingUserDone() {
		
		return finalize(consoleLoadingUserDone,1);
	}

	public String getConsoleSQLError() {
		
		return finalize(consoleSQLError,1);
	}

	public String getConsoleConfigError() {
		
		return finalize(consoleConfigError,1);
	}

	public String getConsoleConfigSaveError() {
		
		return finalize(consoleConfigSaveError,1);
	}

	public String getConsoleMysqlError() {
		
		return finalize(consoleMysqlError,1);
	}

	public String getPluginNotUpToDate() {
		
		return finalize(pluginNotUpToDate,1);
	}

	public String getPluginUpToDate() {
		
		return finalize(pluginUpToDate,1);
	}

	public String getTransformAmount() {
		
		return finalize(transformAmount,1);
	}

	public String getErrorLoadingNewVersion() {
		
		return finalize(errorLoadingNewVersion,1);
	}

	public String getErrorPermission() {
		return finalize(errorPermission,2);
	}

	public String getErrorNoUsers() {
		return finalize(errorNoUsers,2);
	}
	
	public String getErrorUnknownUser() {
		return finalize(errorUnknownUser,2);
	}







	public void loadText() {
		 checkotherMessage = plugin.getlpConfig().checkStringVariable("check-otherplayer-message");
		 pluginTag = plugin.getlpConfig().checkStringVariable("plugin-tag");
		 selfcheckMessage = plugin.getlpConfig().checkStringVariable("self-check-message");		
	}
}
