package com.ir.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.ir.cgtool.DBInfo;

public class ConnectionUtil {
    
	public static Connection getConnection() throws Exception {
		DBInfo dbInfo = DBInfo.getInstance();
		Class.forName(dbInfo.getDriver());
		return DriverManager.getConnection(dbInfo.getUrl(), dbInfo.getUser(), dbInfo.getPassword());
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
