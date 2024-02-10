package com.cleanpay.vklogger;

import com.cleanpay.vklogger.configuration.VkCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@Slf4j
@EnableConfigurationProperties(value = VkCredentials.class)
public class VkLoggerApplication {

    public static void main(String[] args) {
        SpringApplication.run(VkLoggerApplication.class, args);
    }

}
