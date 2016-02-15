package org.mokey.stormv.das.worker.netty;

import org.mokey.stormv.das.worker.dispatchers.Dispatcher;

import java.util.concurrent.Callable;

/**
 * Created by wcyuan on 2015/3/6.
 */
public class ServiceTask implements Callable<Void> {
    private Dispatcher dispatcher;

    public ServiceTask(Dispatcher dispatcher){
        this.dispatcher = dispatcher;
    }

    @Override
    public Void call() throws Exception {
        this.dispatcher.dispatch();
        return null;
    }
}
