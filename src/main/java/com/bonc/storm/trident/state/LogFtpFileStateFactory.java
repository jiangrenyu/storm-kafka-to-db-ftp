//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bonc.storm.trident.state;

 import com.bonc.storm.jdbc.ConnectionProvider;
import com.bonc.storm.trident.mapper.JdbcMapper;
import com.bonc.storm.trident.mapper.LogFtpFileMapper;
 import org.apache.storm.task.IMetricsContext;
 import org.apache.storm.trident.state.State;
 import org.apache.storm.trident.state.StateFactory;

 import java.util.Map;


public class LogFtpFileStateFactory implements StateFactory {
	private ConnectionProvider connectionProvider;
	private int batchSize;
	private String insertStatement;
	private JdbcMapper mapper;

	public LogFtpFileStateFactory() {
	}

	public LogFtpFileStateFactory withInsertStatement(String insertSql) {
		this.insertStatement = insertSql;
		return this;
	}

	public LogFtpFileStateFactory withConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
		return this;
	}

	public LogFtpFileStateFactory withBatchSize(int batchSize) {
		this.batchSize = batchSize;
		return this;
	}

	public LogFtpFileStateFactory withJdbcMapper(LogFtpFileMapper mapper) {
		this.mapper = mapper;
		return this;
	}

	public State makeState(Map conf, IMetricsContext metrics, int partitionIndex, int numPartitions) {
		LogFtpFileJdbcState state = (new LogFtpFileJdbcState()).withConnectionProvider(this.connectionProvider).withJdbcMapper(this.mapper).withInsertStatement(this.insertStatement).withBatchSize(this.batchSize);
		state.prepare();
		return state;
	}
}
