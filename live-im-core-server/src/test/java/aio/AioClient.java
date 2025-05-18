package aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;

public class AioClient {

    public static void main(String[] args) throws IOException, InterruptedException {
        // 打开一个客户端通道
        AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
        // 与服务端建立连接
        channel.connect(new InetSocketAddress("127.0.0.1", 9990));
        Thread.sleep(1000);
        try {
            channel.write(ByteBuffer.wrap("来自AIO客户端".getBytes())).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        try {
            // 从服务端读取返回的数据
            ByteBuffer bytebuffer = ByteBuffer.allocate(1024);
            channel.read(bytebuffer).get(); // 将通道中的数据写入到缓冲区中
            bytebuffer.flip();  // 缓冲区切换成读模式
            String result = Charset.defaultCharset().newDecoder().decode(bytebuffer).toString();
            System.out.println("来自AIO服务端的数据：" + result);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
