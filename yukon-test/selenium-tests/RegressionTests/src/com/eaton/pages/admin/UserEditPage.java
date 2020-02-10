package com.eaton.pages.admin;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class UserEditPage extends PageBase {

    public static final String DEFAULT_URL = Urls.Admin.USERS_AND_GROUPS;
    
    private TextEditElement userName;
    private DropDownElement authentication;
    private DropDownElement userGroup;
    private DropDownElement energyCompany;
    private DropDownElement status;
    private Button save;
    private Button delete;
    private Button cancel;

    public UserEditPage(WebDriver driver, String pageUrl) {
        super(driver, pageUrl);

        this.requiresLogin = true;
        //pageUrl = DEFAULT_URL;
        
        userName = new TextEditElement(this.driver, "username", null);
        authentication = new DropDownElement(this.driver, "authCategory", null);
        userGroup = new DropDownElement(this.driver, "userGroupId", null);
        energyCompany = new DropDownElement(this.driver, "energyCompanyId", null);
        status = new DropDownElement(this.driver, "loginStatus", null);
        save = new Button(this.driver, "Save", null);
        delete = new Button(this.driver, "Delete", null);
        cancel = new Button(this.driver, "Cancel", null);
    }

    public String getTitle() {

        return this.driver.findElement(By.cssSelector(".page-heading")).getText();
    }
    
    public TextEditElement getUserName() {
        return userName;
    }
    
    public DropDownElement getAuthentication() {
        return authentication;
    }

    public DropDownElement getUserGroup() {
        return userGroup;
    }
    
    public DropDownElement getEnergyCompany() {
        return energyCompany;
    }
    
    public DropDownElement getStatus() {
        return status;
    }
    
    public Button getSave() {
        return save;
    }
    
    public Button getCance() {
        return cancel;
    }
    
    public Button getDelete() {
        return delete;
    }
}