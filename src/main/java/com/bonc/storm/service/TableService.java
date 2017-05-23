//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bonc.storm.service;

import com.bonc.storm.jdbc.DBManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TableService {
    private DBManager dbManager;

    public TableService(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    public Map<String, String> getTableAttr() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            connection = this.dbManager.getConnection();
            statement = connection.prepareStatement("select itf_path ,table_attr from conf_path_info_web where is_valid=1");
            rs = statement.executeQuery();
            HashMap e = new HashMap();

            while(rs.next()) {
                if(rs.getString("itf_path") != null) {
                    e.put(rs.getString("itf_path").trim(), rs.getString("table_attr").trim());
                }
            }

            HashMap var6 = e;
            return var6;
        } catch (SQLException var14) {
            var14.printStackTrace();
        } finally {
            try {
                if(rs != null) {
                    rs.close();
                }

                if(statement != null) {
                    statement.close();
                }

                if(connection != null) {
                    connection.close();
                }
            } catch (SQLException var13) {
                var13.printStackTrace();
            }

        }

        return null;
    }
}
