package com.cleanpay.vklogger.service;

import com.cleanpay.vklogger.configuration.VkCredentials;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.docs.responses.SaveResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

@RequiredArgsConstructor
@Service
@Slf4j
public class VkLog {
    private final VkApiClient vkApiClient;
    private final VkCredentials vkCredentials;
    private final VkAutoAuthService vkAutoAuthService;
    private final Random random = new Random();
    UserActor userActor;
    Gson gson = new Gson();

    @PostConstruct
    void postConstruct() {
        userActor = new UserActor(vkCredentials.getVkUserMessagesReceiver(), vkCredentials.getUserAccessToken().get(0));
    }

    public String sendMessage(String text) throws ClientException, ApiException {
        final GroupActor groupActor = new GroupActor(vkCredentials.getGroupId(), getRandomAccessToken());
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

    public String sendPost() throws ClientException, ApiException, IOException, InterruptedException {
        final URI uploadLink = vkApiClient.docs()
                .getWallUploadServer(userActor).
                groupId(224698200L)
                .execute()
                .getUploadUrl();

        final byte[] bytes = "binary code".getBytes();
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.LEGACY);
        builder.addBinaryBody("file", bytes, ContentType.DEFAULT_BINARY, "log%s.txt".formatted(LocalDateTime.now()));
        final HttpEntity entity = builder.build();
        final HttpPost httpPost = new HttpPost(uploadLink);
        httpPost.setEntity(entity);

        try (CloseableHttpClient client = HttpClientBuilder.create()
                .build()) {

            final SaveResponse file1 = client.execute(httpPost, response -> {
                final String next = new Scanner(response.getEntity().getContent()).next();
                log.info(next);
                final JsonObject jsonTree = gson.fromJson(next, JsonObject.class);
                final String file = jsonTree.get("file").getAsString();
                final SaveResponse execute;
                try {
                    execute = vkApiClient.docs().save(userActor).file(file).execute();
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                } catch (ClientException e) {
                    throw new RuntimeException(e);
                }
                log.info(execute.toPrettyString());
                return execute;
            });
            return vkApiClient.wall().post(userActor)
                    .ownerId(-224698200L)
                    .attachments(file1.getType().getValue() + file1.getDoc().getOwnerId() + "_" + file1.getDoc().getId())
                    .message(LocalDateTime.now().toString())
                    .executeAsString();
        }


    }

    private String getRandomAccessToken() {
        final List<String> keys = vkCredentials.getGroupAccessToken();
        return keys.get(random.nextInt(keys.size()));
    }

}
