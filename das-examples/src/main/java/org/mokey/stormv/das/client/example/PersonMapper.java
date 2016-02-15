package org.mokey.stormv.das.client.example;

import java.sql.SQLException;
import java.sql.Timestamp;

import org.mokey.stormv.das.client.RecordMapper;
import org.mokey.stormv.das.models.DalModels.Record;
import org.mokey.stormv.das.models.codec.FieldConvert;

public class PersonMapper extends RecordMapper<Person>{

	@Override
	public Person map(Record record) throws SQLException {
		Person person = new Person();
		person.setID((Integer)FieldConvert.resolveField(record.getFields(0), meta.get(0).getType()));
		person.setAddress((String)FieldConvert.resolveField(record.getFields(1), meta.get(1).getType()));
		person.setTelephone((String)FieldConvert.resolveField(record.getFields(2), meta.get(2).getType()));
		person.setName((String)FieldConvert.resolveField(record.getFields(3), meta.get(3).getType()));
		person.setAge((Integer)FieldConvert.resolveField(record.getFields(4), meta.get(4).getType()));
		person.setGender((Integer)FieldConvert.resolveField(record.getFields(5), meta.get(5).getType()));
		person.setBirth((Timestamp)FieldConvert.resolveField(record.getFields(6), meta.get(6).getType()));
		person.setPartmentID((Integer)FieldConvert.resolveField(record.getFields(7), meta.get(7).getType()));
		person.setSpace((String)FieldConvert.resolveField(record.getFields(8), meta.get(8).getType()));
		return null;
	}
}
