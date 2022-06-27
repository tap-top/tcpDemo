package org.tww.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class PrinterClientHandler extends SimpleChannelInboundHandler<Object> {

    private final Log logger = LogFactory.getLog(PrinterClientHandler.class);

    private ChannelHandlerContext ctx;
    private boolean isConnect = false;

    /**
     * 客户端接收服务端发送数据调用的方法
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info(ctx.channel().remoteAddress());
        logger.info("连接正常messageReceived");
        ByteBuf msg1 = (ByteBuf) msg;
        byte[] bytes = new byte[msg1.readableBytes()];
        msg1.readBytes(bytes);
        String s = new String(bytes, "UTF-8");
        logger.info("接收到的消息:" + s);
    }

    /**
     * 客户端和服务端的连接建立之后就会被调用
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.info("连接正常channelActive");
        isConnect = true;
        if (this.ctx == null) {
            synchronized (PrinterClientHandler.class) {
                if (this.ctx == null) {
                    this.ctx = ctx;
                    linkHeart();
                }
            }
        }
    }

    private void linkHeart() {
        new Thread(() -> {
            while (ctx != null && isConnect) {
                String data = "GetVersion;";
                byte[] bytes = data.getBytes();
                if (isConnect) {
                    ctx.writeAndFlush(Unpooled.buffer(bytes.length).writeBytes(bytes));
                    try {
                        sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        EventLoop loop = ctx.channel().eventLoop();
        loop.schedule(() -> PrinterClient.getInstance().connect(), 5, TimeUnit.SECONDS);
        super.channelInactive(ctx);
        logger.info("重新连接socket服务器");
        isConnect = false;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        logger.info("发送数据包");
    }

    /**
     * 处理消息发生异常的时候被调用
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.info("连接出现异常");
        this.ctx.close();
        this.ctx = null;
    }
}