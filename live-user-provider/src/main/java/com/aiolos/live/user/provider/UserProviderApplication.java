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
@ComponentScan("com.aiolos")    // service实现类不在当前package下，需要指定扫描
@MapperScan("com.aiolos.live.mapper")
public class UserProviderApplication implements CommandLineRunner {

    @Resource
    private LiveUserTagService liveUserTagService;
    
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(UserProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.SERVLET);
        springApplication.run(args);
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
