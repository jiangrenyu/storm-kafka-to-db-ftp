//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bonc.storm.trident.state;

import com.bonc.storm.jdbc.ConnectionProvider;
import com.bonc.storm.jdbc.LogFtpFileJdbcClient;
import com.bonc.storm.trident.mapper.JdbcMapper;
import java.util.List;

import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.state.State;
import org.apache.storm.trident.tuple.TridentTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LogFtpFileJdbcState implements State {
	private static final Logger LOG = LoggerFactory.getLogger(LogFtpFileJdbcState.class);
	private ConnectionProvider connectionProvider;
	private LogFtpFileJdbcClient jdbcClient;
	private int batchSize;
	private String insertStatement;
	private JdbcMapper mapper;

	public LogFtpFileJdbcState() {
	}

	public void prepare() {
		this.connectionProvider.prepare();
		this.mapper.prepare();
		this.jdbcClient = new LogFtpFileJdbcClient(this.connectionProvider, this.mapper, this.batchSize);
	}

	public LogFtpFileJdbcState withConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
		return this;
	}

	public LogFtpFileJdbcState withBatchSize(int batchSize) {
		this.batchSize = batchSize;
		return this;
	}

	public void beginCommit(Long txid) {
		LOG.debug("beginCommit is noop.");
	}

	public void commit(Long txid) {
		LOG.debug("commit is noop.");
	}

	public LogFtpFileJdbcState withJdbcMapper(JdbcMapper mapper) {
		this.mapper = mapper;
		return this;
	}

	public LogFtpFileJdbcState withInsertStatement(String insertStatement) {
		this.insertStatement = insertStatement;
		return this;
	}

	public void updateState(List<TridentTuple> tuples, TridentCollector collector) {
		this.jdbcClient.executeInsert(this.insertStatement, tuples);
	}
}
