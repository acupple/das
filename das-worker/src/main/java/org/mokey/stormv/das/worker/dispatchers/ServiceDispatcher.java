package org.mokey.stormv.das.worker.dispatchers;

import org.mokey.stormv.das.worker.connections.DalConnection;
import org.mokey.stormv.das.worker.connections.DalConnectionManager;
import org.mokey.stormv.das.worker.executors.*;
import org.mokey.stormv.das.models.DalModels;

import org.mokey.stormv.das.models.DalModels.CommandType;

public class ServiceDispatcher implements Dispatcher{

	private ExecutorContext context;

	public ServiceDispatcher(ExecutorContext context){
		this.context = context;
	}

	public void dispatch() throws Exception{
		Executor executor = ExecutorFactory.getInstance().create(context.getCommandType());
		executor.setContext(context);
		if(context.getCommandType() == CommandType.SETTING){
			executor.execute(null);
			return;
		}
		if((context.getCommandType()==CommandType.BATCHUPDATE ||
				context.getCommandType() == CommandType.BATCHSTOREPROCEDURE) &&
				!context.getDalHintsWrapper().is(DalModels.DalHintEnum.forceAutoCommit)){
			this.doTransaction(executor);
		}else {
			this.doConnection(executor);
		}
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
