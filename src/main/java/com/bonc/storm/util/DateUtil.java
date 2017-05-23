//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bonc.storm.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	public DateUtil() {
	}

	public static String formatDate(Date date, String format) {
		try {
			SimpleDateFormat e = new SimpleDateFormat(format);
			return e.format(date);
		} catch (Exception var3) {
			var3.printStackTrace();
			return null;
		}
	}

	public static Date parseDate(String dateStr, String dateFormat) {
		if(dateStr != null && dateFormat != null) {
			try {
				SimpleDateFormat e = new SimpleDateFormat(dateFormat);
				return e.parse(dateStr);
			} catch (ParseException var3) {
				var3.printStackTrace();
			}
		}

		return null;
	}
}
