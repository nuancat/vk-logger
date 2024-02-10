package com.cleanpay.vklogger.configuration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("vk.credentials")
@Data
public class VkCredentials {
    @NotBlank
    List<String> groupAccessToken;
    @NotBlank
    Integer appId;
    @NotNull
    Long groupId;
    @NotBlank
    Long vkUserMessagesReceiver;
    // Если майним токен по почте с паролем
    String email;
    String password;
    String defendKey;
    String serviceAccessKey;
}
