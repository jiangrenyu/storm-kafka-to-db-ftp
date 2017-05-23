//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bonc.storm.trident.mapper;

import com.bonc.storm.bean.Message;
import com.bonc.storm.jdbc.DBManager;
import com.bonc.storm.service.ProvService;
import com.bonc.storm.service.TableService;
import com.bonc.storm.util.DateUtil;
import com.bonc.storm.util.RegUtil;
import org.apache.storm.tuple.ITuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.*;

public class LogFtpFileMapper implements JdbcMapper {
	private static final Logger LOG = LoggerFactory.getLogger(LogFtpFileMapper.class);
	private Map<String, Integer> provMap;
	private int provNamePos;
	private int defaultProvId;
	private Map<String, String> tableMap;
	private String defaultTableAttr;
	private Map<String, List<Map<String, Object>>> fileNameRegularMap;
	private Calendar timeCalendar;
	private int period;
	private Properties dbProperties = null;
	private DBManager dbManager = null;
	private ProvService provService = null;
	private TableService tableService = null;

	public LogFtpFileMapper(Map<String, Integer> provMap, int provNamePos, Map<String, List<Map<String, Object>>> fileNameRegularMap, int defaultProvId, Map<String, String> tableMap, String defaultTableAttr) {
		this.provMap = provMap;
		this.provNamePos = provNamePos;
		this.defaultProvId = defaultProvId;
		this.fileNameRegularMap = fileNameRegularMap;
		this.tableMap = tableMap;
		this.defaultTableAttr = defaultTableAttr;
	}

	public List<Object> getColumns(ITuple tuple) {
		int provId = this.defaultProvId;
		ArrayList columns = new ArrayList();

		try {
			Message e = (Message)tuple.getValue(0);
			Integer newProvId = this.getFixAndMblProvId(e.getFilePath(), e.getFileName());
			if(newProvId == null) {
				String[] tableAttr = e.getFilePath().split("/", -1);
				if(this.provNamePos >= 2 && this.provNamePos <= tableAttr.length) {
					Integer id = (Integer)this.provMap.get(tableAttr[tableAttr.length - this.provNamePos]);
					if(id != null) {
						provId = id.intValue();
					}
				}
			} else {
				provId = newProvId.intValue();
			}

			String tableAttr1 = this.getTableAttr(e.getFilePath());
			columns.add(e.getFileName());
			columns.add(Integer.valueOf(e.getOperateType()));
			columns.add(e.getClientAddress());
			columns.add(e.getServerAddress());
			columns.add(e.getFtpAccount());
			columns.add(e.getFilePath());
			columns.add(e.getVersion());
			columns.add(Double.valueOf(e.getFileSize()));
			columns.add(new Timestamp(DateUtil.parseDate(e.getBeginTime(), "yyyy-MM-dd HH:mm:ss").getTime()));
			columns.add(new Timestamp(DateUtil.parseDate(e.getEndTime(), "yyyy-MM-dd HH:mm:ss").getTime()));
			columns.add(Integer.valueOf(provId));
			columns.add(tableAttr1);
			columns.add(new Timestamp((new Date()).getTime()));
			return columns;
		} catch (Exception var8) {
			LOG.info("mapper message to db field error：" + var8);
			return null;
		}
	}

	private Integer getFixAndMblProvId(String filePath, String fileName) {
		Map var3 = this.fileNameRegularMap;
		synchronized(this.fileNameRegularMap) {
			if(filePath != null && fileName != null) {
				String subPath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
				List ruleList = (List)this.fileNameRegularMap.get(subPath);
				if(ruleList != null) {
					Iterator var7 = ruleList.iterator();

					while(var7.hasNext()) {
						Map rule = (Map)var7.next();
						if(RegUtil.find(fileName, (String)rule.get("fname_regular")) != null) {
							return (Integer)rule.get("prov_id");
						}
					}

					return Integer.valueOf(this.defaultProvId);
				}
			}

			return null;
		}
	}

	private String getTableAttr(String filePath) {
		Map var2 = this.tableMap;
		synchronized(this.tableMap) {
			Iterator var4 = this.tableMap.keySet().iterator();

			while(var4.hasNext()) {
				String key = (String)var4.next();
				if(filePath.startsWith(key)) {
					return (String)this.tableMap.get(key);
				}
			}

			return this.defaultTableAttr;
		}
	}

	public void setTime(Calendar timeCalendar) {
		this.timeCalendar = timeCalendar;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public void setDbProperties(Properties dbProperties) {
		this.dbProperties = dbProperties;
	}

	public void prepare() {
		try {
			this.dbManager = new DBManager(this.dbProperties);
			this.provService = new ProvService(this.dbManager);
			this.tableService = new TableService(this.dbManager);
			if(this.timeCalendar.getTimeInMillis() < (new Date()).getTime()) {
				this.timeCalendar.add(5, 1);
			}

			Date e = this.timeCalendar.getTime();
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new LogFtpFileMapper.RuleTimeTask(), e, (long)(this.period * 24 * 60 * 60 * 1000));
		} catch (Exception var3) {
			throw new RuntimeException(var3);
		}
	}

	private class RuleTimeTask extends TimerTask {
		private RuleTimeTask() {
		}

		public void run() {
			Map tableMap;
			synchronized(LogFtpFileMapper.this.fileNameRegularMap) {
				LogFtpFileMapper.LOG.info("start reload conf_tb_info rule");
				tableMap = LogFtpFileMapper.this.provService.getFileNameRegular();
				if(tableMap != null && tableMap.size() > 0) {
					LogFtpFileMapper.this.fileNameRegularMap = tableMap;
					LogFtpFileMapper.LOG.info("reload conf_tb_info {} rule,detail is :{}", Integer.valueOf(tableMap.size()), tableMap);
				} else {
					LogFtpFileMapper.LOG.info("reload conf_tb_info {} rule, dont\'t change", Integer.valueOf(0));
				}
			}

			synchronized(LogFtpFileMapper.this.tableMap) {
				LogFtpFileMapper.LOG.info("start reload conf_path_info_web rule");
				tableMap = LogFtpFileMapper.this.tableService.getTableAttr();
				if(tableMap != null && tableMap.size() > 0) {
					LogFtpFileMapper.this.tableMap = tableMap;
					LogFtpFileMapper.LOG.info("reload conf_path_info_web {} rule,detail is :{}", Integer.valueOf(tableMap.size()), tableMap);
				} else {
					LogFtpFileMapper.LOG.info("reload conf_path_info_web {} rule, dont\'t change", Integer.valueOf(0));
				}

			}
		}
	}
}
