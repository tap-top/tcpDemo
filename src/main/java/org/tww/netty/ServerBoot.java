package com.zxy.product.mom.barcode.netty;

import com.zxy.mom.sdk.common.util.JsonUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.Set;

/**
 * @author 10064275
 * 服务端启动器
 */
@Component
public class ServerBoot {
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ServerBootstrap serverBootstrap;
    @Resource
    NioEventLoopGroup boosGroup;
    @Resource
    NioEventLoopGroup workerGroup;
    @Autowired
    MyNettyProperties nettyProperties;

    /**
     * 开机启动
     */
    @PostConstruct
    public void start() throws InterruptedException {
        // 绑定端口启动
        serverBootstrap.bind(nettyProperties.getWebSocketPort()).sync();
        log.info("启动Netty Websocket端口服务器: {}", nettyProperties.getWebSocketPort());
    }

    /**
     * 关闭线程池
     */
    @PreDestroy
    public void close() {
        log.info("关闭Netty服务器");
        boosGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    /**
     * 服务端给客户端发消息
     * todo 现在是群发
     */
    public void writeMsg(String msg) {
        if (ServerListenerHandler.online == 0) {
            return;
        }
        Set<Channel> keySet = ServerListenerHandler.channelGroup;
        for (Channel channel : keySet) {
            try {
                if (!channel.isActive()) {
                    continue;
                }
                MessageBean messageBean = new MessageBean();
                messageBean.setType(1);
                messageBean.setContent(msg);
                channel.writeAndFlush(
                        new TextWebSocketFrame(JsonUtil.toJSONString(messageBean)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
