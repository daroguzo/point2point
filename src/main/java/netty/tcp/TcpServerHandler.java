package netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.SocketAddress;
import java.nio.charset.Charset;

public class TcpServerHandler extends ChannelInboundHandlerAdapter {

    private static final String ENCODING = "EUC-KR";
    private static final String RECEIVE_PORT = "28008";
    private static final String SEND_PORT = "28009";

    private static final ChannelGroup sendChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static final ChannelGroup receiveChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress incoming = ctx.channel().localAddress();
        System.out.println("-------------------------------------------------");
        System.out.println("[incoming: " + incoming + "]");
        String incomingPort = incoming.toString().split(":")[1];

        if (incomingPort.equals(SEND_PORT)) {
            sendChannelGroup.add(ctx.channel());
            System.out.println(ctx.channel().remoteAddress() + " [send channel added]");
        }

        if (incomingPort.equals(RECEIVE_PORT)) {
            receiveChannelGroup.add(ctx.channel());
            System.out.println(ctx.channel().remoteAddress() + " [receive channel added]");
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress incoming = ctx.channel().localAddress();
        System.out.println("-------------------------------------------------");
        System.out.println("[incoming: " + incoming + "]");

        String incomingPort = incoming.toString().split(":")[1];
        if (incomingPort.equals(SEND_PORT)) {
            sendChannelGroup.remove(ctx.channel());
            System.out.println(ctx.channel().remoteAddress() + " [send channel removed]");
        }

        if (incomingPort.equals(RECEIVE_PORT)) {
            receiveChannelGroup.remove(ctx.channel());
            System.out.println(ctx.channel() + " [receive channel removed]");
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("-------------------------------------------------");
        System.out.println("[received localAddress: " + ctx.channel().localAddress() + "]");
        System.out.println("[received remoteAddress: " + ctx.channel().remoteAddress() + "]");

        String readMessage = ((ByteBuf)msg).toString(Charset.forName(ENCODING));
        System.out.println("수신 문자열: [" + readMessage + "]");
        String sendMessage = readMessage.replace("TF01", "TF02");
        System.out.println("송신 문자열: [" + sendMessage + "]");

        ByteBuf messageBuffer = Unpooled.buffer();
        messageBuffer.writeBytes(sendMessage.getBytes(ENCODING));

        for (Channel channel : sendChannelGroup) {
            System.out.println(channel.remoteAddress() + " send");
            channel.writeAndFlush(messageBuffer);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();

    }
}
