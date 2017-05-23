//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bonc.storm.jdbc;

import com.bonc.storm.util.FileUtil;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBManager {
	private static final Logger LOG = LoggerFactory.getLogger(DBManager.class);
	private DataSource dataSource = null;
	private Properties properties = null;

	public DBManager(String configPath) throws Exception {
		if(configPath != null) {
			this.properties = FileUtil.getProperteis(configPath);
			this.dataSource = BasicDataSourceFactory.createDataSource(this.properties);
		} else {
			throw new Exception("can\'t found configPath:" + configPath);
		}
	}

	public DBManager(Properties properties) throws Exception {
		try {
			this.properties = properties;
			this.dataSource = BasicDataSourceFactory.createDataSource(properties);
		} catch (Exception var3) {
			throw new Exception("create datasource error:" + var3.getMessage());
		}
	}

	public Properties getProperties() {
		return this.properties;
	}

	public DataSource getDataSource() {
		return this.dataSource;
	}

	public Connection getConnection() {
		try {
			if(this.dataSource != null) {
				return this.dataSource.getConnection();
			}
		} catch (SQLException var2) {
			LOG.info("获取数据库连接失败：" + var2);
		}

		return null;
	}

	public static void closeConnection(Connection conn) {
		try {
			if(conn != null) {
				conn.close();
			}
		} catch (SQLException var2) {
			var2.printStackTrace();
		}

	}
}
