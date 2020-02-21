package com.eaton.pages;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.ValidUserLogin;

public class LoginPage extends PageBase {
    
    public LoginPage(DriverExtensions driverExt, String pageUrl) {
        super(driverExt, pageUrl);
        
        SeleniumTestSetup.navigateToLoginPage();
        requiresLogin = false;
    }

    public void login() {

        setUserName(ValidUserLogin.getUserName());
        setPassword(ValidUserLogin.getPassword());

        loginClick();
    }

    public void setUserName(String userName) {
        WebElement el = this.driverExt.findElement(By.id("login_email"), Optional.empty());
        el.sendKeys(userName);
    }

    public void setPassword(String password) {
        this.driverExt.findElement(By.id("login_password"), Optional.empty()).sendKeys(password);
    }

    public void loginClick() {
        this.driverExt.findElement(By.name("login"), Optional.empty()).click();
    }
}
