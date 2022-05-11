package tcp;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer {
    private static final int SERVER_PORT = 9599;
    private static final int LENGTH_PART_LENGTH = 4;
    private static final int INTERFACE_PART_LENGTH = 4;
    private static final String ENCODING = "EUC-KR";

    public static void main(String[] args) {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(SERVER_PORT));
            System.out.println("[소켓 열림]");

            while (true) {
                System.out.println("\n[대기중..]");
                Socket socket = serverSocket.accept();
                InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();
                System.out.println("[Client Accept: " + isa.getHostName() + " : " + isa.getPort() + "]");

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
                System.out.println("[메시지: " + messagePart + "]");

                // response
                OutputStream os = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);

                String responseData = lengthPart + dataPart;
                byte[] responseBytes = responseData.getBytes(ENCODING);

                dos.write(responseBytes);
                dos.flush();
                System.out.println("[데이터 응답 완료]");

                dos.close();
                os.close();
                dis.close();
                is.close();
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("[소켓 닫힘]");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
