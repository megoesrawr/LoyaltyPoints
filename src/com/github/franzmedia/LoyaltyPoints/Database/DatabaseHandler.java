package com.github.franzmedia.LoyaltyPoints.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Logger;
import com.github.franzmedia.LoyaltyPoints.LPUser;
import com.github.franzmedia.LoyaltyPoints.LoyaltyPoints;

public class DatabaseHandler {
	private int type;
	private Logger log;
	private String hostname, portnmbr, database, username, password;
	private MySQL mysql;
	private SQLite sqlite;
	private LoyaltyPoints plugin;

	public DatabaseHandler(LoyaltyPoints plugin, Logger log, int type,
			String hostname, String portnmbr, String database, String username,
			String password) {

		this.type = type;
		this.plugin = plugin;
		this.hostname = hostname;
		this.portnmbr = portnmbr;
		this.database = database;
		this.username = username;
		this.password = password;
		openConn();

	}

	private void openConn() {
		if (type == 1) {
			mysql = new MySQL(log, "[LoyaltyPoints]", hostname, portnmbr,
					database, username, password);
			mysql.open();
		} else {
			sqlite = new SQLite(log, "[LoyaltyPoints]", "lp",
					plugin.getDataFolder());
			sqlite.open();
		}
		if (!checkTable("users")) {
			createTable("CREATE TABLE users"
					+ "( 	username varchar(16) NOT NULL PRIMARY KEY,"
					+ "	point	 int(16)," + "	totaltime int(25),"
					+ "	time		int(10))");
		}
	}

	private boolean checkTable(String table) {
		checkConnection();
		boolean tableBoolean;
		if (type == 1) {
			tableBoolean = mysql.checkTable(table);
		} else {
			tableBoolean = sqlite.checkTable(table);
		}

		return tableBoolean;
	}

	private boolean checkConnection() {
		boolean connectionBoolean;
		if (type == 1) {
			connectionBoolean = mysql.checkConnection();
		} else {
			connectionBoolean = sqlite.checkConnection();
		}

		return connectionBoolean;
	}

	public void saveUser(LPUser user) {
		checkConnection();
		String sql = "UPDATE users SET point = \"" + user.getPoint() + "\", "
				+ "time = \"" + user.getTime() + "\", " + "totaltime = \""
				+ user.getTotalTime() + "\" WHERE username = \""
				+ user.getName() + "\"";
		plugin.debug(sql);
		if (type == 1) {
			mysql.query(sql);
		} else {
			sqlite.query(sql);
		}
	}

	private boolean checkUser(LPUser user) {
		boolean returnboolean = false;
		ResultSet rs;
		int c = 0;
		String query = "SELECT count(*) as c FROM  users WHERE username=\""
				+ user.getName() + "\"";

		if (type == 1) {

			rs = mysql.query(query);
		} else {
			rs = sqlite.query(query);
		}
		try {
			rs.next();
			c = rs.getInt("c");
		} catch (SQLException e) {
			// SQL ERROR
		}
		if (c > 0) {
			returnboolean = true;
		}

		return returnboolean;
	}

	public void saveUsers(LPUser[] users) {

		if (type == 1) {
			for (int i = 0; i < users.length; i++) {
				String sql = "UPDATE users SET point = \""
						+ users[i].getPoint() + "\", " + "time = \""
						+ users[i].getTime() + "\", " + "totaltime = \""
						+ users[i].getTotalTime() + "\" WHERE username = \""
						+ users[i].getName() + "\"";
				ResultSet rs = mysql.query(sql);
				try {
					if (!rs.rowUpdated()) {
						insertUser(users[i]);
					}
				} catch (SQLException e) {
					log.warning("SQL ERROR");
				}
				try {
					rs.close();
				} catch (SQLException e) {
					// log.warning("SQL ERROR");

				}
			}
		} else {
			for (int i = 0; i < users.length; i++) {

				// TODO: MAKE IT SO IT DOES IT LOKE WITH THE MYSQL!
				String sql = "UPDATE users SET point = \""
						+ users[i].getPoint() + "\", " + "time = \""
						+ users[i].getTime() + "\", " + "totaltime = \""
						+ users[i].getTotalTime() + "\" WHERE username = \""
						+ users[i].getName() + "\"";
					sqlite.query(sql);
					
				
			}
		}
	}

	public void insertUser(LPUser user) {

		if (checkUser(user)) {

		} else {
			String sql = "INSERT INTO users VALUES (\"" + user.getName()
					+ "\",\"" + user.getPoint() + "\"" + ",\""
					+ user.getTotalTime() + "\",\"" + user.getTime() + "\")";
			if (type == 1) {
				mysql.query(sql);
			} else {
				sqlite.query(sql);
			}

		}

	}

	public LPUser GetUser(String username) {
		// TODO: MAKE THE get user ELSE INSERT HIM!
		LPUser user = null;
		long now = new Date().getTime();
		
		int last = 0;
		
		ResultSet rs;
		String query = "SELECT count(*) as c FROM users WHERE username = '"+username+"'";
		
		
		if(type == 1){
			rs = mysql.query(query);
		}else{
			rs = sqlite.query(query);
		}
		
		try {
			rs.next();
		
		last = rs.getInt("c");
		rs.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 query = "SELECT * from users where username = '"+username+"'";
		
		if(type == 1){
			rs = mysql.query(query);
		}else{
			rs = sqlite.query(query);
		}
		try{
		rs.next();
		
		if(last == 0){
			// NEW USER!!!!
			user= new LPUser(plugin, username,
					plugin.getStartingPoints(), 0,
					0, now);
			insertUser(user);
			
		}else{
			plugin.debug("OLD ONE");
			//old one
			user= new LPUser(plugin, rs.getString("username"),
					rs.getInt("point"), rs.getInt("time"),
					rs.getInt("totaltime"), now);
		}
		rs.close();
		}catch (SQLException e) {
			log.warning("SQL ERROR ON GET USERS");
		}
		
		
		return user;
	}

	public LPUser[] getTop(final int from, final int to) {
		LPUser[] users = null;
		
		String query = "SELECT username,point,totaltime, time, count(*) as c FROM users order by point desc LIMIT "
				+ from + "," + to;
		
		plugin.debug(query);
		ResultSet rs;
		if (type == 1) {
			rs = mysql.query(query);
		} else {
			rs = sqlite.query(query);
		}
		plugin.debug("before try");
		try {
			rs.next();
			users = new LPUser[rs.getInt("c")];
			
			Long now = new Date().getTime();
			users[0] = new LPUser(plugin, rs.getString("username"),
					rs.getInt("point"), rs.getInt("time"),
					rs.getInt("totaltime"), now);
			for (int i = 1; rs.next(); i++) {
				plugin.debug(rs.getString("username"));
				users[i] = new LPUser(plugin, rs.getString("username"),
						rs.getInt("point"), rs.getInt("time"),
						rs.getInt("totaltime"), now);
			}
			rs.close();

			
		} catch (SQLException e) {

			//SQLERROR
		}
		return users;
	}

	private void createTable(String query) {

		if (type == 1) {
			mysql.createTable(query);
		} else {
			sqlite.createTable(query);
		}
	}

	public void close() {
		if (type == 1) {
			mysql.close();
		} else {
			sqlite.close();
		}
	}
	
	
}
