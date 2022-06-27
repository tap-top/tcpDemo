package com.zxy.product.mom.barcode.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 10064275
 * websocket服务
 */
public class WebSocketServer {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final WebSocketServer WEB_SOCKET_SERVER = new WebSocketServer();

    private ChannelFuture channelFuture;
    public static WebSocketServer getInstance() {
        return WEB_SOCKET_SERVER;
    }

    public void start(int port) {
        //循环组接收连接，不进行处理,转交给下面的线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //循环组处理连接，获取参数，进行工作处理
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //服务端进行启动类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //使用NIO模式，初始化器等等
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new WebSocketServerInitializer());
            //绑定端口
            channelFuture =
                    serverBootstrap.bind(port).sync();
            logger.info("WebSocket启动在端口：" + port);

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void sendMsg(String msg){
        channelFuture.channel().writeAndFlush(msg);
    }

}
