package com.eaton.elements.modals;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.elements.MultiLineTextElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;

public class CreateRoleGroupModal {

    private DriverExtensions driverExt;
    private TextEditElement name;
    private MultiLineTextElement description;
    private String modalName; 
    private WebElement modal;
    
    public CreateRoleGroupModal(DriverExtensions driverExt, String modalName) {
        
        this.driverExt = driverExt;
        this.modalName = modalName;
        modal = getModal();
        
        name = new TextEditElement(this.driverExt, "groupName", modal);
        description = new MultiLineTextElement(this.driverExt, "groupDescription", modal);
    }  
    
    protected WebElement getModal() {
        Optional<WebElement> found = Optional.empty();

        long startTime = System.currentTimeMillis();                
        
        while (found.isEmpty() && System.currentTimeMillis() - startTime < 5000) {
            
            List<WebElement> elements = this.driverExt.findElements(By.cssSelector(".ui-dialog"), Optional.of(0));
            
            found = elements.stream().filter(element -> element.findElement(By.cssSelector(".ui-dialog-title")).getText().equals(this.modalName)).findFirst();
        }
        
        return found.get();                
    }
    
    public TextEditElement getName() {
        return name;
    }        
    
    public MultiLineTextElement getDescription() {
        return description;
    }   
    
    public String getModalTitle() {
        return modal.findElement(By.cssSelector(".ui-dialog-titlebar .ui-dialog-title")).getText();
    }

    public void clickClose() {
        modal.findElement(By.cssSelector(".ui-dialog-titlebar-close")).click();
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
