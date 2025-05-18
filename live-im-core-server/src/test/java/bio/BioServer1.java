package bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class BioServer1 {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(9990));
        // 阻塞，知道接收到连接
        Socket socket = serverSocket.accept();
        for (; ; ) {
            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[10];
            // 阻塞，直到有数据
            inputStream.read(bytes);
            System.out.println("服务端接收到数据：" + new String(bytes));
        }
    }
}
