package com.cleanpay.vklogger.configuration;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VkConnectConfiguration {

    @Bean
    TransportClient getTransportClient() {
        return new HttpTransportClient();
    }

    @Bean
    VkApiClient getVkApiClient(TransportClient transportClient) {
        return new VkApiClient(transportClient);
    }
}
