package org.mokey.stormv.das.lpt.statistics;

import org.mokey.stormv.das.lpt.connectors.ConnectorType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StatisticsLogger {
	private static Map<ConnectorType, Statistics> stats = new ConcurrentHashMap<>();
	public static void put(ConnectorType type, long time, int size){
		if(!stats.containsKey(type))
			stats.put(type, new Statistics());
		stats.get(type).increment();
		stats.get(type).getAllTime().add(time);
		stats.get(type).getBinarySize().addAndGet(size);
	}
	public static Statistics getStatistics(ConnectorType type){
		if(!stats.containsKey(type))
			stats.put(type, new Statistics());
		return stats.get(type);
	}
	
	public static void reset(){
		for (ConnectorType key : stats.keySet()) {
			stats.put(key, new Statistics());
		}
	}
}
