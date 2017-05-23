//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bonc.storm.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyConfig {
	private static final Logger LOG = LoggerFactory.getLogger(PropertyConfig.class);
	private Properties properties = new Properties();

	public PropertyConfig(String filePath) throws Exception {
		if(filePath != null) {
			FileInputStream inputStream = new FileInputStream(new File(filePath));
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			this.properties.load(reader);
			if(inputStream != null) {
				inputStream.close();
			}

			if(reader != null) {
				reader.close();
			}

		} else {
			throw new Exception("can\'t found file:" + filePath);
		}
	}

	public String getValue(String key) {
		if(this.properties != null) {
			try {
				String e = this.properties.getProperty(key);
				if(e != null && e.length() > 0) {
					return e.trim();
				}
			} catch (Exception var3) {
				LOG.info("获取属性 " + key + " 的值失败；", var3);
				return null;
			}
		}

		return null;
	}

	public String getUnicodeValue(String value) {
		String unicodeStr = this.getValue(value);
		if(unicodeStr != null) {
			StringBuilder buf = new StringBuilder();
			String[] cc = unicodeStr.split("\\\\u");
			String[] var8 = cc;
			int var7 = cc.length;

			for(int var6 = 0; var6 < var7; ++var6) {
				String c = var8[var6];
				if(!c.equals("")) {
					int cInt = Integer.parseInt(c, 16);
					char cChar = (char)cInt;
					buf.append(cChar);
				}
			}

			return buf.toString();
		} else {
			return unicodeStr;
		}
	}
}
