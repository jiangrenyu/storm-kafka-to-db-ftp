//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bonc.storm.jdbc;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class C3P0ConnectionProvider implements ConnectionProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DBManager.class);
    private Properties properties;
    private transient DataSource dataSource;

    public C3P0ConnectionProvider(Properties dbProperties) {
        this.properties = dbProperties;
    }

    public synchronized void prepare() {
        if(this.dataSource == null) {
            try {
                this.dataSource = BasicDataSourceFactory.createDataSource(this.properties);
                LOG.info("创建数据源成功");
            } catch (Exception var2) {
                LOG.info("创建数据源失败：" + var2);
            }
        }

    }

    public Connection getConnection() {
        try {
            return this.dataSource.getConnection();
        } catch (SQLException var2) {
            throw new RuntimeException(var2);
        }
    }
}
