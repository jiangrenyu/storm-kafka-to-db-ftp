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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProvService {
	private DBManager dbManager;

	public ProvService(DBManager dbManager) {
		this.dbManager = dbManager;
	}

	public Map<String, Integer> getProvInfo() {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		try {
			connection = this.dbManager.getConnection();
			statement = connection.prepareStatement("select prov_code,prov_desc,prov_desc2 from d_prov");
			rs = statement.executeQuery();
			HashMap e = new HashMap();

			while(rs.next()) {
				if(rs.getString("prov_desc") != null) {
					e.put(rs.getString("prov_desc").trim(), Integer.valueOf(rs.getInt("prov_code")));
				}

				if(rs.getString("prov_desc2") != null) {
					e.put(rs.getString("prov_desc2").trim(), Integer.valueOf(rs.getInt("prov_code")));
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

	public Map<String, List<Map<String, Object>>> getFileNameRegular() {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		try {
			connection = this.dbManager.getConnection();
			statement = connection.prepareStatement("select file_path,fname_regular,prov_id from conf_tb_info where is_valid = 1 and  table_attr=\'cdr\' group by file_path,fname_regular,prov_id order by file_path, prov_id");
			rs = statement.executeQuery();
			HashMap e = new HashMap();

			while(rs.next()) {
				HashMap rule = new HashMap();
				rule.put("fname_regular", rs.getString("fname_regular").trim());
				rule.put("prov_id", Integer.valueOf(rs.getInt("prov_id")));
				String filePath = rs.getString("file_path");
				if(filePath != null) {
					filePath = filePath.trim().endsWith("/")?filePath.trim():filePath.trim() + "/";
					List ruleList = (List)e.get(filePath);
					if(ruleList == null) {
						ArrayList ruleList1 = new ArrayList();
						ruleList1.add(rule);
						e.put(filePath, ruleList1);
					} else {
						ruleList.add(rule);
					}
				}
			}

			HashMap var9 = e;
			return var9;
		} catch (SQLException var17) {
			var17.printStackTrace();
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
			} catch (SQLException var16) {
				var16.printStackTrace();
			}

		}

		return null;
	}
}
