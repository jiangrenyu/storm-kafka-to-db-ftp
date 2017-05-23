//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bonc.storm.jdbc;

import com.bonc.storm.trident.mapper.JdbcMapper;
import org.apache.storm.trident.tuple.TridentTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

public class LogFtpFileJdbcClient {
	private static final Logger LOG = LoggerFactory.getLogger(LogFtpFileJdbcClient.class);
	private ConnectionProvider connectionProvider;
	private JdbcMapper mapper;
	private int batchSize;

	public LogFtpFileJdbcClient(ConnectionProvider connectionProvider, JdbcMapper mapper, int batchSize) {
		this.connectionProvider = connectionProvider;
		this.mapper = mapper;
		this.batchSize = batchSize;
	}

	public void executeInsert(String insertStatement, List<TridentTuple> tuples) {
		if(tuples != null && tuples.size() != 0) {
			Connection conn = null;
			PreparedStatement pstmt = null;

			try {
				conn = this.connectionProvider.getConnection();
				conn.setAutoCommit(false);
				pstmt = conn.prepareStatement(insertStatement);
				long e2 = 0L;
				Iterator var8 = tuples.iterator();

				while(true) {
					List paramList;
					do {
						if(!var8.hasNext()) {
							if(e2 > 0L) {
								pstmt.executeBatch();
								conn.commit();
								LOG.info("insert :" + e2 + " record successs");
							}

							return;
						}

						TridentTuple tuple = (TridentTuple)var8.next();
						paramList = this.mapper.getColumns(tuple);
					} while(paramList == null);

					for(int i = 0; i < paramList.size(); ++i) {
						pstmt.setObject(i + 1, paramList.get(i));
					}

					pstmt.addBatch();
					if(++e2 % (long)this.batchSize == 0L) {
						int[] var23 = pstmt.executeBatch();
					}
				}
			} catch (SQLException var21) {
				try {
					conn.commit();
				} catch (SQLException var20) {
					var20.printStackTrace();
				}

				;
				LOG.info("execute insert statement error： " + insertStatement, var21);
				LOG.info("the exception is:{}" ,  var21.getNextException());

			} finally {
				try {
					if(pstmt != null) {
						pstmt.close();
					}

					if(conn != null) {
						conn.close();
					}
				} catch (SQLException var19) {
					var19.printStackTrace();
				}

			}

		} else {
			LOG.info("current tuples is null：" + tuples);
		}
	}
}
