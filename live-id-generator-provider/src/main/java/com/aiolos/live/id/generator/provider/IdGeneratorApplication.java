package com.aiolos.live.id.generator.provider;

import com.aiolos.live.id.generator.provider.service.LiveIdGeneratorService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
@ComponentScan("com.aiolos.live")
@MapperScan("com.aiolos.live.mapper")
public class IdGeneratorApplication implements CommandLineRunner {

    @Resource
    private LiveIdGeneratorService liveIdGeneratorService;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(IdGeneratorApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }

    @Override
    public void run(String... args) throws Exception {

        /*new Thread(() -> test(), "thread1").start();
        new Thread(() -> test(), "thread2").start();
        new Thread(() -> test(), "thread3").start();
        new Thread(() -> test(), "thread4").start();
        new Thread(() -> test(), "thread5").start();
        new Thread(() -> test(), "thread6").start();*/
    }
    
    private void test() {
        for (int i = 0; i < 50; i++) {
            Long seqId = liveIdGeneratorService.getSeqId(1);
            System.out.println(Thread.currentThread().getName() + ": " + seqId);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
