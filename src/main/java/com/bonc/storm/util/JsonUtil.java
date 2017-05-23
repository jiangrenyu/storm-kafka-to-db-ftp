//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bonc.storm.util;

import net.sf.json.JSONObject;

public class JsonUtil {
	public JsonUtil() {
	}

	public static String objectToString(Object obj) {
		try {
			return JSONObject.fromObject(obj).toString();
		} catch (Exception var2) {
			var2.printStackTrace();
			return "";
		}
	}

	public static JSONObject parseStrToJson(String str) {
		try {
			return JSONObject.fromObject(str);
		} catch (Exception var2) {
			var2.printStackTrace();
			return null;
		}
	}
}
