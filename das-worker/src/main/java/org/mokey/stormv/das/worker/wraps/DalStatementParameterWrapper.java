package org.mokey.stormv.das.worker.wraps;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.mokey.stormv.das.models.DalModels.DalHintEnum;

public class DalStatementParameterWrapper {
	
	private static final int DEFAULT_RESULT_SET_TYPE = ResultSet.TYPE_FORWARD_ONLY;
	private static final int DEFAULT_RESULT_SET_CONCURRENCY = ResultSet.CONCUR_READ_ONLY;
	
	private static DalStatementParameterWrapper dalStatementParameterWrapper = null;
	
	public static DalStatementParameterWrapper getStatementCreator(){
		if(dalStatementParameterWrapper == null)
			dalStatementParameterWrapper = new DalStatementParameterWrapper();
		return dalStatementParameterWrapper;
	}
	
	public Statement createStatement(Connection conn, DalHintsWrapper hintWrap) throws SQLException {
		Statement statement = conn.createStatement(getResultSetType(hintWrap), getResultSetConcurrency(hintWrap));
		applyHints(statement, hintWrap);
		return statement;
	}
	
	public PreparedStatement createPreparedStatementFromParameters(Connection conn, String sql, DalSqlParametersWrapper paramWrap, DalHintsWrapper hintWrap) throws SQLException{
		PreparedStatement statement;
		if(hintWrap.is(DalHintEnum.returnGeneratedKeys)){
			statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		}else {
			statement = conn.prepareStatement(sql, getResultSetType(hintWrap), getResultSetConcurrency(hintWrap));
		}

		applyHints(statement, hintWrap);

		setParameter(statement, paramWrap);
		return statement;
	}
	
	public CallableStatement createCallableStatement(Connection conn, String sql, DalSqlParametersWrapper paramWrap, DalHintsWrapper hintWrap) throws SQLException{;
		CallableStatement statement = conn.prepareCall(sql);
		applyHints(statement, hintWrap);

		setParameter(statement, paramWrap);
		
		registerOutParameters(statement, paramWrap);
		
		return statement;
	}
	
	public CallableStatement createBatchCallableStatement(Connection conn, String sql, List<DalSqlParametersWrapper> parameters, DalHintsWrapper hintWrap) throws SQLException{
		CallableStatement statement = conn.prepareCall(sql);
		
		applyHints(statement, hintWrap);
		for (DalSqlParametersWrapper paramWrap : parameters) {
			setParameter(statement, paramWrap);
			statement.addBatch();
		}
		return statement;
	}
	
	private void registerOutParameters(CallableStatement statement, DalSqlParametersWrapper paramWrap) throws SQLException {
		for (DalSqlParameterWrapper parameter: paramWrap.getParameters()) {
			if(parameter.isOutParameter()){
				statement.registerOutParameter(parameter.getName(), parameter.getSqlType());
			}
		}
	}
	
	private void setParameter(PreparedStatement statement, DalSqlParametersWrapper paramWrap) throws SQLException {
		for (DalSqlParameterWrapper parameter: paramWrap.getParameters()) {
			if(parameter.isInputParameter())
				statement.setObject(parameter.getIndex(), parameter.getValue(), parameter.getSqlType());
		}
	}
	
	public PreparedStatement createPreparedStatementFromParametersList(Connection conn, String sql, List<DalSqlParametersWrapper> parametersList, DalHintsWrapper hintWrap) throws SQLException{
		PreparedStatement statement = conn.prepareStatement(sql, getResultSetType(hintWrap), getResultSetConcurrency(hintWrap));
		applyHints(statement, hintWrap);
		
		for (DalSqlParametersWrapper paramWrap : parametersList) {
			setParameter(statement, paramWrap);
			statement.addBatch();
		}
		return statement;
	}
	
	private void applyHints(Statement statement, DalHintsWrapper hints) throws SQLException {
		Integer fetchSize = hints.getInt(DalHintEnum.fetchSize);
		
		if(fetchSize != null && fetchSize > 0)
			statement.setFetchSize(fetchSize);

		Integer maxRows = hints.getInt(DalHintEnum.maxRows);
		if (maxRows != null && maxRows > 0)
			statement.setMaxRows(maxRows);

		Integer timeout = hints.getInt(DalHintEnum.timeout);
		if (timeout != null && timeout > 0)
			statement.setQueryTimeout(timeout);
	}
	
	private int getResultSetType(DalHintsWrapper hints) {
		return hints.getInt(DalHintEnum.resultSetType, DEFAULT_RESULT_SET_TYPE);
	}
	
	private int getResultSetConcurrency(DalHintsWrapper hints) {
		return hints.getInt(DalHintEnum.resultSetConcurrency, DEFAULT_RESULT_SET_CONCURRENCY);
	}
}
