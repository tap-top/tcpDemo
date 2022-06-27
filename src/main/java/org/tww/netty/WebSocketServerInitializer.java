package com.zxy.product.mom.barcode.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * 连接注册，创建成功，会被调用
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        //支持写大数据流
        pipeline.addLast(new ChunkedWriteHandler());
        //http聚合器
        pipeline.addLast(new HttpObjectAggregator(1024*62));
        //websocket支持,设置路由
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        pipeline.addLast("handler", new WebSocketServerHandler());
    }
}
