package se.callistaenterprise.cadec.stream.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import se.callistaenterprise.cadec.stream.WebsocketSource;

@Slf4j
public class Handler extends SimpleChannelInboundHandler<WebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if(frame instanceof TextWebSocketFrame) {
            final String payload = ((TextWebSocketFrame)frame).text();
            //ctx.channel().writeAndFlush(new TextWebSocketFrame(payload));
            log.info("Got socket-message: {}", payload);
            WebsocketSource.put(payload);
        } else {
            log.warn("Not supported protocol: ", frame.getClass().getName());
        }
    }
}
