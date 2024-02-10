package com.cleanpay.vklogger.service;

import com.cleanpay.vklogger.configuration.VkCredentials;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        final GroupActor groupActor = vkAutoAuthService.groupActor();

        final String response = vkApiClient.messages()
                .sendUserIds(groupActor)
                .message(text)
//                .randomId(random.nextInt(random.nextInt(Integer.MAX_VALUE)))
                .userId(vkCredentials.getVkUserMessagesReceiver())
                .executeAsString();
        if (!response.startsWith("{\"response\"")) {
            log.error("Error parse send message");
            log.error(response);
            final GroupActor reservedActor = new GroupActor(vkCredentials.getGroupId(), vkCredentials.getGroupAccessToken());
            vkApiClient.messages().sendUserIds(reservedActor).message(response).userId(vkCredentials.getVkUserMessagesReceiver()).execute();
        }
        return response;
    }

}
