package com.ir.cgtool;

import java.io.FileInputStream;
import java.util.Properties;

public class DBInfo {

	public static DBInfo _instance = null;

	private String driver = null;

	private String user = null;

	private String password = null;

	private String url = null;

	public String getDriver() {
		return driver;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getUrl() {
		return url;
	}

	protected  DBInfo() throws Exception {
		Properties props = new Properties();
		FileInputStream in = null;
		try {
			in = new FileInputStream("db.properties");

			props.load(in);
			
			driver = props.getProperty("jdbc.driver");
			url = props.getProperty("jdbc.url");
			user = props.getProperty("jdbc.username");
			password = props.getProperty("jdbc.password");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (in != null)
				in.close();
		}

	}

	public static DBInfo getInstance() throws Exception {
		if (_instance == null) {
			_instance = new DBInfo();
		}
		return _instance;
	}

}
