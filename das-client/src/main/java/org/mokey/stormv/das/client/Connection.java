package org.mokey.stormv.das.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Connection {
    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private Channel channel;
    private final String host;
    private final int port;

    private int queryTimeout = 2 * 60 * 1000;

    public Connection(String host, int port) {
        this.host = host;
        this.port = port;
        this.group = new NioEventLoopGroup();

        bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(new ClientInitializer())
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    public void connect() throws InterruptedException {
        this.channel = bootstrap.connect(host, port).sync().channel();
        System.out.println("Connected: " + host + ":" + port);
    }

    public int getQueryTimeout() {
        return queryTimeout;
    }

    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public ClientHandler getHandler() {
        return this.channel.pipeline().get(ClientHandler.class);
    }

    public Channel getChannel(){
        return this.channel;
    }

    public void close() {
        this.group.shutdownGracefully();
    }
}
