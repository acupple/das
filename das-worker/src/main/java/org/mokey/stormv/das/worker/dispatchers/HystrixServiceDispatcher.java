package org.mokey.stormv.das.worker.dispatchers;

import org.mokey.stormv.das.worker.connections.DalConnection;
import org.mokey.stormv.das.worker.connections.DalConnectionManager;
import org.mokey.stormv.das.worker.executors.*;
import org.mokey.stormv.das.models.DalModels;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

/**
 * Created by wcyuan on 2015/2/15.
 */
public class HystrixServiceDispatcher extends HystrixCommand<Void> implements Dispatcher {
    private ExecutorContext context;

    public HystrixServiceDispatcher(ExecutorContext context){
        super(HystrixCommandGroupKey.Factory.asKey(context.getClientIp() + ":" + context.getDbName()));
        this.context = context;
    }

    @Override
    public void dispatch() throws Exception {
        Executor executor = ExecutorFactory.getInstance().create(context.getCommandType());
        executor.setContext(context);
        if((context.getCommandType()== DalModels.CommandType.BATCHUPDATE ||
                context.getCommandType() == DalModels.CommandType.BATCHSTOREPROCEDURE) &&
                !context.getDalHintsWrapper().is(DalModels.DalHintEnum.forceAutoCommit)){
            this.doTransaction(executor);
        }else {
            this.doConnection(executor);
        }
    }

    @Override
    protected Void run() throws Exception {
        dispatch();
        return  null;
    }

    private void doTransaction(Executor executor){
        DalConnection connection = null;
        try{
            connection = DalConnectionManager.instance()
                    .getDalConnection(executor.getContext().getTransaction(),
                            executor.getContext().getDbName());
            int startLevel = connection.startTransaction();
            executor.execute(connection);
            connection.endTransaction(startLevel);
        }catch (Exception e){
            try {
                connection.rollbackTransaction();
            }catch (Exception el){}
            executor.throwException(e);
        }finally {
            if(connection != null){
                connection.close();
            }
        }
    }

    private void doConnection(Executor executor){
        DalConnection connection = null;
        try{
            connection = DalConnectionManager.instance()
                    .getDalConnection(executor.getContext().getTransaction(),
                            executor.getContext().getDbName());
            executor.execute(connection);
        }catch (Exception e){
            executor.throwException(e);
        }finally {
            if(connection != null){
                connection.close();
            }
        }
    }
}
