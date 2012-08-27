/* 
 * AUTHOR: Kasper Franz
 * Loyalty Points 1.1
 * Last Changed: A hole new way to handle Database
 */


package com.github.franzmedia.LoyaltyPoints.Database;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import com.github.franzmedia.LoyaltyPoints.LPUser;
import com.github.franzmedia.LoyaltyPoints.LoyaltyPoints;

public class Database {

	 private PointsConnectionPool pool;
	    protected LoyaltyPoints  core;
	    private String prefix;

	    public Database(LoyaltyPoints core, String driverName, String url, String username, String password, String prefix) {
	        this.core = core;
	        this.prefix = prefix;
	        try {
	            pool = new PointsConnectionPool(core, driverName, url, username, password);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	
	 protected String getPrefix() {
	        return prefix;
	    }
	 
	 public synchronized ArrayList<LPUser> loadUsers() {
		 
		   ArrayList<LPUser> users = new ArrayList<LPUser>();
	        PointsConnection conn = getConnection();
	        if (conn == null)
	            return users;
	        try {
	            String sql = "SELECT `username`, `time`, `totaltime` FROM `" + prefix + "users`;";
	            PreparedStatement prest = conn.prepareStatement(sql);
	            ResultSet res = prest.executeQuery();
	            while (res.next()) {	
	                users.add(new LPUser(core, res.getString("username"), res
	    					.getInt("point"), res.getInt("time"), res
	    					.getInt("totaltime"), new Date().getTime()));
	            }
	            prest.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
		 
		 return users;
		 
		 
	 }
	 
	 /* 
	  * Function to save the user to the DB
	  * @param user - The user that should be saved.
	  *  
	  */
	   public synchronized void saveUser(LPUser user) {
		   String sql = "UPDATE `" + prefix + "users` SET `point` =  ?, `time` = ?, `totaltime` = ? WHERE `username` = ?;";
	        PointsConnection conn = getConnection();
	        if (conn == null)
	            return;
	        try {
	            PreparedStatement prest = conn.prepareStatement(sql);
	            prest.setInt(1, user.getPoint());
	            prest.setInt(2, user.getTime());
	            prest.setInt(3, user.getTotalTime());
	            prest.setString(4, user.getName());
	            
	            prest.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	   protected PointsConnection getConnection() {
	        try {
	            return pool.getConnection();
	        } catch (SQLException e) {
	            core.getLogger().severe("Unable to connect to the database: "+e.getMessage());
	            return null;
	        }
	    }

	
	  

	 
}
