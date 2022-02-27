package com.ws.demo;

import com.ws.demo.utils.SpringUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class Application {

    @Bean
    public SpringUtil getSpringUtil(){
        return new SpringUtil();
    }


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
