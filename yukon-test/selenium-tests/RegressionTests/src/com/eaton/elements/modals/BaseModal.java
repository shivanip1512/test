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
    
    private void setModal() {
        this.modal = this.driver.findElement(By.cssSelector("[aria-describedby='" + this.modalName  + "']"));
    }
    
    protected WebElement getModal() {
        return this.modal;
    }
    
    public String getTitle() {
        return getModal().findElement(By.cssSelector(".ui-dialog-titlebar .ui-dialog-title")).getText();
    }
    
    public void clickClose() {
        getModal().findElement(By.cssSelector(".ui-dialog-titlebar-close")).click();
    }
  
    //TODO need a unique way to select buttons
//    public void clickCancel() {
//        getModal().findElement(By.cssSelector(".ui-dialog-buttonset ")).click();
//    }
//    
//    public void clickOk() {
//        getModal().findElement(By.cssSelector(".ui-dialog-buttonset ")).click();
//    }
}
