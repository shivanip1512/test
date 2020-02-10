package com.eaton.pages.admin;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.elements.Button;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class UserDetailPage extends PageBase {

    public static final String DEFAULT_URL = Urls.Admin.USERS_AND_GROUPS;
    
    private Button edit;
    private Button changePassword;
    private Button unlockUser;

    public UserDetailPage(WebDriver driver, String pageUrl) {
        super(driver, pageUrl);

        this.requiresLogin = true;
        //pageUrl = DEFAULT_URL;
        
        edit = new Button(this.driver, "Edit", null);
        changePassword = new Button(this.driver, "Change Password", null);
        unlockUser = new Button(this.driver, "Unlock User", null);
    }

    public String getTitle() {

        return this.driver.findElement(By.cssSelector(".page-heading")).getText();
    }    
    
    public Button getEdit() {
        return edit;
    }
    
    public Button getChangePassword() {
        return changePassword;
    }
    
    public Button getUnlockUser() {
        return unlockUser;
    }
}