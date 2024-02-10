package com.cleanpay.vklogger.service;

import com.cleanpay.vklogger.configuration.VkCredentials;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.objects.GroupAuthResponse;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VkAutoAuthService {

    private static final String REDIRECT_URL = "https://oauth.vk.com/blank.html";
    private static final String SCOPE = "397381";
    private static final String GROUP_ACCESS_URL = "https://oauth.vk.com/authorize?client_id=%s&display=page&redirect_uri=" + REDIRECT_URL + "&group_ids=%s&scope=" + SCOPE + "&response_type=code&v=5.131";
    private final VkCredentials vkCredentials;
    private final VkApiClient vkApiClient;
    private static GroupActor groupActor;

    @SneakyThrows
    public GroupActor groupActor() {
        if (groupActor != null) {
            return groupActor;
        }
        final String code = browseIt(vkCredentials.getEmail(), vkCredentials.getPassword());
        assert code != null;
        final GroupAuthResponse execute = vkApiClient.oAuth().groupAuthorizationCodeFlow(vkCredentials.getAppId(), vkCredentials.getDefendKey(), REDIRECT_URL, code).execute();
        groupActor = new GroupActor(vkCredentials.getGroupId(), execute.getAccessTokens().values().stream().findFirst().get());
        return groupActor;
    }

    private String browseIt(String login, String pass) { // magic
        var chromeDriverService = new ChromeDriverService.Builder()
                .withSilent(true)
                .build();
        String url = GROUP_ACCESS_URL.formatted(vkCredentials.getAppId(), vkCredentials.getGroupId());
        var driver = new ChromeDriver(chromeDriverService, new ChromeOptions().addArguments("--enable-javascript", "--silent", "--headless")); // (true) включает js// (true) включает js ||| add add "--silent"
        try {
            driver.get(url);
            WebElement loginField = driver.findElement(By.name("email")); // поиск полей для заполнения логина и пароля
            loginField.click(); // фокус на поле логина
            loginField.sendKeys(login); // ввод логина
            WebElement passField = driver.findElement(By.name("pass"));// то же самое с паролем
            passField.click();// фокус на поле password
            passField.sendKeys(pass); // ввод password
            driver.findElement(By.id("install_allow")).click(); // клик по кнопке подтверждения
            // страница с подтверждением прав приложения
            String token;
            if (driver.getCurrentUrl().contains("access_token")) { //если доступ к приложению был раньше разрешен
                token = getToken(driver.getCurrentUrl());
            } else { //если доступ к приложению не был раньше разрешен
                WebElement findElement = driver.findElement(By.xpath("//*[@id=\"oauth_wrap_content\"]/div[3]/div/div[1]/button[1]"));
                findElement.click();
                token = getToken(driver.getCurrentUrl());
            }
            driver.quit();
            return token;
        } catch (NoSuchElementException ex) {
            System.out.println("Косяк в авторизации проблемный аккаунт -- " + login + " : пароль: " + pass);
            driver.quit();
        }
        return null;
    }

    private String getToken(String url) {
        return url.replaceAll(".+code=", "");
    }
}
