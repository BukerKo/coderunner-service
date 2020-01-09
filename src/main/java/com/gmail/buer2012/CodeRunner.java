package com.gmail.buer2012;

import com.gmail.buer2012.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class CodeRunner {
    
    public static void main(String[] args) {
        SpringApplication.run(CodeRunner.class, args);
    }
}
