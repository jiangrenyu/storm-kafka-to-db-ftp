//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bonc.storm.config;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

@XmlEnum
@XmlRootElement(
		name = "isUnicode"
)
public enum BooleanType {
	@XmlEnumValue("TRUE")
	TRUE,
	@XmlEnumValue("FALSE")
	FALSE;

	private BooleanType() {
	}
}
