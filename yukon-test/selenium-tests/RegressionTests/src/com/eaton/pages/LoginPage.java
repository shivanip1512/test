package com.eaton.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.framework.ValidUserLogin;

public class LoginPage extends PageBase {

    public LoginPage(WebDriver driver, String pageUrl) {
        super(driver, pageUrl);

        driver.navigate().to(pageUrl);
        requiresLogin = false;
    }

    public void login() {

        setUserName(ValidUserLogin.getUserName());
        setPassword(ValidUserLogin.getPassword());

        loginClick();
    }

    public void setUserName(String userName) {
        driver.findElement(By.id("login_email")).sendKeys(userName);
    }

    public void setPassword(String password) {
        driver.findElement(By.id("login_password")).sendKeys(password);
    }

    public void loginClick() {
        driver.findElement(By.name("login")).click();
    }
}