package com.zxy.product.mom.barcode.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;


public class ClientHandler extends ChannelInitializer<SocketChannel> {
    /**
     * 初始化通道以及配置对应管道的处理器
     */
    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
        pipeline.addLast(new LineBasedFrameDecoder(Integer.MAX_VALUE));
        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
        pipeline.addLast(new IdleStateHandler(60, 0, 30));
        pipeline.addLast(new ClientListenerHandler());
    }
}
