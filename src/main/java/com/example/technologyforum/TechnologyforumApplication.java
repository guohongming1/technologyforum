package com.example.technologyforum;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@Configuration
@EnableTransactionManagement
@MapperScan({"com.example.technologyforum.web.mapper"})
public class TechnologyforumApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechnologyforumApplication.class, args);
    }

}



