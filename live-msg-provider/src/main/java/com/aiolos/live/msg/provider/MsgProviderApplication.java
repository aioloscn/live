package com.aiolos.live.msg.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
@ComponentScan("com.aiolos")
@MapperScan("com.aiolos.live.mapper")
public class MsgProviderApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(MsgProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }
}
