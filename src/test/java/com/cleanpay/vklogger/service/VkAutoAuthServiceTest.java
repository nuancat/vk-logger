package com.cleanpay.vklogger.service;

import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.UserActor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VkAutoAuthServiceTest {
    @Autowired
    VkAutoAuthService vkAutoAuthService;

    @Test
    void groupActor() {
        final GroupActor groupActor = vkAutoAuthService.groupActor();
        Assertions.assertThat(groupActor)
                .matches(e -> e.getGroupId() != null)
                .matches(e -> e.getAccessToken() != null)
                .matches(e -> e.getId() != null);
    }

    @Test
    void userActor() {
        final UserActor groupActor = vkAutoAuthService.userActor();
        Assertions.assertThat(groupActor)
                .matches(e -> e.getAccessToken() != null)
                .matches(e -> e.getId() != null);
    }
}