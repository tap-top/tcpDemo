package org.tww.netty;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Configuration;

/**
 * @author 10064275
 * netty服务器统一管理
 */
@Configuration
public class NettyExecutor implements SmartInitializingSingleton, DisposableBean {
    private String host;

    /**
     * 打印机命令执行端口
     */
    private int port;

    /**
     * 打印机事件通知端口
     */
    private int eventPort;

    /**
     * 提供给页面监听的端口
     */
    private int serverPort;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getEventPort() {
        return eventPort;
    }

    public void setEventPort(int eventPort) {
        this.eventPort = eventPort;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void afterSingletonsInstantiated() {
        System.out.println("启动服务");
        try {
            //启动 命令端
            PrinterClient.getInstance().init();
            PrinterClient.getInstance().connect(host, port, 0);
//            PrinterClient.getInstance().connect(host, eventPort, 0);

            //启动 websocket端
            WebSocketServer.getInstance().start(serverPort);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        PrinterClient.getInstance().disConnect();
    }
}
