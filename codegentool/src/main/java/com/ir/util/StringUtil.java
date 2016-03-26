package com.ir.util;


public class StringUtil {

    public static final String LINE_SEPARTOR = System.getProperty("line.separator");
	
	public static boolean isEmpty(String input) {

		if (null == input || "".equals(input.trim()))
			return true;

		return false;
	}

	public static String nullCheck(String input,String defaultValue) {

		if (null == input || "".equals(input.trim()))
			return defaultValue;

		return input;
	}

	
}
