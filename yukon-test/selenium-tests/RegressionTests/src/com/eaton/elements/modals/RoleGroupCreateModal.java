package com.eaton.elements.modals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.elements.MultiLineTextElement;
import com.eaton.elements.TextEditElement;

public class RoleGroupCreateModal extends BaseModal {

    private WebDriver driver;
    private TextEditElement name;
    private MultiLineTextElement description;
    
    // TODO no unique way to get modal
    // TODO cancel and save buttons do not have a unique way to select them
    
    public RoleGroupCreateModal(WebDriver driver, String modalName) {
        super(driver, modalName);
        
        this.driver = driver;
        
        name = new TextEditElement(this.driver, "username", "");
        description = new MultiLineTextElement(this.driver, "password.password", "");
    }  
    
    public TextEditElement geName() {
        return name;
    }        
    
    public MultiLineTextElement getDescription() {
        return description;
    } 
    
    //TODO this a work around until the buttons are updated to follow the Button class pattern
    public void clickSave() {
        getModal().findElement(By.cssSelector(".primary")).click();
    }
    
    //TODO this a work around until the buttons are updated to follow the Button class pattern
    public void clickCancel() {
        getModal().findElement(By.cssSelector(".js-secondary-action")).click();
    }
}
