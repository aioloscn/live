package com.aiolos.live.user.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
@MapperScan("com.aiolos.live.user.provider.mapper")
public class UserProviderApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(UserProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        ConfigurableApplicationContext context = springApplication.run(args);

        // 检查Web服务器Bean是否存在
        boolean isTomcatActive = context.containsBean("tomcatServletWebServerFactory");
        System.out.println("Tomcat是否启动？ " + isTomcatActive);
    }
}
