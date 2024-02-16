package com.cleanpay.vklogger;

import com.cleanpay.vklogger.configuration.VkCredentials;
import com.cleanpay.vklogger.service.VkLog;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
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
                .isNotEmpty();
    }

    @Test
    void checkVkLog() throws ClientException, ApiException {
        final var log = vkLog.sendMessage(LocalDateTime.now().toString());
        System.out.println(log);
    }

    @Test
    void checkVkLog_throwException() throws ClientException, ApiException {
        try {
            throw new Exception();
        } catch (Exception e) {
            final var log = vkLog.sendMessage(e.getStackTrace().toString());
            System.out.println(log);
        }

    }
}
