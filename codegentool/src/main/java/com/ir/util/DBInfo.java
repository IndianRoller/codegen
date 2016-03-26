package com.ir.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class DBInfo {

	public static final String DB_CONFIGURATION = "DB_CONFIGURATION";
	public static final String DB_CONFIGURATION_DEF = "db.properties";

	public static final String USE_DATASOURCE = "USE_DATASOURCE";
	
	public static final String USE_DATASOURCE_FALSE = "FALSE";
	public static final String USE_DATASOURCE_TRUE = "TRUE";
	
	public static final String USE_DATASOURCE_DEF = USE_DATASOURCE_TRUE;
	
	public static DBInfo _instance = null;

	private Properties dbProperties = null;

	public Properties getDBProperties() {
		return dbProperties;
	}

	protected DBInfo() throws Exception {
	 
		InputStream in = null;
		try {
			in = new FileInputStream(StringUtil.nullCheck(System.getProperty(DB_CONFIGURATION), DB_CONFIGURATION_DEF));
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
