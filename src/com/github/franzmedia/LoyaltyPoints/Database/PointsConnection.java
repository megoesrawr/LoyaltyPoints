package com.github.franzmedia.LoyaltyPoints.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class PointsConnection {
	    private Connection conn;
	    public PointsConnection(Connection conn) {
	        this.conn = conn;
	    }
	    
	    public synchronized boolean isClosed() {
	        try {
	            return conn.isClosed();
	        } catch(SQLException e) {
	        	
	            return true;
	        }
	    }
	    
	    public synchronized boolean isValid(int timeout) throws SQLException {
	        try {
	            return conn.isValid(timeout);
	        } catch (AbstractMethodError e) {
	            return true;
	        }
	    }
	    
	    public synchronized void closeConnection() throws SQLException {
	        conn.close();
	    }
	    
	    public synchronized Statement createStatement() throws SQLException {
	        return conn.createStatement();
	    }
	    
	    public synchronized PreparedStatement prepareStatement(String sql) throws SQLException {
	        return conn.prepareStatement(sql);
	    }

}
