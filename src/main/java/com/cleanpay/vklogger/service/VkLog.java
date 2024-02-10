package com.cleanpay.vklogger.service;

import com.cleanpay.vklogger.configuration.VkCredentials;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
@Slf4j
public class VkLog {
    private final VkApiClient vkApiClient;
    private final VkCredentials vkCredentials;
    private final VkAutoAuthService vkAutoAuthService;
    private final Random random = new Random();

    @SneakyThrows
    public String log(String text) {
        GroupActor groupActor = new GroupActor(vkCredentials.getGroupId(), getRandomAccessToken());
        final String response = vkApiClient.messages()
                .sendUserIds(groupActor)
                .message(text)
                .randomId(random.nextInt(Integer.MAX_VALUE))
                .userId(vkCredentials.getVkUserMessagesReceiver())
                .executeAsString();
        if (!response.startsWith("{\"response\"")) {
            log.error("Error parse send message");
            log.error(response);
            final GroupActor reservedActor = new GroupActor(vkCredentials.getGroupId(), getRandomAccessToken());
            vkApiClient.messages().sendUserIds(reservedActor).message(response).userId(vkCredentials.getVkUserMessagesReceiver()).execute();
        }
        return response;
    }

    public void heat() {
        try {
            vkAutoAuthService.groupActor();
        } catch (Exception e) {
            log.error("Не удалось нафармить ключ");
        }
    }

    private String getRandomAccessToken() {
        final List<String> keys = vkCredentials.getGroupAccessToken();
        return keys.get(random.nextInt(keys.size()));
    }

}
