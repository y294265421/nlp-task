package com.liyuncong.learn.common;

public class CommonTools {
	private CommonTools(){}
	
	public static String join(String delimeter, String[] elements) {
		if (elements == null || delimeter == null) {
			throw new NullPointerException();
		}
		StringBuilder result = new StringBuilder();
		if (elements.length == 0) {
			return result.toString();
		}
		for (String s : elements) {
			result.append(s);
			result.append(delimeter);
		}
		return result.substring(0, result.length() - delimeter.length());
	}
}
