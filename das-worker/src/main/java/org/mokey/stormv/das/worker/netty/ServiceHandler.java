package org.mokey.stormv.das.worker.netty;

import org.mokey.stormv.das.worker.cache.QueryCache;
import org.mokey.stormv.das.worker.dispatchers.Dispatcher;
import org.mokey.stormv.das.worker.dispatchers.DispatcherFactory;
import org.mokey.stormv.das.worker.executors.ExecutorContext;
import org.mokey.stormv.das.models.DalModels.Request;
import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicLongProperty;
import com.netflix.config.DynamicPropertyFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class ServiceHandler extends SimpleChannelInboundHandler<Request>{
	private static Logger logger = LoggerFactory.getLogger(ServiceHandler.class);

	DynamicIntProperty poolCoreSize = DynamicPropertyFactory.getInstance().getIntProperty("worker.thread-pool.core-size", 200);
	DynamicIntProperty poolMaximumSize = DynamicPropertyFactory.getInstance().getIntProperty("worker.thread-pool.maximum-size", 2000);
	DynamicLongProperty poolAliveTime = DynamicPropertyFactory.getInstance().getLongProperty("worker.thread-pool.alive-time", 1000 * 60 * 5);

	private final ThreadPoolExecutor poolExecutor;
	private static String channelCreateTimeAttrName = "channelCreateTime";

	public ServiceHandler(){
		poolExecutor = new ThreadPoolExecutor(poolCoreSize.get(), poolMaximumSize.get(), poolAliveTime.get(), TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>());
		Runnable c2 = new Runnable() {
			@Override
			public void run() {
				poolExecutor.setCorePoolSize(poolCoreSize.get());
				poolExecutor.setMaximumPoolSize(poolMaximumSize.get());
				poolExecutor.setKeepAliveTime(poolAliveTime.get(),TimeUnit.MILLISECONDS);
			}
		};

		poolCoreSize.addCallback(c2);
		poolMaximumSize.addCallback(c2);
		poolMaximumSize.addCallback(c2);
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Request msg)
			throws Exception {
		if(QueryCache.present(msg)){
			ctx.writeAndFlush(QueryCache.get(msg));
			return;
		}
		ExecutorContext context = new ExecutorContext(msg, ctx);
		Dispatcher dispatcher = DispatcherFactory.getInstance().getDispatcher(context);

		try {
			poolExecutor.submit(new ServiceTask(dispatcher));
		}catch (Throwable e){
			logger.error(msg.toString(), e);
			ctx.close();
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		channel.attr(AttributeKey.valueOf(channelCreateTimeAttrName)).set(System.currentTimeMillis());
		super.channelActive(ctx);
	}
}
