//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bonc.storm.jdbc;

import com.bonc.storm.config.Field;
import com.bonc.storm.config.FieldMapperConfiguration;
import com.bonc.storm.config.FieldType;
import com.bonc.storm.config.MessageType;
import com.bonc.storm.util.DateUtil;
import com.bonc.storm.util.JsonUtil;
import net.sf.json.JSONObject;
import org.apache.storm.trident.tuple.TridentTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class JdbcClient_New {
	private static final Logger LOG = LoggerFactory.getLogger(JdbcClient_New.class);
	private ConnectionProvider connectionProvider;
	private FieldMapperConfiguration fieldMapperConfiguration;
	private int batchSize;

	public JdbcClient_New(ConnectionProvider connectionProvider, FieldMapperConfiguration fieldMapperConfiguration, int batchSize) {
		this.connectionProvider = connectionProvider;
		this.fieldMapperConfiguration = fieldMapperConfiguration;
		this.batchSize = batchSize;
	}

	public String getInsertStatement() {
		String tableName = this.fieldMapperConfiguration.getTableName();
		List fieldList = this.fieldMapperConfiguration.getFieldList();
		if(tableName != null && fieldList.size() > 0) {
			StringBuffer namePart = new StringBuffer();
			namePart.append("insert into ");
			namePart.append(tableName);
			namePart.append("(");
			StringBuffer valuePart = new StringBuffer();
			Iterator var6 = fieldList.iterator();

			while(var6.hasNext()) {
				Field field = (Field)var6.next();
				namePart.append(field.getFieldName());
				namePart.append(",");
				valuePart.append("?");
				valuePart.append(",");
			}

			namePart.deleteCharAt(namePart.length() - 1);
			valuePart.deleteCharAt(valuePart.length() - 1);
			namePart.append(") values (");
			namePart.append(valuePart);
			namePart.append(")");
			return namePart.toString();
		} else {
			return null;
		}
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
								LOG.info("成功插入:" + e2 + " 条");
							}

							return;
						}

						TridentTuple tuple = (TridentTuple)var8.next();
						paramList = this.getColumns(tuple);
						LOG.info("the parsed param is：" + paramList);
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

				LOG.info("execute insert error： " + insertStatement, var21);
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

	private List<Object> getColumns(TridentTuple tuple) {
		String message = null;

		try {
			message = new String((byte[])tuple.get(0), "UTF-8");
		} catch (UnsupportedEncodingException var8) {
			LOG.info("transfer message " + tuple + " to String exception,", var8);
			return null;
		}

		ArrayList resultList;
		if(MessageType.JSON.equals(this.fieldMapperConfiguration.getHandleType())) {
			JSONObject var9 = JsonUtil.parseStrToJson(message);
			List var10 = this.fieldMapperConfiguration.getFieldList();
			if(var9 == null) {
				LOG.info("transfer message: " + message + " to JSONObject exception");
				return null;
			} else {
				var9.accumulate("insert_time", DateUtil.formatDate(new Date(), "yyyyMMdd HH:mm:ss:S"));
				resultList = new ArrayList();
				Iterator var12 = var10.iterator();

				while(var12.hasNext()) {
					Field var11 = (Field)var12.next();
					resultList.add(this.getValue(var11, var9));
				}

				return resultList;
			}
		} else if(!MessageType.SPLIT.equals(this.fieldMapperConfiguration.getHandleType())) {
			return null;
		} else {
			List fieldList = this.fieldMapperConfiguration.getFieldList();
			String[] msgArray = message.split(this.fieldMapperConfiguration.getSplitChar());
			resultList = new ArrayList();
			int fieldLength = fieldList.size() - 1;
			int j;
			if(fieldLength > msgArray.length) {
				for(j = 0; j < msgArray.length; ++j) {
					resultList.add(this.transferField((Field)fieldList.get(j), msgArray[j]));
				}

				while(j < fieldLength) {
					resultList.add(this.transferField((Field)fieldList.get(j), (String)null));
					++j;
				}
			} else {
				for(j = 0; j < fieldLength; ++j) {
					resultList.add(this.transferField((Field)fieldList.get(j), msgArray[j]));
				}
			}

			resultList.add(this.transferField((Field)fieldList.get(fieldList.size() - 1), DateUtil.formatDate(new Date(), "yyyyMMdd HH:mm:ss:S")));
			return resultList;
		}
	}

	private List<Object> getColumns_Old(TridentTuple tuple) {
		String message = null;

		try {
			message = new String((byte[])tuple.get(0), "UTF-8");
		} catch (UnsupportedEncodingException var9) {
			LOG.info("transfer message " + tuple + " to String exception,", var9);
			return null;
		}

		List fieldList;
		if(MessageType.JSON.equals(this.fieldMapperConfiguration.getHandleType())) {
			JSONObject var10 = JsonUtil.parseStrToJson(message);
			fieldList = this.fieldMapperConfiguration.getFieldList();
			if(var10 == null) {
				LOG.info("transfer message: " + message + " to JSONObject exception");
				return null;
			} else {
				var10.accumulate("insert_time", DateUtil.formatDate(new Date(), "yyyyMMdd HH:mm:ss:S"));
				ArrayList var11 = new ArrayList();
				Iterator var13 = fieldList.iterator();

				while(var13.hasNext()) {
					Field var12 = (Field)var13.next();
					var11.add(this.getValue(var12, var10));
				}

				return var11;
			}
		} else if(MessageType.SPLIT.equals(this.fieldMapperConfiguration.getHandleType())) {
			String spliceChar = this.fieldMapperConfiguration.getSplitChar();
			if(this.fieldMapperConfiguration.isUnicodeRegSplit()) {
				spliceChar = this.fieldMapperConfiguration.getRegSplit();
			}

			message = message + spliceChar + DateUtil.formatDate(new Date(), "yyyyMMdd HH:mm:ss:S");
			fieldList = this.fieldMapperConfiguration.getFieldList();
			String[] msgArray = message.split(this.fieldMapperConfiguration.getSplitChar());
			ArrayList resultList = new ArrayList();
			int fieldLength = fieldList.size();
			int j;
			if(fieldLength > msgArray.length) {
				for(j = 0; j < msgArray.length; ++j) {
					resultList.add(this.transferField((Field)fieldList.get(j), msgArray[j]));
				}

				while(j < fieldLength) {
					resultList.add((Object)null);
					++j;
				}
			} else {
				for(j = 0; j < fieldLength; ++j) {
					resultList.add(this.transferField((Field)fieldList.get(j), msgArray[j]));
				}
			}

			return resultList;
		} else {
			return null;
		}
	}

	private Object getValue(Field field, JSONObject jsonObj) {
		String value = null;

		try {
			value = jsonObj.getString(field.getName());
		} catch (Exception var5) {
			LOG.info("the field " + field.getName() + " not exist,it will use default value according it\'s type " + field.getType());
		}

		return this.transferField(field, value);
	}

	private Object transferField(Field field, String value) {
		FieldType fieldType = field.getType();
		if(FieldType.STRING.equals(fieldType)) {
			return value == null?null:value.trim();
		} else if(FieldType.TIMESTAMP.equals(fieldType)) {
			try {
				return new Timestamp(DateUtil.parseDate(value, field.getDateFormat()).getTime());
			} catch (Exception var5) {
				return null;
			}
		} else if(FieldType.DOUBLE.equals(fieldType)) {
			try {
				return Double.valueOf(Double.parseDouble(value));
			} catch (Exception var6) {
				return Integer.valueOf(0);
			}
		} else if(FieldType.INTEGER.equals(fieldType)) {
			try {
				return Integer.valueOf(Integer.parseInt(value));
			} catch (Exception var7) {
				return Integer.valueOf(0);
			}
		} else if(FieldType.DATE.equals(fieldType)) {
			try {
				return DateUtil.parseDate(value, field.getDateFormat());
			} catch (Exception var8) {
				return null;
			}
		} else if(FieldType.LONG.equals(fieldType)) {
			try {
				return Long.valueOf(Long.parseLong(value));
			} catch (Exception var9) {
				return Integer.valueOf(0);
			}
		} else if(FieldType.FLOAT.equals(fieldType)) {
			try {
				return Float.valueOf(Float.parseFloat(value));
			} catch (Exception var10) {
				return Integer.valueOf(0);
			}
		} else if(FieldType.BOOLEAN.equals(fieldType)) {
			try {
				return Boolean.valueOf(Boolean.parseBoolean(value));
			} catch (Exception var11) {
				return Boolean.valueOf(false);
			}
		} else {
			LOG.info("dont\'t support type：" + field.getType());
			return null;
		}
	}
}
