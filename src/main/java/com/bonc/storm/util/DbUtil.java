//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bonc.storm.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbUtil {
	private static String driver = null;
	private static String url = null;
	private static String username = null;
	private static String password = null;

	static {
		InputStream is = null;
		Properties p = null;

		try {
			is = DbUtil.class.getClassLoader().getResourceAsStream("jdbc.properties");
			p = new Properties();
			p.load(is);
			driver = p.getProperty("DBDriver");
			url = p.getProperty("DBUrl");
			username = p.getProperty("DBuser");
			password = p.getProperty("DBpassword");
		} catch (IOException var13) {
			var13.printStackTrace();
		} finally {
			try {
				if(is != null) {
					is.close();
				}
			} catch (IOException var11) {
				var11.printStackTrace();
			}

		}

		try {
			Class.forName(driver);
		} catch (ClassNotFoundException var12) {
			var12.printStackTrace();
		}

	}

	public DbUtil() {
	}

	public static Connection getConnection() {
		try {
			Connection e = DriverManager.getConnection(url, username, password);
			return e;
		} catch (SQLException var1) {
			var1.printStackTrace();
			return null;
		}
	}
}
