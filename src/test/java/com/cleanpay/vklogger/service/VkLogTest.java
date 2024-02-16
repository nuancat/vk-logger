package com.cleanpay.vklogger.service;

import com.cleanpay.vklogger.configuration.VkCredentials;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDateTime;

@SpringBootTest
class VkLogTest {
    @Autowired
    VkCredentials vkCredentials;
    @Autowired
    VkLog vkLog;

    @Test
    void checkVkLog() throws InterruptedException, ClientException, ApiException {
        for (int i = 0; i < 500; i++) {
            System.out.println(vkLog.sendMessage(LocalDateTime.now() + "   " + RandomStringUtils.randomAlphabetic(4000)));
            Thread.sleep(1000);
        }
    }

    @Test
    void sendPost() throws InterruptedException, ClientException, ApiException, IOException {
        System.out.println(vkLog.sendPost(LocalDateTime.now() + "   " + RandomStringUtils.randomAlphabetic(4000)));
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