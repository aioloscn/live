package bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BioServer2 {

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 10, 3, TimeUnit.MINUTES, new ArrayBlockingQueue<>(100));

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(9990));
        for (; ; ) {
            try {
                Socket socket = serverSocket.accept();
                executor.execute(() -> {
                    try {
                        InputStream inputStream = socket.getInputStream();
                        byte[] bytes = new byte[10];
                        // 阻塞，直到有数据，处理完再处理下一个线程
                        inputStream.read();
                        System.out.println("服务端接收到数据：" + new String(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
