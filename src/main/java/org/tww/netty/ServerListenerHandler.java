package com.zxy.product.mom.barcode.netty;

import com.zxy.mom.sdk.common.util.JsonUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * @author 10064275
 * 服务端监听器
 */
@ChannelHandler.Sharable
public class ServerListenerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 在线人数
     */
    public static int online;

    /**
     * 客户端上线的时候调用
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String address = String.valueOf(ctx.channel().remoteAddress());
        log.info("{}客户端连接进来了", address);
        channelGroup.add(ctx.channel());
        online = channelGroup.size();

        ctx.fireChannelActive();
    }

    /**
     * 客户端掉线的时候调用
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String address = String.valueOf(ctx.channel().remoteAddress());
        log.info("{}连接断开了", address);
        channelGroup.remove(ctx.channel());
        online = channelGroup.size();

        ctx.fireChannelInactive();
    }


    /**
     * 读取客户端信息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame message) throws Exception {
        String remoteAddress = channelHandlerContext.channel().remoteAddress().toString();
        byte[] bytes = new byte[message.content().readableBytes()];
        message.content().readBytes(bytes);
        String s = new String(bytes, StandardCharsets.UTF_8);

        log.debug("来自客户端{}的消息{}", remoteAddress, s);
        channelHandlerContext.writeAndFlush(
                new TextWebSocketFrame("收到了客户端" + remoteAddress + "的消息" + s));
    }

    /**
     * 异常发生时候调用
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("{}连接出异常了", ctx.channel().remoteAddress());
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 给每个人发送消息,除发消息人外
     */
    private void sendAllMessages(String msg) {
        for (Channel channel : channelGroup) {
            channel.writeAndFlush(new TextWebSocketFrame(JsonUtil.toJSONString(msg)));
        }
    }
}
