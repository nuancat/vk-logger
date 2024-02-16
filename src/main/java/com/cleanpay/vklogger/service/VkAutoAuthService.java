package com.cleanpay.vklogger.service;

import com.cleanpay.vklogger.configuration.VkCredentials;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.UserActor;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class VkAutoAuthService {

    private static final String REDIRECT_URL = "https://oauth.vk.com/blank.html";
    private static final String GROUP_SCOPE = "134623237";
    private static final String USER_SCOPE = "501202911";
    private static final String GROUP_ACCESS_URL = "https://oauth.vk.com/authorize?client_id=%s&display=page&redirect_uri=%s&group_ids=%s&scope=%s&response_type=token";
    private static final String USER_ACCESS_URL = "https://oauth.vk.com/authorize?client_id=%s&display=page&redirect_uri=%s&scope=%s&response_type=token";
    private final VkCredentials vkCredentials;

    @SneakyThrows
    public GroupActor groupActor() {
        final String url = GROUP_ACCESS_URL.formatted(vkCredentials.getAppId(), REDIRECT_URL, vkCredentials.getGroupId(), GROUP_SCOPE);
        final String token = browseIt(vkCredentials.getEmail(), vkCredentials.getPassword(), url);
        GroupActor groupActor = new GroupActor(vkCredentials.getGroupId(), token);
        return groupActor;
    }

    @SneakyThrows
    public UserActor userActor() {
        final String url = USER_ACCESS_URL.formatted(vkCredentials.getAppId(), REDIRECT_URL, USER_SCOPE);
        final String token = browseIt(vkCredentials.getEmail(), vkCredentials.getPassword(), url);
        UserActor userActor = new UserActor(vkCredentials.getVkUserMessagesReceiver(), token);
        return userActor;
    }
    private String browseIt(String login, String pass, String url) { // magic
        var chromeDriverService = new ChromeDriverService.Builder()
                .withSilent(true)
                .build();

        var driver = new ChromeDriver(chromeDriverService, new ChromeOptions().addArguments("--enable-javascript", "--silent")); // (true) включает js// (true) включает js ||| add add "--silent"
        try {
            log.info("print url {}", url);
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
        log.info("url: {}", url);
        final String s = url.replaceAll(".+access_token=", "");
        final String s1 = s.replaceAll("&.+", "");
        return s1;
    }
}
