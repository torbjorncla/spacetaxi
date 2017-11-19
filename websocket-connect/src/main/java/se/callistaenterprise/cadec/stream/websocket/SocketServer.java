package se.callistaenterprise.cadec.stream.websocket;

import com.google.common.collect.EvictingQueue;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.concurrent.atomic.AtomicBoolean;

public class SocketServer {

    private final EventLoopGroup master;
    private final EventLoopGroup workers;

    private final int port;
    private final String path;

    private final AtomicBoolean connected = new AtomicBoolean(false);

    public SocketServer(final int port, final String path) {
        master = new NioEventLoopGroup(1);
        workers = new NioEventLoopGroup();

        this.port = port;
        this.path = path;
    }


    public void start() throws Exception {
        try {
            final ServerBootstrap b = new ServerBootstrap();
            b.group(master, workers)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new Initializer(path));
            final Channel ch = b.bind(port).sync().channel();
            connected.set(true);
            ch.closeFuture().sync();
        } finally {
            workers.shutdownGracefully();
            master.shutdownGracefully();
        }
    }

    public void stop() {
        connected.set(false);
        workers.shutdownGracefully();
        master.shutdownGracefully();
    }

    public boolean isConnected() {
        return connected.get();
    }
}
