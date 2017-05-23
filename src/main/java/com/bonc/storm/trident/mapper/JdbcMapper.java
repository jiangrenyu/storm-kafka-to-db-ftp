//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bonc.storm.trident.mapper;


import org.apache.storm.tuple.ITuple;

import java.io.Serializable;
import java.util.List;

public interface JdbcMapper extends Serializable {
	List<Object> getColumns(ITuple var1);

	void prepare();
}
