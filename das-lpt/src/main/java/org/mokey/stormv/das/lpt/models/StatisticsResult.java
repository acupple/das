package org.mokey.stormv.das.lpt.models;

import java.util.ArrayList;
import java.util.List;

import org.mokey.stormv.das.lpt.connectors.ConnectorType;
import org.mokey.stormv.das.lpt.statistics.JsonData;
import org.mokey.stormv.das.lpt.statistics.Statistics;
import org.mokey.stormv.das.lpt.statistics.StatisticsLogger;

public class StatisticsResult {
	private List<JsonData> records;
	
	public StatisticsResult(){
		this.records = new ArrayList<JsonData>();
		this.records.add(this.getData(ConnectorType.JDBC));
		this.records.add(this.getData(ConnectorType.JDBCBIG));
		this.records.add(this.getData(ConnectorType.DAS));
		this.records.add(this.getData(ConnectorType.AMEOBA));
		this.records.add(this.getData(ConnectorType.DASWITHCOMPRESS));
	}
	
	private JsonData getData(ConnectorType type){
		Statistics stats = StatisticsLogger.getStatistics(type);
    	JsonData data = new JsonData();
    	int count = stats.getRequestCount();
    	data.setRequestCount(count);
    	data.setServiceHandleTime(stats.getServiceHandleTime().getAvg(count));
    	data.setNetworkTime(stats.getNetWorkTime().getAvg(count));
    	data.setResultDecodeTime(stats.getDecodeTime().getAvg(count));
    	data.setResultSize(stats.getRecordAvgCount());
    	data.setAllAllTime(stats.getAllTime().getAvg(count));
    	data.setCreateClient(stats.getCreateClient().getAvg(count));
		data.setDatabaseAccessType(type.getName());
		data.setBinarySize(stats.getBinarySize().get() / 1024);
		data.setBinaryAvgSize((count > 0 ? stats.getBinarySize().get() / count : 0)/ 1024);
    	return data;
	}

	public List<JsonData> getRecords() {
		return records;
	}

	public void setRecords(List<JsonData> records) {
		this.records = records;
	}
	
}
