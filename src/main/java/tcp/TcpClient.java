package tcp;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TcpClient {

    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 9599;
    private static final int LENGTH_PART_LENGTH = 4;
    private static final int INTERFACE_PART_LENGTH = 4;
    private static final String ENCODING = "EUC-KR";

    public static void main(String[] args) {
        Socket socket = null;
        String interfaceCode = "TF01";
        String message = "디리아";

        try {
            socket = new Socket();
            System.out.println("[연결 시도]");
            socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));
            System.out.println("[연결 성공]");

            // set length part
            StringBuilder sendLengthPart = new StringBuilder();
            int bytesLength = (interfaceCode + message).getBytes(ENCODING).length;
            String lengthString = String.valueOf(bytesLength);
            for (int i = 0; i < LENGTH_PART_LENGTH - lengthString.length(); i++) {
                sendLengthPart.append(0);
            }
            sendLengthPart.append(lengthString);

            // send
            OutputStream os = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            String sendData = sendLengthPart.toString() + interfaceCode + message;
            System.out.println("[보낸 데이터: " + sendData + "]");
            byte[] sendBytes = sendData.getBytes(ENCODING);
            dos.write(sendBytes);
            dos.flush();

            // receive
            InputStream is = socket.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            byte[] lengthBytes = new byte[LENGTH_PART_LENGTH];
            dis.readFully(lengthBytes);
            String lengthPart = new String(lengthBytes, ENCODING);
            System.out.println("[길이부: " + lengthPart + "]");
            int dataLength = Integer.parseInt(lengthPart);
            byte[] dataBytes = new byte[dataLength];
            dis.readFully(dataBytes);
            String dataPart = new String(dataBytes, ENCODING);

            String interfacePart = dataPart.substring(0, INTERFACE_PART_LENGTH);
            System.out.println("[인터페이스 코드: " + interfacePart + "]");
            String messagePart = dataPart.substring(INTERFACE_PART_LENGTH);
            System.out.println("[응답 메시지: " + messagePart + "]");

            dos.close();
            os.close();
            dis.close();
            is.close();
            socket.close();
            System.out.println("소켓 닫힘");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
