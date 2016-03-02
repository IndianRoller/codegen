package com.ir.cgtool;

import java.io.FileInputStream;
import java.util.Properties;

public class CGToolInfo {

	public static CGToolInfo _instance = null;

	private Properties cgToolProperties = null;
	
	public Properties getCgToolProperties() {
		return cgToolProperties;
	}

	protected CGToolInfo() throws Exception {

		FileInputStream in = null;
		try {
			in = new FileInputStream("cgtool.properties");

			cgToolProperties = new Properties();
			cgToolProperties.load(in);

		} catch (Exception e) {
			throw e;
		} finally {
			if (in != null)
				in.close();
		}

	}

	public static CGToolInfo getInstance() throws Exception {
		if (_instance == null) {
			_instance = new CGToolInfo();
		}
		return _instance;
	}

}
