//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bonc.storm.config;

import com.bonc.storm.util.PropertityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(
		name = "fieldMapperConfiguration"
)
public class FieldMapperConfiguration implements Serializable {
	private static final Logger LOG = LoggerFactory.getLogger(FieldMapperConfiguration.class);
	private MessageType handleType;
	private String splitChar;
	private String tableName;
	private BooleanType isUnicode;
	private boolean isUnicodeRegSplit = false;
	private String regSplit;
	private List<Field> fieldList = new ArrayList(0);

	public static FieldMapperConfiguration getInstance(String fileName) throws Exception {
		BufferedReader reader = null;
		if(fileName != null) {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName)), "UTF-8"));
			JAXBContext context = JAXBContext.newInstance(new Class[]{FieldMapperConfiguration.class, MessageType.class, Field.class, FieldType.class});
			Unmarshaller unmarshaller = context.createUnmarshaller();
			FieldMapperConfiguration fieldMapperConfiguration = (FieldMapperConfiguration)unmarshaller.unmarshal(reader);
			return fieldMapperConfiguration;
		} else {
			throw new Exception("can\'t found file:" + fileName);
		}
	}

	private FieldMapperConfiguration() {
	}

	@XmlAttribute(
			name = "handleType",
			required = true
	)
	public MessageType getHandleType() {
		return this.handleType;
	}

	public void setHandleType(MessageType handleType) {
		this.handleType = handleType;
	}

	@XmlAttribute(
			name = "splitChar",
			required = false
	)
	public String getSplitChar() {
		return this.splitChar;
	}

	public void setSplitChar(String splitChar) {
		this.splitChar = splitChar;
	}

	@XmlElement(
			name = "field",
			nillable = false
	)
	public List<Field> getFieldList() {
		return this.fieldList;
	}

	public void setFieldList(List<Field> fieldList) {
		this.fieldList = fieldList;
	}

	@XmlAttribute(
			name = "tableName",
			required = true
	)
	public String getTableName() {
		return this.tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@XmlAttribute(
			name = "isUnicode",
			required = false
	)
	public BooleanType getIsUnicode() {
		return this.isUnicode;
	}

	public void setIsUnicode(BooleanType isUnicode) {
		this.isUnicode = isUnicode;
	}

	public boolean isUnicodeRegSplit() {
		return this.isUnicodeRegSplit;
	}

	public void setUnicodeRegSplit(boolean isUnicodeRegSplit) {
		this.isUnicodeRegSplit = isUnicodeRegSplit;
	}

	public String getRegSplit() {
		return this.regSplit;
	}

	public void setRegSplit(String regSplit) {
		this.regSplit = regSplit;
	}

	public void prepare() {
		if(MessageType.SPLIT.equals(this.getHandleType()) && (this.getSplitChar() == null || this.getSplitChar().length() == 0)) {
			LOG.info("当消息为字符分隔时，分隔符不能为空：" + this.getSplitChar());
			throw new RuntimeException("当消息为字符分隔时，分隔符不能为空：" + this.getSplitChar());
		} else {
			if(MessageType.SPLIT.equals(this.getHandleType()) && BooleanType.TRUE.equals(this.getIsUnicode())) {
				String unicodeSplitChar = PropertityUtil.readUnicodeStr(this.getSplitChar());
				if(".$|^?*+".indexOf(unicodeSplitChar.charAt(0)) != -1) {
					this.setSplitChar("\\" + unicodeSplitChar);
					this.isUnicodeRegSplit = true;
					this.regSplit = unicodeSplitChar;
				} else {
					this.setSplitChar(unicodeSplitChar);
				}
			}

		}
	}
}
