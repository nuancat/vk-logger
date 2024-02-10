package com.cleanpay.vklogger;

import com.cleanpay.vklogger.configuration.VkCredentials;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SeleniumTest {

    @Autowired
    VkCredentials vkCredentials;

    @Test
    void test() {
        assert StringUtils.isNotBlank(vkCredentials.getEmail());
        assert StringUtils.isNotBlank(vkCredentials.getPassword());

        final String code = browseIt(vkCredentials.getEmail(), vkCredentials.getPassword());
        Assertions.assertThat(code)
                .isNotBlank();
        System.out.println(code);
    }

    public String browseIt(String login, String pass) { // magic
        try {
            var chromeDriverService = new ChromeDriverService.Builder().withSilent(true).build();
            String url = "https://oauth.vk.com/authorize?client_id=%s&display=page&redirect_uri=https://oauth.vk.com/blank.html&group_ids=%s&scope=messages&response_type=code&v=5.131".formatted(vkCredentials.getAppId(), vkCredentials.getGroupId());
            WebDriver driver = new ChromeDriver(chromeDriverService, new ChromeOptions().addArguments("--enable-javascript", "--silent", "--headless")); // (true) включает js// (true) включает js ||| add add "--silent"
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
        }
        return null;
    }

    private String getToken(String url) {
        return url.replaceAll(".+code=", "");
    }
}
