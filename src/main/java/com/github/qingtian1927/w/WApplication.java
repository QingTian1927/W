package com.github.qingtian1927.w;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WApplication {

    public static void main(String[] args) {
        SpringApplication.run(WApplication.class, args);
    }

}
