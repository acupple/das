package org.mokey.stormv.das.worker.executors;

import org.mokey.stormv.das.worker.connections.DalConnection;
import org.mokey.stormv.das.worker.wraps.DalStatementParameterWrapper;
import org.mokey.stormv.das.models.DalModels;

import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * Execute the update/batch update request, and transport the result back to the remote client.
 * Created by wcyuan on 2015/2/5.
 */
public class UpdateExecutor extends Executor{
    @Override
    public void execute(DalConnection connection) throws Exception {
        Statement statement;
        if(context.isBatchWithOutParameters()){
            statement = DalStatementParameterWrapper.getStatementCreator()
                    .createStatement(connection.getConn(),context.getDalHintsWrapper());
            for(String sql: context.getSqls()){
                statement.addBatch(sql);
            }
            this.addAffectRowsList(statement.executeBatch());
        }else if(context.isBatchWithParametersList()){
            statement = DalStatementParameterWrapper.getStatementCreator()
                    .createPreparedStatementFromParametersList(connection.getConn(),
                            context.getSql(),
                            context.getDalSqlParametersWrapperList(),
                            context.getDalHintsWrapper());
            this.addAffectRowsList(statement.executeBatch());
        }else {
            statement = DalStatementParameterWrapper.getStatementCreator()
                    .createPreparedStatementFromParameters(connection.getConn(),
                            context.getSql(),
                            context.getDalSqlParametersWrapper(),
                            context.getDalHintsWrapper());
            int ret = ((PreparedStatement)statement).executeUpdate();
            this.setAffectRows(ret);
        }
        if(context.getDalHintsWrapper().is(DalModels.DalHintEnum.returnGeneratedKeys)){
            this.buildGeneratedKeys(statement.getGeneratedKeys());
        }

        this.buildAndFlush();
    }
}
