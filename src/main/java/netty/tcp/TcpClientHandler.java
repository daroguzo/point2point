package netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;
import java.util.UUID;

public class TcpClientHandler extends ChannelInboundHandlerAdapter {

    private static final int LENGTH_PART_LENGTH = 4;
    private static final String ENCODING = "EUC-KR";

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String interfaceCode = "TF01";
        String message = "DIREA";
        String guid = UUID.randomUUID().toString().substring(0,8);

        // set the length part
        StringBuilder sendLengthPart = new StringBuilder();
        int bytesLength = (interfaceCode + guid + message).getBytes(ENCODING).length;
        String lengthString = String.valueOf(bytesLength);
        for (int i = 0; i < LENGTH_PART_LENGTH - lengthString.length(); i++) {
            sendLengthPart.append(0);
        }
        sendLengthPart.append(lengthString);
        String sendData = sendLengthPart.toString() + interfaceCode + guid + message;

        ByteBuf messageBuffer = Unpooled.buffer();
        messageBuffer.writeBytes(sendData.getBytes(ENCODING));
        System.out.println("보낸 문자열: [" + sendData + "]");
        ctx.writeAndFlush(messageBuffer);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String readMessage = ((ByteBuf)msg).toString(Charset.forName(ENCODING));
        System.out.println("받은 문자열: [" + readMessage + "]");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.close()
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
