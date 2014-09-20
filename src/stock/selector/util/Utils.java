package stock.selector.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class Utils {
	public static String timeformat = "MM/dd/yyyy";
	public static SimpleDateFormat SF = new SimpleDateFormat(timeformat);

	public static SimpleDateFormat SF1 = new SimpleDateFormat("yyyy-MM-dd");

	public static Date format(String strDate) throws ParseException {
		return SF.parse(strDate);
	}

	public static String format(Date time) {
		return SF1.format(time);
	}

	public static Date parseDate(String date) throws ParseException {
		return SF1.parse(date);
	}

	public static void main(String[] args) throws ParseException {
		format("02/02/1998");
	}

	public static double handleDouble(String x) {
		Double y;
		if (Pattern.matches("N/A", x)) {
			y = 0.00;
		} else {
			y = Double.parseDouble(x);
		}
		return y;
	}

	public static int handleInt(String x) {
		int y;
		if (Pattern.matches("N/A", x)) {
			y = 0;
		} else {
			y = Integer.parseInt(x);
		}
		return y;
	}
}
