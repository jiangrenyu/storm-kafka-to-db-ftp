//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bonc.storm.trident.state;

import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.state.BaseStateUpdater;
import org.apache.storm.trident.tuple.TridentTuple;

import java.util.List;


public class JdbcUpdater extends BaseStateUpdater<JdbcState> {
	public JdbcUpdater() {
	}

	public void updateState(JdbcState state, List<TridentTuple> tuples, TridentCollector collector) {
		state.updateState(tuples, collector);
	}
}
