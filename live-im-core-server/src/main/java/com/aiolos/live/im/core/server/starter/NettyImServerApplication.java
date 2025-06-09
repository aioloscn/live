package com.aiolos.live.im.core.server.starter;

import com.aiolos.live.im.core.server.common.ChannelHandlerContextCache;
import com.aiolos.live.im.core.server.common.ImMsgDecoder;
import com.aiolos.live.im.core.server.common.ImMsgEncoder;
import com.aiolos.live.im.core.server.handler.ImServerCoreHandler;
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
public class NettyImServerApplication implements InitializingBean {
    
    @Value("${im.port}")
    private int port;
    @Resource
    private ImServerCoreHandler imServerCoreHandler;
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
                // 设计消息体
                
                // 增加编解码器
                channel.pipeline().addLast(new ImMsgDecoder());
                channel.pipeline().addLast(new ImMsgEncoder());
                // 设置这个netty处理handler
                channel.pipeline().addLast(imServerCoreHandler);
            }
        });
        // 基于JVM的钩子函数去实现优雅关闭
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }));

        String dubboIpToRegistry = environment.getProperty("DUBBO_IP_TO_REGISTRY");
        String dubboPortToBind = environment.getProperty("DUBBO_PORT_TO_BIND");
        if (StringUtils.isBlank(dubboIpToRegistry) || StringUtils.isBlank(dubboPortToBind)) {
            throw new IllegalArgumentException("The registered IP and port in the startup parameters cannot be empty!");
        }
        ChannelHandlerContextCache.setServerIpAddress(dubboIpToRegistry + ":" + dubboPortToBind);

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
