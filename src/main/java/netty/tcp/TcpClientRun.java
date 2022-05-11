package netty.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TcpClientRun {
    private static final String HOST = "localhost";
    private static final int PORT = 28007;

    public static void main(String[] args) throws Exception{
        NioEventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new TcpClientHandler());
                        }
                    });
            // Start the client
            ChannelFuture future = bootstrap.connect(HOST, PORT).sync();

            // Wait until the server socket is closed
            future.channel().closeFuture().sync();
        } finally {
            // Shut down all event loops to terminate all threads
            group.shutdownGracefully();
        }
    }
}
