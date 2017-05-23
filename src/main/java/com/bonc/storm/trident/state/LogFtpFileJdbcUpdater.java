//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bonc.storm.trident.state;

import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.state.BaseStateUpdater;
import org.apache.storm.trident.tuple.TridentTuple;

import java.util.List;


public class LogFtpFileJdbcUpdater extends BaseStateUpdater<LogFtpFileJdbcState> {
	public LogFtpFileJdbcUpdater() {
	}

	public void updateState(LogFtpFileJdbcState state, List<TridentTuple> tuples, TridentCollector collector) {
		state.updateState(tuples, collector);
	}
}
