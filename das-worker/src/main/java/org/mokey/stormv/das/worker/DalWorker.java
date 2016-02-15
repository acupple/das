package org.mokey.stormv.das.worker;

import org.mokey.stormv.das.worker.netty.NettyServer;
import org.mokey.stormv.das.worker.utils.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wcyuan on 2015/2/15.
 */
public class DalWorker {
    private static Logger logger = LoggerFactory.getLogger(DalWorker.class);
    public static void main(String[] args){
        try{
            int port = 8080;
            if(args.length > 0){
                port = Integer.parseInt(args[0]);
            }

            Configurator.config(args.length >= 2 ? args[1] : "");

            final NettyServer server = new NettyServer(port);
            server.start();
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
                @Override
                public void run() {
                    server.stop();
                }}));
        }catch (Exception e){
            logger.error("can't start netty server", e);
        }
    }
}
