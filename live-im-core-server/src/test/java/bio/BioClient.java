package bio;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class BioClient {

    public static void main(String[] args) throws InterruptedException {
        AtomicInteger connectCount = new AtomicInteger(0);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    Socket socket = new Socket();
                    countDownLatch.await();
                    socket.connect(new InetSocketAddress(9990));
                    System.out.println("连接完成");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        countDownLatch.countDown();
        Thread.sleep(100000);
    }
}
