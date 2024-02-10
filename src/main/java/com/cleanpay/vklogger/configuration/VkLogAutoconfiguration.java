package com.cleanpay.vklogger.configuration;

import com.cleanpay.vklogger.service.VkAutoAuthService;
import com.cleanpay.vklogger.service.VkLog;
import com.vk.api.sdk.client.VkApiClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = VkCredentials.class)
public class VkLogAutoconfiguration {
    @Bean
    VkLog getVkLog(
            VkApiClient vkApiClient,
            VkCredentials vkCredentials,
            VkAutoAuthService vkAutoAuthService
    ) {
        return new VkLog(vkApiClient, vkCredentials, vkAutoAuthService);
    }
}
