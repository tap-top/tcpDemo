package com.zxy.product.mom.barcode.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;


/**
 * @author 10064275
 * 服务端处理器
 */
public class ServerHandler extends ChannelInitializer<SocketChannel> {

    /**
     * 初始化通道以及配置对应管道的处理器
     */
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new HttpServerCodec());
        //支持写大数据流
        pipeline.addLast(new ChunkedWriteHandler());
        //http聚合器
        pipeline.addLast(new HttpObjectAggregator(1024*62));
        //websocket支持,设置路由
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        pipeline.addLast(new ServerListenerHandler());
    }
}
