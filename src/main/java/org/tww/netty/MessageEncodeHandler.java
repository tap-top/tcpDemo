package org.tww.netty;

import com.zxy.mom.sdk.common.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

/**
 * 后续消息转换byte
 */
@Deprecated
public class MessageEncodeHandler extends MessageToByteEncoder<MessageBean> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessageBean messageBean, ByteBuf byteBuf) throws Exception {
        String json = JsonUtil.toJSONString(messageBean);
        byteBuf.writeInt(json.length());
        byteBuf.writeBytes(json.getBytes(StandardCharsets.UTF_8));
    }
}