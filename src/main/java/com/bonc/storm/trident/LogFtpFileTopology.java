//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bonc.storm.trident;

import com.bonc.storm.bean.PropertyConfig;
import com.bonc.storm.jdbc.C3P0ConnectionProvider;
import com.bonc.storm.jdbc.DBManager;
import com.bonc.storm.service.ProvService;
import com.bonc.storm.service.TableService;
import com.bonc.storm.trident.mapper.LogFtpFileMapper;
import com.bonc.storm.trident.scheme.LogFtpFileScheme;
import com.bonc.storm.trident.state.LogFtpFileJdbcUpdater;
import com.bonc.storm.trident.state.LogFtpFileStateFactory;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.kafka.trident.OpaqueTridentKafkaSpout;
import org.apache.storm.kafka.trident.TridentKafkaConfig;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.trident.Stream;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.tuple.Fields;

import java.util.Calendar;
import java.util.Map;


public class LogFtpFileTopology {
	private static String topologyName = null;
	private static int spoutParallelism = 0;
	private static int mysqlBoltParallelism = 0;
	private static PropertyConfig config = null;
	private static DBManager dbManager = null;
	private static String dbPath = null;

	public LogFtpFileTopology() {
	}

	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException, AuthorizationException {
		String usage = "Usage:LogFtpFileTopology [-topologyName <topologyName>] -spoutParallelism <spoutParallelism> -mysqlBoltParallelism <mysqlBoltParallelism>  -configPath <configPath> -dbPath <dbPath>";
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
				} catch (NumberFormatException var40) {
					System.err.println("spoutParallelism must be a number");
					System.exit(-1);
				}
			} else if("-mysqlBoltParallelism".equals(args[zkHost])) {
				try {
					++zkHost;
					mysqlBoltParallelism = Integer.parseInt(args[zkHost]);
				} catch (NumberFormatException var39) {
					System.err.println("mysqlBoltParallelism must be a number");
					System.exit(-1);
				}
			} else if("-configPath".equals(args[zkHost])) {
				++zkHost;
				consumerTopic = args[zkHost];

				try {
					config = new PropertyConfig(consumerTopic);
				} catch (Exception var38) {
					System.err.println("configPath can\'t found:" + consumerTopic);
					System.exit(-1);
				}
			} else {
				if(!"-dbPath".equals(args[zkHost])) {
					throw new IllegalArgumentException("arg " + args[zkHost] + " not recognized");
				}

				++zkHost;
				dbPath = args[zkHost];

				try {
					dbManager = new DBManager(dbPath);
				} catch (Exception var37) {
					System.err.println("create dataSource error ,please check dbPath:" + dbPath);
					System.exit(-1);
				}
			}
		}

		String var41 = config.getValue("zkHost");
		consumerTopic = config.getValue("consumerTopic");
		String spoutClientId = config.getValue("spoutClientId");
		String insertTableName = config.getValue("insertTableName");
		int batchSize = 400;

		try {
			batchSize = Integer.parseInt(config.getValue("batchSize"));
		} catch (Exception var36) {
			var36.printStackTrace();
		}

		if(var41 == null || !checkHost(var41)) {
			System.err.println("参数配置错误，请检查配置文件中的 zkHost 参数");
			System.exit(-1);
		}

		if(consumerTopic == null) {
			System.err.println("参数配置错误，请检查配置文件中的 consumerTopic 参数");
			System.exit(-1);
		}

		if(insertTableName == null || insertTableName.trim().length() == 0) {
			System.err.println("参数配置错误，请检查配置文件中的 insertTableName 参数");
			System.exit(-1);
		}

		ProvService provService = new ProvService(dbManager);
		Map provMap = provService.getProvInfo();
		if(provMap == null || provMap.size() == 0) {
			System.err.println("从数据库省份编码表d_prov中加载的内容为空");
			System.exit(-1);
		}

		Map fileNameRegularMap = provService.getFileNameRegular();
		if(fileNameRegularMap == null || fileNameRegularMap.size() == 0) {
			System.err.println("从数据库表conf_tb_info 中加载的内容为空");
			System.exit(-1);
		}

		TableService tableService = new TableService(dbManager);
		Map tableMap = tableService.getTableAttr();
		if(tableMap == null || tableMap.size() == 0) {
			System.err.println("从数据库表conf_path_info_web 中加载的有效记录为空");
			System.exit(-1);
		}

		String defaultTableAttr = "-1";

		try {
			defaultTableAttr = config.getValue("defaultTableAttr");
		} catch (Exception var35) {
			var35.printStackTrace();
		}

		String timing = config.getValue("timing");
		Calendar timeCalendar = Calendar.getInstance();
		if(timing != null && timing.split(":").length == 3) {
			String[] period = timing.split(":");
			int zk = 0;
			int spoutConf = 0;
			int forceFromStart = 0;

			try {
				zk = Integer.parseInt(period[0]);
				spoutConf = Integer.parseInt(period[1]);
				forceFromStart = Integer.parseInt(period[2]);
			} catch (NumberFormatException var34) {
				System.out.println("参数配置错误，请检查配置文件中的timing参数，格式为HH:mm:ss");
				System.exit(-1);
			}

			if(zk < 0 || zk > 23) {
				System.out.println("参数配置错误，请检查配置文件中的timing参数，格式为HH:mm:ss,HH 取值范围：0~23");
				System.exit(-1);
			}

			if(spoutConf < 0 || zk > 59) {
				System.out.println("参数配置错误，请检查配置文件中的timing参数，格式为HH:mm:ss,mm 取值范围：0~59");
				System.exit(-1);
			}

			if(forceFromStart < 0 || forceFromStart > 59) {
				System.out.println("参数配置错误，请检查配置文件中的timing参数，格式为HH:mm:ss,ss 取值范围：0~59");
				System.exit(-1);
			}

			timeCalendar.set(11, zk);
			timeCalendar.set(12, spoutConf);
			timeCalendar.set(13, forceFromStart);
		} else {
			System.err.println("参数配置错误，请检查配置文件中的timing参数，格式为HH:mm:ss");
			System.exit(-1);
		}

		int var42 = 0;

		try {
			var42 = Integer.parseInt(config.getValue("period"));
			if(var42 < 0) {
				System.err.println("参数配置错误，请检查配置文件中的定时间隔天数period参数，单位为天数");
				System.exit(-1);
			}
		} catch (NumberFormatException var33) {
			System.err.println("参数配置错误，请检查配置文件中的定时间隔天数period参数，单位为天数");
			System.exit(-1);
		}

		ZkHosts var43 = new ZkHosts(var41);
		TridentKafkaConfig var44 = null;
		if(spoutClientId == null || spoutClientId.startsWith("/")) {
			System.err.println("参数配置错误，请检查配置文件中的 spoutClientId 参数，spoutClientId不能为空且不能以 / 开头");
			System.exit(-1);
		}

		var44 = new TridentKafkaConfig(var43, consumerTopic, spoutClientId);
		var44.scheme = new SchemeAsMultiScheme(new LogFtpFileScheme());
		String var45 = config.getValue("forceFromStart");
		if(var45 != null) {
			try {
				var44.ignoreZkOffsets = Boolean.parseBoolean(var45);
			} catch (Exception var32) {
				var32.printStackTrace();
			}
		}
		String startOffsetTime = config.getValue("startOffsetTime");
		if ("smallest".equals(startOffsetTime)) {
			var44.startOffsetTime = kafka.api.OffsetRequest.EarliestTime();
		} else if ("largest".equals(startOffsetTime)) {
			var44.startOffsetTime = kafka.api.OffsetRequest.LatestTime();
		}
		int provNamePos = 2;

		try {
			provNamePos = Integer.parseInt(config.getValue("provNamePosition"));
		} catch (Exception var31) {
			var31.printStackTrace();
		}

		int defaultProvId = -1;

		try {
			defaultProvId = Integer.parseInt(config.getValue("defaultProvId"));
		} catch (Exception var30) {
			var30.printStackTrace();
		}

		OpaqueTridentKafkaSpout spout = new OpaqueTridentKafkaSpout(var44);
		TridentTopology topology = new TridentTopology();
		Stream stream = topology.newStream(spoutClientId, spout).parallelismHint(spoutParallelism).shuffle();
		C3P0ConnectionProvider connectionProvider = new C3P0ConnectionProvider(dbManager.getProperties());
		String insertSql = "insert into  " + insertTableName + "(file_name ,operate_type,client_address,server_address,ftp_account,file_path,version,file_size,begin_time,end_time,prov_id,table_attr,insert_time) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		LogFtpFileMapper mapper = new LogFtpFileMapper(provMap, provNamePos, fileNameRegularMap, defaultProvId, tableMap, defaultTableAttr);
		mapper.setTime(timeCalendar);
		mapper.setPeriod(var42);
		mapper.setDbProperties(dbManager.getProperties());
		LogFtpFileStateFactory stateFactory = (new LogFtpFileStateFactory()).withConnectionProvider(connectionProvider).withInsertStatement(insertSql).withJdbcMapper(mapper).withBatchSize(batchSize);
		stream.partitionPersist(stateFactory, spout.getOutputFields(), new LogFtpFileJdbcUpdater(), new Fields(new String[0])).parallelismHint(mysqlBoltParallelism);
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
