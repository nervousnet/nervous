package ch.ethz.soms.nervous.utils;

public class ValueFormatter {
	public static String leadingZeroHexUpperString(int i) {
		return Integer.toHexString(0x10000 | i).substring(1).toUpperCase();
	}
	
	public static String leadingZeroHexLowerString(int i) {
		return Integer.toHexString(0x10000 | i).substring(1);
	}
}
