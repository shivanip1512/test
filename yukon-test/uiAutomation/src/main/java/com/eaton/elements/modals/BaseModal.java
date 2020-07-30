package com.eaton.elements.modals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;

public class BaseModal {

    protected DriverExtensions driverExt;
    private String modalTitle;
    private String describedBy;

    public BaseModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        this.driverExt = driverExt;

        if (modalTitle.isPresent())
            this.modalTitle = modalTitle.get();
        if (describedBy.isPresent())
            this.describedBy = describedBy.get();
    }    

    public WebElement getModal() {
        if (describedBy != null) {
            return this.driverExt.findElement(By.cssSelector("[aria-describedby*='" + this.describedBy + "']"), Optional.of(2));
        }

        Optional<WebElement> found = Optional.empty();

        long startTime = System.currentTimeMillis();

        while (found.isEmpty() && System.currentTimeMillis() - startTime < 3000) {

            List<WebElement> elements = this.driverExt.findElements(By.cssSelector(".ui-dialog"), Optional.of(5));

            found = elements.stream()
                    .filter(element -> element.findElement(By.cssSelector(".ui-dialog-title")).getText().equals(this.modalTitle))
                    .findFirst();
        }

        return found.get();
    }

    public String getModalTitle() {
        return getModal().findElement(By.cssSelector(".ui-dialog-titlebar .ui-dialog-title")).getText();
    }

    public void clickCloseAndWait() {
        getModal().findElement(By.cssSelector(".ui-dialog-titlebar-close")).click();

        if (describedBy != null) {
            SeleniumTestSetup.waitUntilModalClosedByDescribedBy(describedBy);
        } else if (modalTitle != null) {
            SeleniumTestSetup.waitUntilModalClosedByTitle(modalTitle);
        }
    }

    // TODO need a unique way to select the save button
    public void clickOkAndWait() {
        getModal().findElement(By.cssSelector(".ui-dialog-buttonset .primary")).click();

        if (describedBy != null) {
            SeleniumTestSetup.waitUntilModalClosedByDescribedBy(describedBy);
        } else if (modalTitle != null) {
            SeleniumTestSetup.waitUntilModalClosedByTitle(modalTitle);
        }
    }

    /// TODO need a unique way to select the cancel button
    public void clickCancelAndWait() {
        getModal().findElement(By.cssSelector(".ui-dialog-buttonset .js-secondary-action")).click();

        if (describedBy != null) {
            SeleniumTestSetup.waitUntilModalClosedByDescribedBy(describedBy);
        } else if (modalTitle != null) {
            SeleniumTestSetup.waitUntilModalClosedByTitle(modalTitle);
        }
    }

    public void clickOkDeleteByClassAndWait() {
        getModal().findElement(By.cssSelector(".ui-dialog-buttonset .primary")).click();

        if (describedBy != null) {
            SeleniumTestSetup.waitUntilModalClosedByDescribedBy(describedBy);
        } else if (modalTitle != null) {
            SeleniumTestSetup.waitUntilModalClosedByTitle(modalTitle);
        }
    }
    
    public void clickCancelByNameAndWait() {
        List<WebElement> list = getModal().findElements(By.cssSelector(".ui-dialog-buttonset button"));
        
        list.stream().filter(x -> x.getText().contains("Cancel")).findFirst().orElseThrow().click();
        
        if (describedBy != null) {
            SeleniumTestSetup.waitUntilModalClosedByDescribedBy(describedBy);
        } else if (modalTitle != null) {
            SeleniumTestSetup.waitUntilModalClosedByTitle(modalTitle);
        }
    }

    public List<String> getFieldLabels() {
        List<WebElement> nameElements = getModal().findElements(By.cssSelector("table tr .name"));

        List<String> names = new ArrayList<>();

        for (WebElement element : nameElements) {
            names.add(element.getText());
        }

        return names;
    }
    
    public Boolean isModalDisplayed() {
        Boolean isDisplayed = driverExt.findElement(By.cssSelector("[aria-describedby='"+describedBy+"']"), Optional.of(0)).isDisplayed();
        return isDisplayed;
    }
    
    public Boolean isModalClosed() {
        WebElement element= this.driverExt.findElement(By.cssSelector("#"+describedBy+""),Optional.empty());
        String styleAttribute = element.getAttribute("style");
        Boolean isModalClosed = false;
        if(styleAttribute.contains("display: none;")) {
            isModalClosed = true;
        } else {
            element= this.driverExt.findElement(By.cssSelector("[aria-describedby='"+describedBy+"']"),Optional.empty());
            styleAttribute = element.getAttribute("style");
            if(styleAttribute.contains("display: none;")) {
                isModalClosed = true;
            }
        }
        return isModalClosed;
    }
}
