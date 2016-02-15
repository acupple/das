package org.mokey.stormv.das.worker.executors;

import org.mokey.stormv.das.worker.connections.DalConnection;
import org.mokey.stormv.das.worker.wraps.DalHintsWrapper;
import org.mokey.stormv.das.worker.wraps.DalSqlParameterWrapper;
import org.mokey.stormv.das.worker.wraps.DalStatementParameterWrapper;
import org.mokey.stormv.das.models.DalModels;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Execute the store-procedure/batch store-procedure request, and transport the result back to the remote client.
 * Created by wcyuan on 2015/2/5.
 */
public class StoreProcedureExecutor extends Executor{
    @Override
    public void execute(DalConnection connection) throws Exception {
        CallableStatement statement;
        if(context.isBatchWithParametersList()){
            statement = DalStatementParameterWrapper.getStatementCreator()
                    .createBatchCallableStatement(connection.getConn(),
                            context.getSql(),
                            context.getDalSqlParametersWrapperList(),
                            context.getDalHintsWrapper());
            this.addAffectRowsList(statement.executeBatch());
        }else {
            statement = DalStatementParameterWrapper.getStatementCreator()
                    .createCallableStatement(connection.getConn(),
                            context.getSql(),
                            context.getDalSqlParametersWrapper(),
                            context.getDalHintsWrapper());
            boolean retVal = statement.execute();
            int updateCount = statement.getUpdateCount();
            if (retVal || updateCount != -1) {
                this.addKeyHolders(extractReturnedResults(statement,
                        context.getDalSqlParametersWrapper().getResultParameters(),
                        updateCount, context.getDalHintsWrapper()));
            }
            this.addKeyHolders(extractOutputParameters(statement, context.getDalSqlParametersWrapper().getCallParameters()));
        }
        this.buildAndFlush();
    }

    private Map<String, String> extractReturnedResults(CallableStatement statement, List<DalSqlParameterWrapper> resultParameters, int updateCount, DalHintsWrapper hints) throws SQLException {
        Map<String, String> returnedResults = new LinkedHashMap<>();
        boolean moreResults;
        if(hints.is(DalModels.DalHintEnum.skipResultsProcessing))
            return returnedResults;

        if(resultParameters.size() == 0) {
            do {
                moreResults = statement.getMoreResults();
                updateCount = statement.getUpdateCount();
            }
            while (moreResults || updateCount != -1);
            return returnedResults;
        }

        int index = 0;
        do {
            if(index >= resultParameters.size())
                break;
            String key = resultParameters.get(index).getName();
            String value = "";
            if(updateCount == -1){
                ResultSet rs = statement.getResultSet();
                while (rs.next()){
                    value = rs.getObject(1).toString();
                }
            }else {
                value = updateCount + "";
            }

            moreResults = statement.getMoreResults();
            updateCount = statement.getUpdateCount();
            index++;
            returnedResults.put(key, value);
        }
        while (moreResults || updateCount != -1);

        return returnedResults;
    }

    private Map<String, String> extractOutputParameters(CallableStatement statement, List<DalSqlParameterWrapper> callParameters)
            throws SQLException {
        Map<String, String> returnedResults = new LinkedHashMap<>();
        for (DalSqlParameterWrapper parameter : callParameters) {
            Object value = statement.getObject(parameter.getName());
            if (value instanceof ResultSet) {
                ResultSet rs = (ResultSet)value;
                while (rs.next()){
                    value = rs.getObject(1);
                }
            }
            returnedResults.put(parameter.getName(), value.toString());
        }
        return returnedResults;
    }
}
