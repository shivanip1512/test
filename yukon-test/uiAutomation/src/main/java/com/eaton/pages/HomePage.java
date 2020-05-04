package com.eaton.pages;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.elements.MenuElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;

public class HomePage extends PageBase {

    private MenuElement menu;
    public static final String DEFAULT_URL = Urls.HOME;

    public HomePage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = DEFAULT_URL;
        menu = new MenuElement(this.driverExt);
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

        return this.driverExt.findElements(By.cssSelector(".utility a"), Optional.empty());
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
    
    public boolean versionDisplayed() {
        List<WebElement> list = this.driverExt.findElements(By.cssSelector(".legal>ul>li"), Optional.empty());
        
        Optional<WebElement> version = list.stream().filter(element -> element.getText().contains("Yukon Version")).findFirst();
        
        return !version.isEmpty();
    }
}
