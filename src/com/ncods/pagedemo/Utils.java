package com.ncods.pagedemo;

public class Utils {

	/**
	 * 判断字符串是否为空或空白字符
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		int length;
		if ((str == null) || ((length = str.length()) == 0)) {
			return true;
		}
		for (int i = 0; i < length; i++) {
			if (false == isBlankChar(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	public static boolean isBlankChar(int c) {
		return Character.isWhitespace(c) || Character.isSpaceChar(c) || c == '\ufeff' || c == '\u202a' || c == '\u0000';
	}
}
