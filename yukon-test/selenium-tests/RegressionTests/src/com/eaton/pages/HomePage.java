package com.eaton.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.eaton.elements.MenuElement;

public class HomePage extends PageBase {

    private MenuElement menu;
    private WebDriver driver;

    public HomePage(WebDriver driver, String pageUrl) {
        super(driver, pageUrl);

        this.driver = driver;
        menu = new MenuElement(this.driver);
    }

    public MenuElement getMenu() {
        return menu;
    }

    public String getUtilityUrl(String name) {

        List<WebElement> options = getUtilityUrls();

        String url = null;
        
        for (WebElement option : options) {
            String optionText = option.getAttribute("innerText");
            if (optionText.equals(name)) {
                url = option.getAttribute("href");
                return url;
            }
        }

        return url;
    }

    private List<WebElement> getUtilityUrls() {

        return this.driver.findElements(By.cssSelector(".utility a"));
    }

    public void clickSupport() {
        
        List<WebElement> options = getUtilityUrls();

        for (WebElement option : options) {
            String optionText = option.getAttribute("innerText");
            if (optionText.equals("Support")) {
                option.click();
            }
        }
    }

    public void clickSiteMap() {
        
        List<WebElement> options = getUtilityUrls();

        for (WebElement option : options) {
            String optionText = option.getAttribute("innerText");
            if (optionText.equals("Site Map")) {
                option.click();
            }
        }
    }
}
