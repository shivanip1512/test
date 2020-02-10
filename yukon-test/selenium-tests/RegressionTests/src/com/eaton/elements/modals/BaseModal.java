package com.eaton.elements.modals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class BaseModal {

    private WebDriver driver;
    private String modalName;
    protected WebElement modal;

    public BaseModal(WebDriver driver, String modalName) {
        this.driver = driver;
        this.modalName = modalName;
    }    
    
    //TODO this is not a unique way to select the modal.  On the new user, new user group and new role group the describedby changes 
    protected WebElement getModal() {
        this.modal = this.driver.findElement(By.cssSelector("[aria-describedby='" + this.modalName  + "']"));
        
        return this.modal;
    }
    
    public String getTitle() {
        return getModal().findElement(By.cssSelector(".ui-dialog-titlebar .ui-dialog-title")).getText();
    }
    
    public void clickClose() {
        getModal().findElement(By.cssSelector(".ui-dialog-titlebar-close")).click();
    }
  
    //TODO need a unique way to select buttons
    public void clickOk() {
        getModal().findElement(By.cssSelector(".ui-dialog-buttonset .primary")).click();
    }
//    
//    public void clickCancel() {
//        getModal().findElement(By.cssSelector(".ui-dialog-buttonset ")).click();
//    }
}
