package org.mokey.stormv.das.lpt.connectors;

import java.util.ArrayList;
import java.util.List;

import org.mokey.stormv.das.lpt.models.Model;
import org.mokey.stormv.das.lpt.statistics.StatisticsLogger;

public abstract class Connector implements IConnector{
	
	protected List<Model> models = new ArrayList<Model>();
	
	@Override
	public String connect() throws Exception {
		long start = System.currentTimeMillis();
		ConnectorType type = this.process();

		long usedTime = System.currentTimeMillis() - start;
		StatisticsLogger.put(type, usedTime, objectSize(models));
		return String.format("{'Records':%s, 'usedTime':%s}", this.models.size(), usedTime);
	}
	
	public abstract ConnectorType process() throws Exception;

	private static int objectSize(List<Model> mods) {
		if(mods == null || mods.size() <= 0)
			return 0;
		int size = 0;
		for (Model model : mods){
			size += model.size();
		}
		return size;
	}
}
