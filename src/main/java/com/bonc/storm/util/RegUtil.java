//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bonc.storm.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegUtil {
	private static final Logger LOG = LoggerFactory.getLogger(RegUtil.class);

	public RegUtil() {
	}

	public static boolean isMatch(String str, String reg) {
		if(str != null && reg != null) {
			try {
				Pattern e = Pattern.compile(reg);
				Matcher matcher = e.matcher(str);
				return matcher.matches();
			} catch (Exception var4) {
				LOG.error("使用正则 " + reg + " 匹配字符 " + str + "异常：", var4);
			}
		}

		return false;
	}

	public static String find(String str, String reg) {
		if(str != null && reg != null) {
			try {
				Pattern e = Pattern.compile(reg);
				Matcher matcher = e.matcher(str);
				if(matcher.find()) {
					return matcher.group(0);
				}
			} catch (Exception var4) {
				LOG.error("使用正则 " + reg + " 匹配字符 " + str + "异常：", var4);
			}
		}

		return null;
	}

	public static String replace(String str, String reg, String replacedStr) {
		return str != null && reg != null && replacedStr != null?str.replace(reg, replacedStr):str;
	}
}
