package org.mokey.stormv.das.worker.executors;

import org.mokey.stormv.das.worker.connections.DalConnection;
import org.mokey.stormv.das.worker.netty.DalWorkerInitializer;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * Created by wcyuan on 2015/3/9.
 */
public class SettingExecutor extends Executor {
    @Override
    public void execute(DalConnection connection) throws Exception {
        buildAndFlush().addListener(new ChannelFutureListener(){
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(context.getCompress()) {
                    DalWorkerInitializer.addCompressDecoderEncoder(future);
                }else {
                    DalWorkerInitializer.removeCompressDecoderEncoder(future);
                }
            }
        });
    }
}
