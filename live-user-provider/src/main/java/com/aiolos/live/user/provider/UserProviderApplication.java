package com.aiolos.live.user.provider;

import com.aiolos.live.enums.UserTagEnum;
import com.aiolos.live.user.provider.service.LiveUserTagService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
@ComponentScan("com.aiolos.live")
@MapperScan("com.aiolos.live.mapper")
public class UserProviderApplication implements CommandLineRunner {

    @Resource
    private LiveUserTagService liveUserTagService;
    
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(UserProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        ConfigurableApplicationContext context = springApplication.run(args);

        // 检查Web服务器Bean是否存在
        boolean isTomcatActive = context.containsBean("tomcatServletWebServerFactory");
        System.out.println("Tomcat是否启动？ " + isTomcatActive);

        Environment env = context.getEnvironment();
        System.out.println("spring.application.name=" + env.getProperty("spring.application.name"));
        System.out.println("redis.key.application-name=" + env.getProperty("redis.key.application-name"));
    }

    @Override
    public void run(String... args) throws Exception {

        /*CountDownLatch latch = new CountDownLatch(1);
        for (int i = 0; i < 100; i++) {
            new Thread(() -> liveUserTagService.setTag(101L, UserTagEnum.IS_VIP), "thread" + i).start();
        }
        latch.countDown();
        Thread.sleep(100000);*/
    }
}
