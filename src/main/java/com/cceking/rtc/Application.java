package com.cceking.rtc;

import com.cceking.rtc.servlet.DataWebSocketServlet;
import com.cceking.rtc.servlet.MonitorWebSocketServlet;
import com.cceking.rtc.servlet.UserWebSocketServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * @program: RTCApp
 * @description: 启动器
 * @author: cceking
 * @create: 2020-02-21 15:46
 **/
@EnableAutoConfiguration
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ServletRegistrationBean monitorWebSocketServlet() {
        return new ServletRegistrationBean(new MonitorWebSocketServlet(), "/monitor");
    }

    @Bean
    public ServletRegistrationBean dataWebSocketServlet() {
        return new ServletRegistrationBean(new DataWebSocketServlet(), "/data");
    }

    @Bean
    public ServletRegistrationBean userWebSocketServlet() {
        return new ServletRegistrationBean(new UserWebSocketServlet(), "/user");
    }
}
