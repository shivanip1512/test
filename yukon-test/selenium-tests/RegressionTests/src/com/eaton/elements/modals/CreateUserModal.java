package com.eaton.elements.modals;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;

public class CreateUserModal  {

    private TextEditElement userName;
    private DropDownElement authentication;
    private TextEditElement password;
    private TextEditElement confirmPassword;
    private DropDownElement userGroup;
    private DropDownElement energyCompany;
    private TrueFalseCheckboxElement status;
    private String modalName;    
    private WebElement modal;
    private DriverExtensions driverExt;

    public CreateUserModal(DriverExtensions driverExt, String modalName) {
        this.modalName = modalName;
        this.driverExt = driverExt;
        modal = getModal();
        
        userName = new TextEditElement(driverExt, "username", modal);
        authentication = new DropDownElement(driverExt, "authCategory", modal);
        password = new TextEditElement(driverExt, "password.password", modal);
        confirmPassword = new TextEditElement(driverExt, "password.confirmPassword", modal);
        userGroup = new DropDownElement(driverExt, "userGroupId", modal);
        energyCompany = new DropDownElement(driverExt, "energyCompanyId", modal);
        status = new TrueFalseCheckboxElement(driverExt, "enabled", modal);
    }
    
    public WebElement getModal() {                 
        Optional<WebElement> found = Optional.empty();

        long startTime = System.currentTimeMillis();                
        
        while (found.isEmpty() && System.currentTimeMillis() - startTime < 5000) {
            
            List<WebElement> elements = this.driverExt.findElements(By.cssSelector(".ui-dialog"), Optional.of(0));
            
            found = elements.stream().filter(element -> element.findElement(By.cssSelector(".ui-dialog-title")).getText().equals(this.modalName)).findFirst();
        }
        
        return found.get();
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
    
    public TrueFalseCheckboxElement getStatus() {
        return status;
    }
    
    public String getModalTitle() {
        return modal.findElement(By.cssSelector(".ui-dialog-titlebar .ui-dialog-title")).getText();
    }

    public void clickClose() {
        modal.findElement(By.cssSelector(".ui-dialog-titlebar-close")).click();
        
        SeleniumTestSetup.waitUntilModalClosedByTitle(this.modalName);
    }

    // TODO need a unique way to select the save button
    public void clickOk() {
        modal.findElement(By.cssSelector(".ui-dialog-buttonset .primary")).click();
    }

    ///TODO need a unique way to select the cancel button
    public void clickCancel() {
        modal.findElement(By.cssSelector(".ui-dialog-buttonset .js-secondary-action")).click();
    }
}
