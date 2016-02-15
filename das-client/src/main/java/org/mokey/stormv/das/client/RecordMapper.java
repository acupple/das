package org.mokey.stormv.das.client;

import java.util.List;

import org.mokey.stormv.das.models.DalModels.ColumnMata;
import org.mokey.stormv.das.models.DalModels.Record;

public abstract class RecordMapper<T> {
	protected List<ColumnMata> meta = null;
	public void setColumnMetaData(List<ColumnMata> meta){
		this.meta = meta;
	}
	public abstract T map(Record record) throws Exception;
}
