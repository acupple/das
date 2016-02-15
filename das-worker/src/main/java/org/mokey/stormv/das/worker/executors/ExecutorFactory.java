package org.mokey.stormv.das.worker.executors;

import org.mokey.stormv.das.models.DalModels;

/**
 * Created by wcyuan on 2015/2/10.
 */
public class ExecutorFactory {
    private static ExecutorFactory ourInstance = new ExecutorFactory();

    public static ExecutorFactory getInstance() {
        return ourInstance;
    }

    private ExecutorFactory() {
    }

    public Executor create(DalModels.CommandType commandType) throws Exception{
        Executor executor;
        switch (commandType){
            case QUERY:
                executor = new QueryExecutor();
                break;
            case UPDATE:
            case BATCHUPDATE:
                executor = new UpdateExecutor();
                break;
            case STOREPROCEDURE:
            case BATCHSTOREPROCEDURE:
                executor = new StoreProcedureExecutor();
                break;
            case STARTTRANSACTION:
            case ENDTRANSACTION:
            case ROLLBACKTRANSACTION:
                executor = new TransactionExecutor();
                break;
            case SETTING:
                executor = new SettingExecutor();
                break;
            default:
                throw  new Exception(
                        String.format("The Command Type[%s] is not supported.",commandType));
        }

        return executor;
    }
}
