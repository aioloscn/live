package com.aiolos.live.im.core.server.starter;

import com.aiolos.live.im.core.server.common.ChannelHandlerContextCache;
import com.aiolos.live.im.core.server.common.WebSocketEncoder;
import com.aiolos.live.im.core.server.handler.ws.WsImServerCoreHandler;
import com.aiolos.live.im.core.server.handler.ws.WsSharkHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class WsImServerApplication implements InitializingBean {
    
    @Value("${im.ws.port}")
    private int port;
    @Value("${im.bind.server.ip}")
    private String imBindServerIp;
    @Value("${dubbo.protocol.port}")
    private Integer dubboPort;
    @Resource
    private WsSharkHandler wsSharkHandler;
    @Resource
    private WsImServerCoreHandler wsImServerCoreHandler;

    public void startApplication() throws InterruptedException {
        // 处理accept事件
        NioEventLoopGroup  bossGroup = new NioEventLoopGroup();
        // 处理read&write事件
        NioEventLoopGroup  workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                log.info("初始化连接渠道");
                // 因为基于http协议，使用http编解码器
                channel.pipeline().addLast(new HttpServerCodec());
                // 处理大文件/大数据的分块写入，防止大数据传输内存溢出
                channel.pipeline().addLast(new ChunkedWriteHandler());
                // 将多个http消息片段聚合成完整的 FullHttpRequest 或 FullHttpResponse，最大聚合内容长度设置为 1MB
                channel.pipeline().addLast(new HttpObjectAggregator(1024 * 1024));
                channel.pipeline().addLast(new WebSocketEncoder());
                // 握手处理
                channel.pipeline().addLast(wsSharkHandler);
                // 消息处理
                channel.pipeline().addLast(wsImServerCoreHandler);
            }
        });
        // 基于JVM的钩子函数去实现优雅关闭
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }));

        if (StringUtils.isBlank(imBindServerIp) || dubboPort == null) {
            throw new IllegalArgumentException("The registered IP and port in the startup parameters cannot be empty!");
        }
        ChannelHandlerContextCache.setServerIpAddress(imBindServerIp + ":" + dubboPort);

        ChannelFuture channelFuture = bootstrap.bind(port).sync();
        log.info("IM服务启动成功，监听端口：{}", port);
        // 这里会阻塞掉线程，实现服务长期开启效果
        channelFuture.channel().closeFuture().sync();
    }

    @Override
    public void afterPropertiesSet() {
        new Thread(() -> {
            try {
                startApplication();
            } catch (InterruptedException e) {
                log.error("IM启动服务失败", e);
            }
        }, "live-im-server").start();
    }
}
