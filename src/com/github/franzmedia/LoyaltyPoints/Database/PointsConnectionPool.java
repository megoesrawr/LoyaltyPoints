package com.github.franzmedia.LoyaltyPoints.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.github.franzmedia.LoyaltyPoints.LoyaltyPoints;

public class PointsConnectionPool {
	private PointsConnection connection;
	private String url;
	private String username;
	private String password;

	public PointsConnectionPool(LoyaltyPoints core, String driverName,
			String url, String username, String password)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {
		// Driver driver = (Driver) Class.forName(driverName, true,
		// URLClassLoader.newInstance(null));
		// PointsDriver pDriver = new PointsDriver(driver);
		// DriverManager.registerDriver(pDriver);
		this.url = url;
		this.username = username;
		this.password = password;
	}

	public synchronized PointsConnection getConnection() throws SQLException {
		if (connection != null
				&& (connection.isClosed() || !connection.isValid(1))) {
			try {
				connection.closeConnection();
			} catch (SQLException e) {
			}

			connection = null;
		}

		if (connection == null) {
			Connection conn = DriverManager.getConnection(url, username,
					password);
			connection = new PointsConnection(conn);
		}

		return connection;
	}

	public synchronized void closeConnection() {
		if (connection != null) {
			try {
				connection.closeConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
}
