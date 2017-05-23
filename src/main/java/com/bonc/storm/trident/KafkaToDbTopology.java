//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bonc.storm.trident;


import com.bonc.storm.bean.PropertyConfig;
import com.bonc.storm.config.FieldMapperConfiguration;
import com.bonc.storm.jdbc.C3P0ConnectionProvider;
import com.bonc.storm.trident.state.JdbcStateFactory;
import com.bonc.storm.trident.state.JdbcUpdater;
import com.bonc.storm.util.FileUtil;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.kafka.trident.OpaqueTridentKafkaSpout;
import org.apache.storm.kafka.trident.TridentKafkaConfig;
import org.apache.storm.trident.Stream;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.tuple.Fields;

import java.util.Properties;


public class KafkaToDbTopology {
	private static String topologyName = null;
	private static int spoutParallelism = 0;
	private static int mysqlBoltParallelism = 0;
	private static PropertyConfig config = null;
	private static Properties dbProperties = null;

	public KafkaToDbTopology() {
	}

	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException, AuthorizationException {
		String usage = "Usage:KafkaToDbTopology [-topologyName <topologyName>] -spoutParallelism <spoutParallelism> -mysqlBoltParallelism <mysqlBoltParallelism> -configPath <configPath> -dbPath <dbPath>";
		if(args.length < 8) {
			System.err.println("缺少参数");
			System.out.println(usage);
			System.exit(-1);
		}

		String consumerTopic;
		for(int zkHost = 0; zkHost < args.length; ++zkHost) {
			if("-topologyName".equals(args[zkHost])) {
				++zkHost;
				topologyName = args[zkHost];
			} else if("-spoutParallelism".equals(args[zkHost])) {
				try {
					++zkHost;
					spoutParallelism = Integer.parseInt(args[zkHost]);
				} catch (NumberFormatException var24) {
					System.err.println("spoutParallelism must be a number");
					System.exit(-1);
				}
			} else if("-mysqlBoltParallelism".equals(args[zkHost])) {
				try {
					++zkHost;
					mysqlBoltParallelism = Integer.parseInt(args[zkHost]);
				} catch (NumberFormatException var23) {
					System.err.println("mysqlBoltParallelism must be a number");
					System.exit(-1);
				}
			} else if("-configPath".equals(args[zkHost])) {
				++zkHost;
				consumerTopic = args[zkHost];

				try {
					config = new PropertyConfig(consumerTopic);
				} catch (Exception var22) {
					System.err.println("configPath can\'t found");
					System.exit(-1);
				}
			} else {
				if(!"-dbPath".equals(args[zkHost])) {
					throw new IllegalArgumentException("arg " + args[zkHost] + " not recognized");
				}

				++zkHost;
				consumerTopic = args[zkHost];

				try {
					dbProperties = FileUtil.getProperteis(consumerTopic);
					if(dbProperties == null) {
						System.err.println("create dataSource error ,please check dbPath:" + consumerTopic);
						System.exit(-1);
					}
				} catch (Exception var21) {
					System.err.println("create dataSource error ,please check dbPath:" + consumerTopic);
					System.exit(-1);
				}
			}
		}

		String var25 = config.getValue("zkHost");
		consumerTopic = config.getValue("consumerTopic");
		String spoutClientId = config.getValue("spoutClientId");
		String fieldMapperFile = config.getValue("fieldMapperFile");
		FieldMapperConfiguration configuration = null;

		try {
			configuration = FieldMapperConfiguration.getInstance(fieldMapperFile);
		} catch (Exception var20) {
			System.err.println("字段映射配置文件错误，请检查文件：" + fieldMapperFile);
			System.exit(-1);
		}

		int batchSize = 400;

		try {
			batchSize = Integer.parseInt(config.getValue("batchSize"));
		} catch (Exception var19) {
			var19.printStackTrace();
		}

		if(var25 == null || !checkHost(var25)) {
			System.err.println("参数配置错误，请检查配置文件中的 zkHost 参数");
			System.exit(-1);
		}

		if(consumerTopic == null) {
			System.err.println("参数配置错误，请检查配置文件中的 consumerTopic 参数");
			System.exit(-1);
		}

		ZkHosts zk = new ZkHosts(var25);
		TridentKafkaConfig spoutConf = null;
		if(spoutClientId == null || spoutClientId.startsWith("/")) {
			System.err.println("参数配置错误，请检查配置文件中的 spoutClientId 参数，spoutClientId不能为空且不能以 / 开头");
			System.exit(-1);
		}

		spoutConf = new TridentKafkaConfig(zk, consumerTopic, spoutClientId);
		String forceFromStart = config.getValue("forceFromStart");
		if(forceFromStart != null) {
			try {
				spoutConf.ignoreZkOffsets = Boolean.parseBoolean(forceFromStart);
			} catch (Exception var18) {
				var18.printStackTrace();
			}
		}
		String startOffsetTime = config.getValue("startOffsetTime");
		if ("smallest".equals(startOffsetTime)) {
			spoutConf.startOffsetTime = kafka.api.OffsetRequest.EarliestTime();
		} else if ("largest".equals(startOffsetTime)) {
			spoutConf.startOffsetTime = kafka.api.OffsetRequest.LatestTime();
		}

		OpaqueTridentKafkaSpout spout = new OpaqueTridentKafkaSpout(spoutConf);
		TridentTopology topology = new TridentTopology();
		Stream stream = topology.newStream(spoutClientId, spout).parallelismHint(spoutParallelism);
		C3P0ConnectionProvider connectionProvider = new C3P0ConnectionProvider(dbProperties);
		JdbcStateFactory stateFactory = (new JdbcStateFactory()).withConnectionProvider(connectionProvider).withFieldMapperConfig(configuration).withBatchSize(batchSize);
		stream.partitionPersist(stateFactory, spout.getOutputFields(), new JdbcUpdater(), new Fields(new String[0])).parallelismHint(mysqlBoltParallelism);
		Config conf = new Config();
		conf.setDebug(false);
		if(topologyName != null) {
			StormSubmitter.submitTopology(topologyName, conf, topology.build());
		} else {
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("test", conf, topology.build());
		}

	}

	public static boolean checkHost(String hostStr) {
		if(hostStr != null) {
			String[] hosts = hostStr.split(",");
			if(hosts != null && hosts.length > 0) {
				String[] var5 = hosts;
				int var4 = hosts.length;

				for(int var3 = 0; var3 < var4; ++var3) {
					String host = var5[var3];
					if(host.split(":") == null || host.split(":").length == 0) {
						return false;
					}
				}

				return true;
			}
		}

		return false;
	}
}
