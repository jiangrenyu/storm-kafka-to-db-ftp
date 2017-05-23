//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bonc.storm.trident.state;

 import com.bonc.storm.config.FieldMapperConfiguration;
import com.bonc.storm.jdbc.ConnectionProvider;
 import org.apache.storm.task.IMetricsContext;
 import org.apache.storm.trident.state.State;
 import org.apache.storm.trident.state.StateFactory;


 import java.util.Map;

public class JdbcStateFactory implements StateFactory {
	private FieldMapperConfiguration fieldMapperConfiguration;
	private ConnectionProvider connectionProvider;
	private int batchSize;

	public JdbcStateFactory() {
	}

	public JdbcStateFactory withFieldMapperConfig(FieldMapperConfiguration configuration) {
		this.fieldMapperConfiguration = configuration;
		return this;
	}

	public JdbcStateFactory withConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
		return this;
	}

	public JdbcStateFactory withBatchSize(int batchSize) {
		this.batchSize = batchSize;
		return this;
	}

	public State makeState(Map conf, IMetricsContext metrics, int partitionIndex, int numPartitions) {
		JdbcState state = (new JdbcState()).withConnectionProvider(this.connectionProvider).withFieldMapperConfig(this.fieldMapperConfiguration).withBatchSize(this.batchSize);
		state.prepare();
		return state;
	}
}
