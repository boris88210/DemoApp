package com.chailease.tw.app.android.utils;

import android.text.Editable;

import com.chailease.tw.app.android.constants.ValueConstants;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 *
 */
public class InputUtils {

	public static final int INT_EMPTY           =   ValueConstants.INT_EMPTY;
	public static final int INT_NOT_NUMBER    =   ValueConstants.INT_NOT_NUMBER;
	public static final short SHORT_EMPTY                       = -32768;
	public static final short SHORT_ZERO_OR_NOT_SHORT = 0;
	public static final float FLOAT_EMPTY = Float.MIN_VALUE;
	public static final float FLOAT_ZERO_OR_NOT_FLOAT   =   0;
	public static final double DOUBLE_EMPTY = Double.MIN_VALUE;
	public static final double DOUBLE_ZERO_OR_NOT_DOUBLE    =   0;
	static final String intPattern = "((-)?(0)*(1?\\d{1,9}|20\\d{8}|21[1-3]\\d{7}|214[1-6]\\d{6}|2147[1-3]\\d{5}|21474[1-7]\\d{4}|214748[1-2]\\d{3}|2147483[1-5]\\d{2}|21474836[1-3]\\d|214748364[1-7])|-2147483648)";
	static final String longPattern = "(-)?\\d+";
	static final String numPattern = "(-)?\\d+(.\\d+)?";
	static final String shortPattern = "((-)?(0)*([1-2]?\\d{1,4}|31\\d{3}|32[1-6]\\d{2}|327[1-5]\\d|3276[1-7])|-32768)";
	static final Pattern cNumPattern = Pattern.compile(numPattern);
	static final Pattern cIntPattern = Pattern.compile(intPattern);
	static final Pattern cLongPattern = Pattern.compile(longPattern);
	static final Pattern cShortPattern = Pattern.compile(shortPattern);

	public static boolean isDoubleValue(String val) {
		if (val != null) {
			val = val.replaceAll(",", "");
			if (cNumPattern.matcher(val).matches()) {
				try {
					Double.parseDouble(val);
					return true;
				} catch (NumberFormatException nfe) {
					return false;
				}
			}
		}
		return false;
	}
	public static double getDoubleValue(Editable txt) {
		String str = txt!=null ? txt.toString() : null;
		if (!StringUtils.isBlank(str)) {
			str = str.replaceAll(",", "");
			if (str.matches(numPattern)) {
				try {
					return Double.parseDouble(str);
				} catch (NumberFormatException nfe) {}
			}
			return DOUBLE_ZERO_OR_NOT_DOUBLE;
		}
		return DOUBLE_EMPTY;
	}

	public static boolean isFloatValue(String val) {
		if (val != null) {
			val = val.replaceAll(",", "");
			if (cNumPattern.matcher(val).matches()) {
				try {
					Float.parseFloat(val);
					return true;
				} catch (NumberFormatException nfe) {
					return false;
				}
			}
		}
		return false;
	}
	public static float getFloatValue(Editable txt) {
		String str = txt!=null ? txt.toString() : null;
		if (!StringUtils.isBlank(str)) {
			str = str.replaceAll(",", "");
			if (str.matches(numPattern)) {
				try {
					return Float.parseFloat(str);
				} catch (NumberFormatException nfe) {}
			}
			return FLOAT_ZERO_OR_NOT_FLOAT;
		}
		return FLOAT_EMPTY;
	}

	public static short getShortValue(Editable txt) {
		//–32,768 to 32,767
		String str = txt!=null ? txt.toString() : null;
		if (!StringUtils.isBlank(str)) {
			str = str.replaceAll(",", "");
			if(str.matches(shortPattern)) {
				return Short.parseShort(str);
			} else if (str!=null) {
				return SHORT_ZERO_OR_NOT_SHORT;
			}
		}
		return SHORT_EMPTY;
	}
	public static boolean isShortValue(String val) {
		if (val!=null) {
			val = val.replaceAll(",", "");
			return cShortPattern.matcher(val).matches();
		}
		return false;
	}

	public static int getIntValue(Editable txt) {
		String str = txt!=null ? txt.toString() : null;
		if (!StringUtils.isBlank(str)) {
			str = str.replaceAll(",", "");
			if (str.matches(intPattern)) {
//				2147483647
//				-2147483648
				return Integer.parseInt(str);
			} else if (str != null) {
				return INT_NOT_NUMBER;
			}
		}
		return INT_EMPTY;
	}
	public static boolean isIntValue(String val) {
		if (val != null) {
			val = val.replaceAll(",", "");
			return cIntPattern.matcher(val).matches();
		}
		return false;
	}

	public static long getLongValue(Editable txt) {
		String str = txt!=null ? txt.toString() : null;
		if (!StringUtils.isBlank(str)) {
			str = str.replaceAll(",", "");
			if (str.matches(longPattern)) {
				return Long.parseLong(str);
			} else if (str != null) {
				return INT_NOT_NUMBER;
			}
		}
		return INT_EMPTY;
	}
	public static boolean isLongValue(String val) {
		if (val != null) return cLongPattern.matcher(val).matches();
		return false;
	}

	public static boolean nonInt(int val) {
		if (INT_NOT_NUMBER==val || INT_EMPTY==val) {
			return true;
		}
		return false;
	}

	public static boolean nonInt(String val) {
		if (StringUtils.isBlank(val) || !val.matches(intPattern)) {
			return true;
		}
		return false;
	}
	public static boolean nonNumber(String val) {
		if (StringUtils.isBlank(val) || !val.matches(numPattern)) return true;
		return false;
	}

}