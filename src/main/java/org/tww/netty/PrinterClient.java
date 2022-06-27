package com.zxy.product.mom.barcode.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author 10064275
 * 打印机发送命令
 */
public class PrinterClient {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY = 5;

    private static final PrinterClient PRINTER_CLIENT = new PrinterClient();
    private Channel channel = null;

    private Bootstrap bootstrap;
    private EventLoopGroup group;
    private int port;
    private String host;

    public static PrinterClient getInstance() {
        return PRINTER_CLIENT;
    }

    public PrinterClient setPort(int port) {
        this.port = port;
        return this;
    }

    public PrinterClient setHost(String host) {
        this.host = host;
        return this;
    }

    /**
     * 无入参连接
     */
    public void connect() {
        //重试计数
        this.connect(host, port, 0);
    }

    public void init() {
        if (channel != null) {
            return;
        }
        if (group == null) {
            //NIO线程组
            group = new NioEventLoopGroup();
        }
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new PrinterClientInitializer());
    }

    public void connect(String host, int port, int retry) {
        //绑定服务器
        try {
            ChannelFuture channelFuture = bootstrap
                    .connect(host, port)
                    .addListener(future -> {
                        //连接成功
                        if (future.isSuccess()) {
                            logger.info("连接成功!");
                        } else if (retry >= MAX_RETRY) {
                            logger.info("重试" + MAX_RETRY + "次失败，放弃连接！");
                        } else {
                            // 第几次重连
                            int order = retry + 1;
                            // 本次重连的间隔，1<<order相当于1乘以2的order次方
                            int delay = 1 << order;
                            logger.error(new Date() + ": 连接失败，第" + order + "次重连……");
                            bootstrap.config().group()
                                    .schedule(() ->
                                            connect(host, port, retry + 1), delay, TimeUnit.SECONDS);
                        }
                    })
                    .sync();

            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            disConnect();
        }
    }

    /**
     * 断开tcp连接.
     */
    public void disConnect() {
        if (null != group) {
            group.shutdownGracefully();
        }
        group = null;
        channel = null;
    }

    /**
     * 连接成功后，通过Channel提供的接口进行IO操作
     */
    public void sendMessage(String msg) {
        try {
            if (channel != null && channel.isOpen()) {
                channel.writeAndFlush(msg).sync();
                logger.info("send succeed " + msg);
            } else {
                throw new Exception("channel is null | closed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
