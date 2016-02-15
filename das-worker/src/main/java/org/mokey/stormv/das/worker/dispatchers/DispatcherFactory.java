package org.mokey.stormv.das.worker.dispatchers;

import org.mokey.stormv.das.worker.executors.ExecutorContext;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;

/**
 * Created by wcyuan on 2015/2/15.
 */
public class DispatcherFactory {
    private static DispatcherFactory ourInstance = new DispatcherFactory();

    public static DispatcherFactory getInstance() {
        return ourInstance;
    }

    private DispatcherFactory() {
    }

    public Dispatcher getDispatcher(ExecutorContext context){
        DynamicStringProperty type = DynamicPropertyFactory.getInstance().getStringProperty("worker.dispatcher.type", "default");
        if(type.get().isEmpty() || type.get().equalsIgnoreCase("default")){
            return new ServiceDispatcher(context);
        }else if(type.get().equalsIgnoreCase("hystrix")){
            return new HystrixServiceDispatcher(context);
        }
        return  null;
    }
}
