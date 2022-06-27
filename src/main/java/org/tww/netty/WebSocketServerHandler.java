package com.zxy.product.mom.barcode.netty;

import com.zxy.mom.sdk.common.util.JsonUtil;
import com.zxy.product.mom.barcode.entity.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 在线人数
     */
    public static int online;

    /**
     * 服务端接收客户端发送数据调用的方法
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        byte[] bytes = new byte[msg.content().readableBytes()];
        msg.content().readBytes(bytes);
        String s = new String(bytes, StandardCharsets.UTF_8);
        logger.info(ctx.channel().remoteAddress() + "读取消息:" + s);

        Message message = new Message();
        message.setType("1");
        message.setMsg(s);
        sendMessage(ctx, message);
    }

    /**
     * 处理客户端消息发生异常的时候被调用
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.getCause();
        ctx.channel().close();
    }

    /**
     * 客户端建立连接
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        channelGroup.add(ctx.channel());
        online = channelGroup.size();
        logger.info(ctx.channel().remoteAddress() + "上线了!");
    }

    /**
     * 关闭连接
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        channelGroup.remove(ctx.channel());
        online = channelGroup.size();
        logger.info(ctx.channel().remoteAddress() + "断开连接");
    }

    /**
     * 给某个人发送消息
     */
    private void sendMessage(ChannelHandlerContext ctx, Message msg) {
        ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.toJSONString(msg)));
    }

    /**
     *  给每个人发送消息,除发消息人外
     */
    private void sendAllMessages(ChannelHandlerContext ctx, Message msg) {
        for (Channel channel : channelGroup) {
            if (!channel.id().asLongText().equals(ctx.channel().id().asLongText())) {
                channel.writeAndFlush(new TextWebSocketFrame(JsonUtil.toJSONString(msg)));
            }
        }
    }
}