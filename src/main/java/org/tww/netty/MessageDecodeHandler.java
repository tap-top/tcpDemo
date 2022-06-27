package org.tww.netty;

import com.zxy.mom.sdk.common.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 后续byte转换消息
 */
@Deprecated
public class MessageDecodeHandler extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int len = byteBuf.readInt();
        byte[] content = new byte[len];
        byteBuf.readBytes(content);
        MessageBean messageBean = JsonUtil.parseObject(content, MessageBean.class);
        list.add(messageBean);
    }
}