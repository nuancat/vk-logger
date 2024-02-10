package com.cleanpay.vklogger;

import com.cleanpay.vklogger.configuration.VkCredentials;
import com.cleanpay.vklogger.service.VkLog;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
class VkLoggerApplicationTests {
    @Autowired
    VkCredentials vkCredentials;
    @Autowired
    VkLog vkLog;

    @Test
    void contextLoads() {
    }

    @Test
    void checkVkCredentials() {
        Assertions.assertThat(vkCredentials.getGroupAccessToken())
                .isPrintable()
                .isNotBlank();
    }

    @Test
    void checkVkLog() {
        final var log = vkLog.log(LocalDateTime.now().toString());
        System.out.println(log);
    }

    @Test
    void checkVkLog_throwException() {
        try {
            throw new Exception();
        } catch (Exception e) {
            final var log = vkLog.log(e.getStackTrace().toString());
            System.out.println(log);
        }

    }
}
