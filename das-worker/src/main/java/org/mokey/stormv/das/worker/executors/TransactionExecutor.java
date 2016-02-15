package org.mokey.stormv.das.worker.executors;

import org.mokey.stormv.das.worker.connections.DalConnection;
import org.mokey.stormv.das.models.DalModels;

/**
 * Created by wcyuan on 2015/2/6.
 */
public class TransactionExecutor extends Executor{
    @Override
    public void execute(DalConnection connection) throws Exception {
        DalModels.Transaction.Builder builder = DalModels.Transaction.newBuilder();
        if(context.getCommandType() == DalModels.CommandType.STARTTRANSACTION){
            int level = connection.startTransaction();
            builder.setStartLevel(level);
        }else if(context.getCommandType() == DalModels.CommandType.ENDTRANSACTION){
            if(context.getTransaction() == null || context.getTransaction().getStartLevel() < 0){
                throw new Exception("The end transaction request required a greater zero tranaction start level");
            }
            connection.endTransaction(context.getTransaction().getStartLevel());
        }else if(context.getCommandType() == DalModels.CommandType.ROLLBACKTRANSACTION){
            connection.rollbackTransaction();
        }
        builder.setTransactionId(connection.getUuid()); //Required
        this.buildAndFlush();
    }
}
