package com.zxy.product.mom.barcode.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author 10064275
 * 客户端
 */
@Component
public class ClientBoot implements CommandLineRunner {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 延时几秒调用
     */
    public static final int DELAY = 60;
    @Autowired
    Bootstrap bootstrap;
    @Autowired
    MyNettyProperties myNettyProperties;

    /**
     * 命令发送 缓存
     */
    ChannelFuture channelFuture;

    /**
     * 事件监听 缓存
     */
    ChannelFuture channelFutureEvent;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("启动事件监听。。。");
        this.connectEvent();
        System.out.println(getClass() + "事件监听已完成");
    }

    public Channel connect() throws InterruptedException {
        String host = myNettyProperties.getHost();
        int port = myNettyProperties.getPort();
        return this.connect(host, port);
    }

    /**
     * 主端口连接
     */
    public Channel connect(String host, int port) throws InterruptedException {
        if (channelFuture != null) {
            if (channelFuture.channel().isOpen()) {
                return channelFuture.channel();
            }
        }

        // 连接服务器
        channelFuture = bootstrap.connect(host, port)
                .addListener(future -> {
                    if (future.isSuccess()) {
                        log.info(host + ":" + port + "连接成功！");
                    } else {
                        log.info(host + ":" + port + "连接失败！");
                        bootstrap.config().group()
                                .schedule(() -> this.connect(host, port), DELAY, TimeUnit.SECONDS);
                    }
                }).sync();
        // 监听关闭
        Channel channel = channelFuture.channel();
        return channel;
    }


    public Channel connectEvent() throws InterruptedException {
        String host = myNettyProperties.getHost();
        int eventPort = myNettyProperties.getEventPort();
        return this.connectEvent(host, eventPort);
    }

    /**
     * 监听事件
     */
    public Channel connectEvent(String host, int eventPort) throws InterruptedException {
        if (channelFutureEvent != null) {
            if (channelFutureEvent.channel().isOpen()) {
                return channelFutureEvent.channel();
            }
        }
        // 连接服务器
        channelFutureEvent =
                bootstrap.connect(host, eventPort)
                        .addListener(future -> {
                            if (future.isSuccess()) {
                                log.info(host + ":" + eventPort + "连接成功！");
                            } else {
                                log.info(host + ":" + eventPort + "连接失败！");
                                bootstrap.config().group()
                                        .schedule(() -> this.connectEvent(host, eventPort), DELAY, TimeUnit.SECONDS);
                            }
                        }).sync();
        // 监听关闭
        Channel channel = channelFutureEvent.channel();
        return channel;
    }

    /**
     * 发送消息到服务器端
     */
    public void sendMsg(String msg) throws InterruptedException {
        //补上\r\n
        connect().writeAndFlush(msg+"\r\n");

        //开启服务端
        connectEvent();
    }
}


