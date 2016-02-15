package org.mokey.stormv.das.worker.executors;

import org.mokey.stormv.das.worker.connections.DalConnection;
import org.mokey.stormv.das.worker.wraps.DalStatementParameterWrapper;
import org.mokey.stormv.das.models.DalModels;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Execute the query request, and transport the result set to the remote client.
 * Created by wcyuan on 2015/2/5.
 */
public class QueryExecutor extends Executor{
    @Override
    public void execute(DalConnection connection) throws Exception{
        PreparedStatement statement = DalStatementParameterWrapper
                .getStatementCreator()
                .createPreparedStatementFromParameters(
                        connection.getConn(), context.getSql(), context.getDalSqlParametersWrapper(),
                        context.getDalHintsWrapper());
        ResultSet rs = statement.executeQuery();
        rs.setFetchSize(context.getFetchSize());
        this.addQueryHeaders(rs);
        DalModels.InnerResultSet.Builder irs = DalModels.InnerResultSet.newBuilder().setLast(false);
        while(true){
            if(!rs.next()){
                irs.setLast(true);
                this.setResultSet(irs.build());
                break;
            }
            if(irs.getRecordsCount() >= context.getQuerySize()){
                this.setResultSet(irs.build());
                irs = DalModels.InnerResultSet.getDefaultInstance().toBuilder().setLast(false);
            }
            irs.addRecords(this.newRecord(rs));
        }
    }
}
