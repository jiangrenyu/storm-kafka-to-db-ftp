//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bonc.storm.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class FileUtil {
	public FileUtil() {
	}

	public static Properties getProperteis(String filePath) throws Exception {
		if(filePath != null) {
			Properties pro = new Properties();
			FileInputStream inputStream = new FileInputStream(new File(filePath));
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			pro.load(reader);
			if(inputStream != null) {
				inputStream.close();
			}

			if(reader != null) {
				reader.close();
			}

			return pro;
		} else {
			throw new Exception("can\'t found file:" + filePath);
		}
	}

	public static String getStringFromInputStream(InputStream inputStream) {
		if(inputStream != null) {
			BufferedReader reader = null;
			StringBuilder sb = new StringBuilder();
			String line = null;

			try {
				reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

				while((line = reader.readLine()) != null) {
					sb.append(line);
					sb.append("\n");
				}
			} catch (IOException var13) {
				var13.printStackTrace();
			} finally {
				try {
					if(inputStream != null) {
						inputStream.close();
					}
				} catch (IOException var12) {
					var12.printStackTrace();
				}

			}

			return sb.toString();
		} else {
			return null;
		}
	}
}
