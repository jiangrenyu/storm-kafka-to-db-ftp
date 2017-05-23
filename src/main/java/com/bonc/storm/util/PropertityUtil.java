//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bonc.storm.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertityUtil {
	private static final Logger LOG = LoggerFactory.getLogger(PropertityUtil.class);

	public PropertityUtil() {
	}

	public static String getValue(String fileName, String key) {
		if(fileName != null && key != null) {
			InputStream inputStream = PropertityUtil.class.getClassLoader().getResourceAsStream(fileName);
			Properties properties = new Properties();

			try {
				properties.load(inputStream);
				String e = properties.getProperty(key);
				if(e != null && e.length() > 0) {
					String var6 = e.trim();
					return var6;
				}
			} catch (IOException var15) {
				LOG.error("加载 " + fileName + "配置文件失败：", var15);
			} finally {
				try {
					if(inputStream != null) {
						inputStream.close();
					}
				} catch (IOException var14) {
					var14.printStackTrace();
				}

			}
		}

		return null;
	}

	public static String readUnicodeStr(String unicodeStr) {
		StringBuilder buf = new StringBuilder();
		String[] cc = unicodeStr.split("\\\\u");
		String[] var6 = cc;
		int var5 = cc.length;

		for(int var4 = 0; var4 < var5; ++var4) {
			String c = var6[var4];
			if(!c.equals("")) {
				int cInt = Integer.parseInt(c, 16);
				char cChar = (char)cInt;
				buf.append(cChar);
			}
		}

		return buf.toString();
	}
}
