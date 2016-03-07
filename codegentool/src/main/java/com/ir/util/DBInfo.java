package com.ir.util;

import java.io.FileInputStream;
import java.util.Properties;

public class DBInfo {

	public static DBInfo _instance = null;

	private Properties dbProperties = null;
	
	public Properties getDBProperties() {
		return dbProperties;
	}

	protected DBInfo() throws Exception {

		FileInputStream in = null;
		try {
			in = new FileInputStream("db.properties");

			dbProperties = new Properties();
			dbProperties.load(in);

		} catch (Exception e) {
			throw e;
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
