package org.mokey.stormv.das.worker.wraps;

import org.mokey.stormv.das.models.DalModels.Direction;
import org.mokey.stormv.das.models.DalModels.SqlParameter;
import org.mokey.stormv.das.models.codec.FieldConvert;

import java.sql.SQLException;

/**
 * Some value convert here
 * @author wcyuan
 */
public class DalSqlParameterWrapper {
	
	private SqlParameter parameter;
	
	public DalSqlParameterWrapper(){
		this.parameter = SqlParameter.newBuilder().build();
	}
	
	public DalSqlParameterWrapper(SqlParameter param){
		this.parameter = param;
	}
	
	public SqlParameter getSqlParameter(){
		return this.parameter;
	}
	
	public boolean isInputParameter(){
		return this.parameter.getDirection() == Direction.Input || 
				this.parameter.getDirection() == Direction.InputOutput;
	}
	
	public boolean isOutParameter(){
		return this.parameter.getDirection() == Direction.Output ||
				this.parameter.getDirection() == Direction.InputOutput;
	}

	public boolean isResultParameter(){
		return this.parameter.getDirection() == Direction.ReturnValue;
	}
	
	public String getName(){
		return this.parameter.getName();
	}
	
	public int getIndex(){
		return this.parameter.getIndex();
	}
	
	public int getSqlType(){
		return this.parameter.getDbType();
	}
	
	public Object getValue() throws SQLException{
		return FieldConvert.resolveField(this.parameter.getValue(), this.parameter.getDbType());
	}
}
