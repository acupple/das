package org.mokey.stormv.das.worker.wraps;

import java.util.LinkedList;
import java.util.List;

import org.mokey.stormv.das.models.DalModels.SqlParameter;
import org.mokey.stormv.das.models.DalModels.SqlParameters;

public class DalSqlParametersWrapper {
	
	public List<DalSqlParameterWrapper> parameters;
	private List<DalSqlParameterWrapper> resultParameters;
	private List<DalSqlParameterWrapper> callParameters;

	public DalSqlParametersWrapper(SqlParameters parameters){
		this.parameters = new LinkedList<DalSqlParameterWrapper>();
		this.resultParameters = new LinkedList<DalSqlParameterWrapper>();
		this.callParameters = new LinkedList<DalSqlParameterWrapper>();
		if(parameters != null)
			for (SqlParameter sqlParameter : parameters.getParametersList()) {
				DalSqlParameterWrapper wraper = new DalSqlParameterWrapper(sqlParameter);

				if(wraper.isResultParameter()){
					resultParameters.add(wraper);
				}else if(wraper.isOutParameter()){
					callParameters.add(wraper);
				}

				this.parameters.add(wraper);
			}
	}
	
	public List<DalSqlParameterWrapper> getParameters(){
		return this.parameters;
	}

	public List<DalSqlParameterWrapper> getResultParameters(){
		return this.resultParameters;
	}

	public List<DalSqlParameterWrapper> getCallParameters() {
		return callParameters;
	}
}
