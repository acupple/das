package org.mokey.stormv.das.lpt.models;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.mokey.stormv.das.client.RecordMapper;
import org.mokey.stormv.das.models.DalModels.ColumnMata;
import org.mokey.stormv.das.models.DalModels.Record;
import org.mokey.stormv.das.models.codec.FieldConvert;
import org.mokey.stormv.das.lpt.connectors.ConnectorType;
import org.mokey.stormv.das.lpt.statistics.StatisticsLogger;

public final class AutoRecordMapper<T> extends RecordMapper<T> {

	private Class<?> clazz;
	private Map<String, Method> setters;
	private Constructor<?> constructor;

	private AutoRecordMapper(Class<?> clazz) throws Exception{
		this.clazz = clazz;
		this.setters = new HashMap<>();
		this.constructor = this.clazz.getConstructor();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static AutoRecordMapper create(Class<?> clazz) throws Exception {
		return new AutoRecordMapper(clazz);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T map(Record record) throws Exception {
		StatisticsLogger.getStatistics(ConnectorType.DAS).getDecodeTime().start();
		if(this.setters.isEmpty()){
			ColumnMata column = null;
			Field field = null;
			for (int i = 0; i < this.meta.size(); i++) {
				column = this.meta.get(i);
				field = this.clazz.getDeclaredField(this.uncapitalize(column.getName()));
				Method setter = this.clazz.getMethod("set" + this.capitalize(column.getName()), field.getType());
				this.setters.put(column.getName(), setter);
			}
		}
		Object obj = this.constructor.newInstance();
		ColumnMata column = null;
		for (int i = 0; i < this.meta.size(); i++) {
			column = this.meta.get(i);
			Object val = FieldConvert.resolveField(record.getFields(i),
					column.getType());
			if (val != null)
				this.setters.get(column.getName()).invoke(obj, val);
		}
		StatisticsLogger.getStatistics(ConnectorType.DAS).getDecodeTime().end();
		return (T) obj;
	}

	public String capitalize(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		}
		return new StringBuffer(strLen)
				.append(Character.toTitleCase(str.charAt(0)))
				.append(str.substring(1)).toString();
	}

	public String uncapitalize(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		}
		return new StringBuffer(strLen)
				.append(Character.toLowerCase(str.charAt(0)))
				.append(str.substring(1)).toString();
	}
}
