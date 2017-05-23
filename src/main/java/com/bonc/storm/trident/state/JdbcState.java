//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bonc.storm.trident.state;

import com.bonc.storm.config.FieldMapperConfiguration;
import com.bonc.storm.jdbc.ConnectionProvider;
import com.bonc.storm.jdbc.JdbcClient_New;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.state.State;
import org.apache.storm.trident.tuple.TridentTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class JdbcState implements State {
	private static final Logger LOG = LoggerFactory.getLogger(JdbcState.class);
	private FieldMapperConfiguration fieldMapperConfiguration;
	private ConnectionProvider connectionProvider;
	private JdbcClient_New jdbcClient;
	private int batchSize;
	private String insertStatement;

	public JdbcState() {
	}

	public void prepare() {
		this.connectionProvider.prepare();
		this.fieldMapperConfiguration.prepare();
		this.jdbcClient = new JdbcClient_New(this.connectionProvider, this.fieldMapperConfiguration, this.batchSize);
		this.insertStatement = this.jdbcClient.getInsertStatement();
		if(this.insertStatement == null) {
			throw new RuntimeException("插入语句为空insertStatement 为空：" + this.insertStatement);
		} else {
			LOG.info("生成插入语句：" + this.insertStatement);
		}
	}

	public JdbcState withFieldMapperConfig(FieldMapperConfiguration fieldMapperConfiguration) {
		this.fieldMapperConfiguration = fieldMapperConfiguration;
		return this;
	}

	public JdbcState withConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
		return this;
	}

	public JdbcState withBatchSize(int batchSize) {
		this.batchSize = batchSize;
		return this;
	}

	public void beginCommit(Long txid) {
		LOG.debug("beginCommit is noop.");
	}

	public void commit(Long txid) {
		LOG.debug("commit is noop.");
	}

	public void updateState(List<TridentTuple> tuples, TridentCollector collector) {
		this.jdbcClient.executeInsert(this.insertStatement, tuples);
	}
}
