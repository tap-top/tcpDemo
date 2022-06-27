package org.tww.netty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 10064275
 * 客户端启动器
 */
@ConfigurationProperties(prefix = "netty")
@Configuration
public class MyNettyProperties {
    /**
     * boss线程数量 默认为cpu线程数*2
     */
    private Integer boss;
    /**
     * worker线程数量 默认为cpu线程数*2
     */
    private Integer worker;
    /**
     * 连接超时时间 默认为30s
     */
    private Integer timeout = 30000;
    /**
     * 命令连接端口
     */
    private Integer port = 20001;
    /**
     * 监听连接端口
     */
    private Integer eventPort = 20003;

    /**
     * 服务器端口
     */
    private Integer webSocketPort =9008;

    /**
     * 服务器地址 默认为本地
     */
    private String host = "127.0.0.1";

    public Integer getBoss() {
        return boss;
    }

    public void setBoss(Integer boss) {
        this.boss = boss;
    }

    public Integer getWorker() {
        return worker;
    }

    public void setWorker(Integer worker) {
        this.worker = worker;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getEventPort() {
        return eventPort;
    }

    public void setEventPort(Integer eventPort) {
        this.eventPort = eventPort;
    }

    public Integer getWebSocketPort() {
        return webSocketPort;
    }

    public void setWebSocketPort(Integer webSocketPort) {
        this.webSocketPort = webSocketPort;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
