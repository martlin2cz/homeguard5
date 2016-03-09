package cz.martlin.hg5.ws;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Utils static methods to (probably) use int {@link WebServiceProcessor}s.
 * 
 * @author martin
 *
 */
public class WebServiceUtils {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

	/**
	 * Simply finds and returns find string or null
	 * 
	 * @param reportSpecAttrName
	 * @param request
	 * @return
	 */
	public static String getString(String param, Map<String, String[]> request) {
		String[] strs = request.get(param);
		if (strs != null && strs.length == 1) {
			return strs[0];
		} else {
			return null;
		}

	}

	/**
	 * Parses datetime (firstly formated by DATE_FORMAT then unix format).
	 * Throws exception if fails.
	 * 
	 * @param fullDateParam
	 * @param unixDateParam
	 * @param request
	 * @return
	 */
	public static Calendar parseDateTime(String fullDateParam, String unixDateParam, Map<String, String[]> request)
			throws IllegalArgumentException {
		String[] fullDateStrArr = request.get(fullDateParam);
		if (fullDateStrArr != null && fullDateStrArr.length == 1) {
			String dateStr = fullDateStrArr[0];
			return parseFullDate(fullDateParam, dateStr);
		}

		String[] unixDateStrArr = request.get(unixDateParam);
		if (unixDateStrArr != null && unixDateStrArr.length == 1) {
			String dateStr = unixDateStrArr[0];
			return parseUnixDate(unixDateParam, dateStr);
		}

		throw new IllegalArgumentException("Missing date parameter. Use \"" + fullDateParam + "\"(with format "
				+ DATE_FORMAT.toPattern() + ") or \"" + unixDateParam + "\" param");
	}

	/**
	 * Tries to parse unix date. Throws exception if fails.
	 * 
	 * @param paramName
	 * @param dateStr
	 * @return
	 */
	public static Calendar parseUnixDate(String paramName, String dateStr) throws IllegalArgumentException {
		Calendar day;
		try {
			day = Calendar.getInstance();
			long ms = Long.parseLong(dateStr);
			day.setTimeInMillis(ms);
		} catch (Exception e) {
			throw new IllegalArgumentException("Unparsable \"" + paramName + "\". Must be UTC time in ms", e);
		}

		return day;
	}

	/**
	 * Tries to parse full formated date. Throws exception if fails.
	 * 
	 * @param paramName
	 * @param dateStr
	 * @return
	 */
	public static Calendar parseFullDate(String paramName, String dateStr) throws IllegalArgumentException {
		Calendar day;
		try {
			day = Calendar.getInstance();
			Date date = DATE_FORMAT.parse(dateStr);
			day.setTime(date);
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Unparsable \"" + paramName + "\". Must be in format " + DATE_FORMAT.toPattern(), e);
		}

		return day;
	}

	/**
	 * Parses number of throws exception.
	 * 
	 * @param paramName
	 * @param request
	 * @return
	 */
	public static int parseNumber(String paramName, Map<String, String[]> request) throws IllegalArgumentException {

		String[] numStrArr = request.get(paramName);
		if (numStrArr == null || numStrArr.length != 1) {
			throw new IllegalArgumentException("Missing numeric parameter " + paramName);
		}

		String numStr = numStrArr[0];
		try {
			return Integer.parseInt(numStr);
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Value \"" + numStr + "\" of param \"" + paramName + "\" is not a number.", e);
		}
	}

	/**
	 * Parses boolean parameter. If value (which can be null) of paramName
	 * equals to valIfTrue (can be null), returns true, if is equal to
	 * valIfFalse (can be null too), returns false, else throw exception.
	 * 
	 * @param paramName
	 * @param request
	 * @param valIfTrue
	 * @param valIfFalse
	 * @return
	 */
	public static boolean parseBoolean(String paramName, Map<String, String[]> request, String valIfTrue,
			String valIfFalse) throws IllegalArgumentException {

		String[] boolStrArr = request.get(paramName);
		if (boolStrArr != null && boolStrArr.length != 1) {
			throw new IllegalArgumentException("Missing (or overdosed) boolean parameter " + paramName);
		}

		String numStr = (boolStrArr == null) ? null : boolStrArr[0];

		if (numStr == null) {
			if ((valIfTrue == null)) {
				return true;
			}
			if ((valIfFalse == null)) {
				return false;
			}
		} else {
			if (numStr.equals(valIfTrue)) {
				return true;
			}
			if (numStr.equals(valIfFalse)) {
				return false;
			}
		}

		throw new IllegalArgumentException(
				"Boolean parameter " + paramName + " is nor " + valIfTrue + " or " + valIfFalse);
	}

	/**
	 * Tries to parse color and if fails returns default color.
	 * 
	 * @param paramName
	 * @param request
	 * @param dfltColor
	 * @return
	 */
	public static Color parseColorOrDefault(String paramName, Map<String, String[]> request, Color dfltColor) {
		Color color = null;
		try {
			color = parseColor(paramName, request);
		} catch (IllegalArgumentException e) {
			color = null;
		}

		if (color != null) {
			return color;
		} else {
			return dfltColor;
		}
	}

	/**
	 * Parses color as #rrggbb or #rrggbbaa.
	 * 
	 * @param paramName
	 * @param request
	 * @return
	 */
	public static Color parseColor(String paramName, Map<String, String[]> request) {
		String[] value = request.get(paramName);
		if (value == null || value.length != 1) {
			throw new IllegalArgumentException("Missing color parameter " + paramName + ". Use #rrggbb[aa] format");
		}

		Color color = colorFromRGBA(value[0]);
		return color;

	}

	/**
	 * Tries to parse given string as color. Returns null if fails.
	 * 
	 * @param str
	 * @return
	 */
	public static Color colorFromRGBA(String str) {
		try {
			if (str.charAt(0) == '#') {
				str = str.substring(1);
			}
			if (str.length() < 8) {
				str = str + "FF";
			}

			int red = Integer.valueOf(str.substring(0, 2), 16);
			int green = Integer.valueOf(str.substring(2, 4), 16);
			int blue = Integer.valueOf(str.substring(4, 6), 16);
			int alpha = Integer.valueOf(str.substring(6, 8), 16);

			return new Color(red, green, blue, alpha);
		} catch (Exception e) {
			return null;
		}
	}

}
