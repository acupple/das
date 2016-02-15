package org.mokey.stormv.das.worker.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServer {
	private static Logger logger = LoggerFactory.getLogger(NettyServer.class);

	private final int port;
	private ServerBootstrap bootstrap;
	private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

	public NettyServer(int port){
		this.port = port;
		this.bossGroup = new NioEventLoopGroup(1);
		this.workerGroup =  new NioEventLoopGroup();
		this.bootstrap = new ServerBootstrap();
	}

	public void start() throws Exception {
		this.bootstrap.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new DalWorkerInitializer())
			.childOption(ChannelOption.SO_KEEPALIVE, true);
		this.bootstrap.bind(port).syncUninterruptibly();
		logger.info(NettyServer.class.getName() + "started and listen on " + this.port);
	}

	public void stop() {
		this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
		logger.info(NettyServer.class.getName() + "stopped gracefully");
	}
}
