package com.ir.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.ir.cgtool.CGToolInfo;

public class ConnectionUtil {
    
	public static Connection getConnection() throws Exception {
		Properties cgToolProperties = CGToolInfo.getInstance().getCgToolProperties();
		
		Class.forName(cgToolProperties.getProperty("jdbc.driver"));
		
		return DriverManager.getConnection(cgToolProperties.getProperty("jdbc.url"),
										   cgToolProperties.getProperty("jdbc.username"), 
										   cgToolProperties.getProperty("jdbc.password"));
	}

	public static void closeConnection(Connection connection) throws SQLException {
		if (connection != null)
			connection.close();
	}

	public static void closeConnection(Connection connection, ResultSet rs, Statement stmt) throws SQLException {

		if (rs != null)
			rs.close();

		if (stmt != null)
			stmt.close();

		if (connection != null)
			connection.close();

	}

	public static void closeConnection(Connection connection, Statement stmt) throws SQLException {
 
		if (stmt != null)
			stmt.close();

		if (connection != null)
			connection.close();

	}
	
	public static void closeConnection(ResultSet rs, PreparedStatement psmt) throws SQLException {

		if (rs != null)
			rs.close();

		if (psmt != null)
			psmt.close();
	}
	
	public static void main(String args[]) throws Exception {
		Connection connection = getConnection();
 		closeConnection(connection);
	}

} 
