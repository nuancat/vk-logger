package com.cleanpay.vklogger.service;

import com.cleanpay.vklogger.configuration.VkCredentials;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
class VkLogTest {
    @Autowired
    VkCredentials vkCredentials;
    @Autowired
    VkLog vkLog;

    @Test
    void checkVkLog() throws InterruptedException {
        for (int i = 0; i < 10000; i++) {
            final var log = vkLog.log(LocalDateTime.now() + "   " + RandomStringUtils.randomAlphabetic(3900));
            System.out.println(log);
            Thread.sleep(2000);
        }

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