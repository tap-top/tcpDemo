package com.zxy.product.mom.barcode.netty;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 10064275
 * netty服务器统一管理
 */
@Configuration
public class NettyConfig {
    @Value("${printer.host:127.0.0.1}")
    private String host;

    /**
     * 打印机命令执行端口
     */
    @Value("${printer.port:20001}")
    private int port;

    /**
     * 打印机事件通知端口
     */
    @Value("${printer.event.port:20003}")
    private int eventPort;

    /**
     * 提供给页面监听的端口
     */
    @Value("${printer.server.port:9008}")
    private int serverPort;

    @Bean
    public NettyExecutor nettyExecutor() {
        NettyExecutor nettyExecutor = new NettyExecutor();
        nettyExecutor.setHost(host);
        nettyExecutor.setPort(port);
        nettyExecutor.setEventPort(eventPort);
        nettyExecutor.setServerPort(serverPort);
        return nettyExecutor;
    }
}
