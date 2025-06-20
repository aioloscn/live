package com.aiolos.live.im.core.server.starter;

import com.aiolos.live.im.core.server.common.ChannelHandlerContextCache;
import com.aiolos.live.im.core.server.common.TcpMsgDecoder;
import com.aiolos.live.im.core.server.common.TcpMsgEncoder;
import com.aiolos.live.im.core.server.handler.tcp.TcpImServerCoreHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
public class TcpImServerApplication implements InitializingBean {
    
    @Value("${im.tcp.port}")
    private int port;
    @Value("${im.bind.server.ip}")
    private String imBindServerIp;
    @Value("${im.bind.server.port}")
    private Integer imBindServerPort;
    @Resource
    private TcpImServerCoreHandler tcpImServerCoreHandler;
    @Resource
    private Environment environment;

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
                // 增加编解码器
                channel.pipeline().addLast(new TcpMsgDecoder());
                channel.pipeline().addLast(new TcpMsgEncoder());
                // 设置这个netty处理handler
                channel.pipeline().addLast(tcpImServerCoreHandler);
            }
        });
        // 基于JVM的钩子函数去实现优雅关闭
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }));

        if (StringUtils.isBlank(imBindServerIp) || imBindServerPort == null) {
            throw new IllegalArgumentException("The registered IP and port in the startup parameters cannot be empty!");
        }
        ChannelHandlerContextCache.setServerIpAddress(imBindServerIp + ":" + imBindServerPort);

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
