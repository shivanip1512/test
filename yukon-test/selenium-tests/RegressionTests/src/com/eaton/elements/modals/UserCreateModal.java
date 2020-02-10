package com.eaton.elements.modals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;

public class UserCreateModal extends BaseModal {

    private WebDriver driver;
    private TextEditElement userName;
    private DropDownElement authentication;
    private TextEditElement password;
    private TextEditElement confirmPassword;
    private DropDownElement userGroup;
    private DropDownElement energyCompany;
    private static final String PARENT = "ui-id-8";

    // TODO no unique way to get modal
    // TODO enable/disable is a new kind of checkbox, should this be changed to match the others?
    // TODO cancel and save buttons do not have a unique way to select them

    public UserCreateModal(WebDriver driver, String modalName) {
        super(driver, modalName);

        this.driver = driver;

        userName = new TextEditElement(this.driver, "username", PARENT);
        authentication = new DropDownElement(this.driver, "authCategory", PARENT);
        password = new TextEditElement(this.driver, "password.password", PARENT);
        confirmPassword = new TextEditElement(this.driver, "password.confirmPassword", PARENT);
        userGroup = new DropDownElement(this.driver, "userGroupId", PARENT);
        energyCompany = new DropDownElement(this.driver, "energyCompanyId", PARENT);
    }

    public TextEditElement getUserName() {
        return userName;
    }

    public DropDownElement getAuthentication() {
        return authentication;
    }

    public TextEditElement getPassword() {
        return password;
    }

    public TextEditElement getConfirmPassword() {
        return confirmPassword;
    }

    public DropDownElement getUserGroup() {
        return userGroup;
    }

    public DropDownElement getEnergyCompany() {
        return energyCompany;
    }

    // TODO this a work around until the buttons are updated to follow the Button class pattern
    public void clickSave() {
        getModal().findElement(By.cssSelector(".primary")).click();
    }

    // TODO this a work around until the buttons are updated to follow the Button class pattern
    public void clickCancel() {
        getModal().findElement(By.cssSelector(".js-secondary-action")).click();
    }
}
