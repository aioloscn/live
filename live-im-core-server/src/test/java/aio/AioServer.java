package aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

public class AioServer {
    
    public AsynchronousServerSocketChannel serverSocketChannel;
    
    public void listen() throws Exception {
        // 打开一个服务端通道
        serverSocketChannel = AsynchronousServerSocketChannel.open();
        // 监听端口
        serverSocketChannel.bind(new InetSocketAddress(9990));
        serverSocketChannel.accept(this, new CompletionHandler<>() {
            @Override
            public void completed(AsynchronousSocketChannel result, AioServer attachment) {
                try {
                    if (result.isOpen()) {
                        System.out.println("接收到新的客户端连接，地址：" + result.getRemoteAddress());
                        final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        // 读取客户端发送的数据
                        result.read(byteBuffer, result, new CompletionHandler<>() {
                            @Override
                            public void completed(Integer result, AsynchronousSocketChannel attachment) {
                                try {
                                    // 读取请求，处理客户端发送的数据
                                    byteBuffer.flip();
                                    String content = Charset.defaultCharset().newDecoder().decode(byteBuffer).toString();
                                    System.out.println("服务端接收到客户端发送的数据：" + content);
                                    // 向客户端发送数据
                                    ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
                                    writeBuffer.put("Server send".getBytes());
                                    writeBuffer.flip();
                                    attachment.write(writeBuffer).get();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
                                exc.printStackTrace();
                                try {
                                    attachment.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // 当有新的客户端接入时，直接调用accept方法，递归执行下去，这样可以保证多个客户端都阻塞
                    attachment.serverSocketChannel.accept(attachment, this);
                }
            }

            @Override
            public void failed(Throwable exc, AioServer attachment) {
                exc.printStackTrace();
            }
        });
    }

    public static void main(String[] args) throws Exception {
        new AioServer().listen();
        Thread.sleep(Integer.MAX_VALUE);
    }
}
