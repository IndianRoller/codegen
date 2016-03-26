
package com.ir.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ConnectionUtil {

	private static DataSource ds = null;

	public static Connection getConnection() throws Exception {
		if(DBInfo.USE_DATASOURCE_TRUE.equalsIgnoreCase(StringUtil.nullCheck(System.getProperty(DBInfo.USE_DATASOURCE), DBInfo.USE_DATASOURCE_TRUE))){
			return getConnectionDS();
		}else {
			return getConnectionStandAlone();
		}
 	}

	private static Connection getConnectionDS() throws NamingException, SQLException {
		if (ds == null) {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			ds = (DataSource) envCtx.lookup("ds/budgetApp");

		}
		return ds.getConnection();
	}

	private static Connection getConnectionStandAlone() throws Exception {
		Properties dbProperties = DBInfo.getInstance().getDBProperties();

		Class.forName(dbProperties.getProperty("jdbc.driver"));

		return DriverManager.getConnection(dbProperties.getProperty("jdbc.url"),
				dbProperties.getProperty("jdbc.username"), dbProperties.getProperty("jdbc.password"));
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
