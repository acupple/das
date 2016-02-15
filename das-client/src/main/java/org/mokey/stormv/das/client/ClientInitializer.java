package org.mokey.stormv.das.client;

import org.mokey.stormv.das.models.DalModels;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.JdkZlibEncoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.util.AttributeKey;

@ChannelHandler.Sharable
public class ClientInitializer extends ChannelInitializer<SocketChannel>{

    private static final String ProtobufVarint32FrameDecoder = "protobufVarint32FrameDecoder";
    private static final String JdkZlibDecoder = "compressDecode";
    private static final String ProtobufDecoder = "protobufDecoder";
    private static final String JdkZlibEncoder = "compressEncode";
    private static final String ProtobufVarint32LengthFieldPrepender = "protobufVarint32LengthFieldPrepender";
    private static final String ProtobufEncoder = "protobufEncoder";

    private static final String compressAttr = "compress";

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		    ChannelPipeline p = ch.pipeline();
            p.addLast(ProtobufVarint32FrameDecoder, new ProtobufVarint32FrameDecoder());
            p.addLast(ProtobufDecoder, new ProtobufDecoder(DalModels.Response.getDefaultInstance()));

            p.addLast(ProtobufVarint32LengthFieldPrepender, new ProtobufVarint32LengthFieldPrepender());
            p.addLast(ProtobufEncoder, new ProtobufEncoder());

            p.addLast(new ClientHandler());
	}

    public static void addCompressDecoderEncoder(ChannelFuture future){
        Object isCompress = future.channel().attr(AttributeKey.valueOf(compressAttr)).get();
        if(isCompress == null || (!(Boolean)isCompress)){
            future.channel().pipeline().addBefore(ProtobufVarint32FrameDecoder, JdkZlibDecoder, new JdkZlibDecoder());
            future.channel().pipeline().addBefore(ProtobufVarint32LengthFieldPrepender, JdkZlibEncoder, new JdkZlibEncoder());

            future.channel().attr(AttributeKey.valueOf(compressAttr)).set(true);
        }
    }

    public static void removeCompressDecoderEncoder(ChannelFuture future){
        Object isCompress = future.channel().attr(AttributeKey.valueOf(compressAttr)).get();
        if(isCompress != null && (Boolean)isCompress){
            future.channel().pipeline().remove(JdkZlibDecoder);
            future.channel().pipeline().remove(JdkZlibEncoder);

            future.channel().attr(AttributeKey.valueOf(compressAttr)).set(false);
        }
    }
}
