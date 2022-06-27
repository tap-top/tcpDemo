package com.zxy.product.mom.barcode.netty;

import com.zxy.mom.sdk.spi.ServiceFinder;
import com.zxy.product.mom.barcode.service.PrintService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class ClientListenerHandler extends SimpleChannelInboundHandler<Object> {
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 延时几秒调用
     */
    public static final int DELAY = 10;

    /**
     * 服务端上线的时候调用
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("{}连上了服务器", ctx.channel().remoteAddress());
    }

    /**
     * 服务端掉线的时候调用
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("{}断开了服务器", ctx.channel().remoteAddress());
        InetSocketAddress a = (InetSocketAddress) ctx.channel().remoteAddress();
        EventLoop loop = ctx.channel().eventLoop();

        //间隔DELAY秒调用 命令
        loop.schedule(() -> ServiceFinder.findService(ClientBoot.class)
                .connect(), DELAY, TimeUnit.SECONDS);
        //间隔DELAY秒调用 事件
        loop.schedule(() -> ServiceFinder.findService(ClientBoot.class)
                .connectEvent(), DELAY, TimeUnit.SECONDS);
        log.info("{};" + DELAY + "秒后重新尝试连接", ctx.channel().remoteAddress());

        ctx.fireChannelInactive();
    }


    /**
     * 读取服务端消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object message) throws Exception {
        String msg = String.valueOf(message);
        log.debug("来自服务端的消息:{}", msg);

        try {
            //通知应用
            ServiceFinder.findService(PrintService.class)
                    .printReturn(msg);
        } catch (Exception e) {
            log.error(message + "处理报错");
            e.printStackTrace();
        }
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

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            //该事件需要配合 io.netty.handler.timeout.IdleStateHandler使用
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.ALL_IDLE) {
                //向服务端发送心跳检测
                ctx.writeAndFlush("GetStatus;");
                log.debug("发送心跳数据");
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}

