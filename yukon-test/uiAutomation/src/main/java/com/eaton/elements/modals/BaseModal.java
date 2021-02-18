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

    /**
     * This is the base modal and it should be inherited from.  It gets the modal and has the methods
     * that are used commonly on all modals.
     * 
     * @param driverExt
     * @param modalTitle - This is an optional field that is used to find the modal by it's title. 
     *                     Use this only if the modal does NOT have an aria-describedby, or if the
     *                     aria-describedby is dynamically generated.  When using send in Optional.empty() for describedBy.
     *                     
     * @param describedBy - This is an optional field that is used to find the modal by aria-describedby.  
     *                      Use this first if the modal uses aria-describedby that is not dynamically generated.  
     *                      When Using send in Optional.empty() for modal title.
     */
    public BaseModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) 
    { 
        this.driverExt = driverExt;

        if (modalTitle.isPresent())
            this.modalTitle = modalTitle.get();
        if (describedBy.isPresent())
            this.describedBy = describedBy.get();
    }    

    /**
     * Use this method to get the modal
     * 
     * @return - returns out a WebElement representing the modal
     */
    public WebElement getModal() {
        if (describedBy != null) {
            return this.driverExt.findElement(By.cssSelector("[aria-describedby='" + this.describedBy + "']"), Optional.of(2));
        }

        Optional<WebElement> found = Optional.empty();

        long startTime = System.currentTimeMillis();

        while (found.isEmpty() && (System.currentTimeMillis() - startTime) < 3000) {

            List<WebElement> elements = this.driverExt.findElements(By.cssSelector(".ui-dialog"), Optional.of(5));

            found = elements.stream().filter(element -> element.findElement(By.cssSelector(".ui-dialog-title")).getText().equals(this.modalTitle)).findFirst();
        }

        return found.get();
    }

    public String getModalTitle() {
        return getModal().findElement(By.cssSelector(".ui-dialog-titlebar .ui-dialog-title")).getText();
    }

    public void clickCloseAndWait() {
        getModal().findElement(By.cssSelector(".ui-dialog-titlebar-close")).click();

        if (describedBy != null) {
            SeleniumTestSetup.waitUntilModalInvisibleByDescribedBy(describedBy);
        } else if (modalTitle != null) {
            SeleniumTestSetup.waitUntilModalClosedByTitle(modalTitle);
        }
    }
    
    public void clickOk() {
        getModal().findElement(By.cssSelector(".ui-dialog-buttonset .primary")).click();
    }

    // TODO need a unique way to select the save button
    public void clickOkAndWaitForModalToClose() {
        WebElement el = getModal().findElement(By.cssSelector(".ui-dialog-buttonset .primary"));
        
        SeleniumTestSetup.moveToElement(el);
        
        el.click();
        
        if (describedBy != null) {
            SeleniumTestSetup.waitUntilModalClosedByDescribedBy(describedBy);
        } else if (modalTitle != null) {
            SeleniumTestSetup.waitUntilModalClosedByTitle(modalTitle);
        }
    }
    
    /**
     *  This method is to be used when you click ok on a modal and need to wait for a validation error
     */
    public void clickOkAndWaitForSpinner() {
        getModal().findElement(By.cssSelector(".ui-dialog-buttonset .primary")).click();
        
        SeleniumTestSetup.waitForLoadingSpinner();
    }
    
    public void clickOkAndWaitForModalCloseDisplayNone() {
        getModal().findElement(By.cssSelector(".ui-dialog-buttonset .primary")).click();
        if (describedBy != null) {
            SeleniumTestSetup.waitUntilModalInvisibleByDescribedBy(describedBy);
        } else if (modalTitle != null) {
            SeleniumTestSetup.waitUntilModalClosedByTitle(modalTitle);
        }                
    }

    /// TODO need a unique way to select the cancel button
    public void clickCancelAndWait() {
        getModal().findElement(By.cssSelector(".ui-dialog-buttonset .js-secondary-action")).click();

        if (describedBy != null) {
            SeleniumTestSetup.waitUntilModalInvisibleByDescribedBy(describedBy);
        } else if (modalTitle != null) {
            SeleniumTestSetup.waitUntilModalClosedByTitle(modalTitle);
        }
    }
    
    /**
     * This method is used on the Confirm Delete Modal in Comm Channel TCP
     */
    public void clickCancelBtnByNameAndWait() {
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
    
    /**This function should be used for values that are read only.  It will take the field Label and will return the corresponding value.
     * @param fieldLabel  : provide label for which you want to fetch corresponding value like 'Name' 
     * @return Value as String if label can not be found will throw a selenium error.
     */
    public String getReadOnlyFieldValueByLabel(String fieldLabel) {
        List<WebElement> rows = getModal().findElements(By.cssSelector("table tr"));
        
        WebElement row = rows.stream().filter(x -> x.findElement(By.cssSelector(".name")).getText().contains(fieldLabel)).findFirst().orElseThrow();
        
        return row.findElement(By.cssSelector(".value")).getText();        
    }
    
    public boolean isModalDisplayed() {
        WebElement modal = getModal();
        String style = modal.getAttribute("style");
        
        return !style.contains("display: none;");            
    }
    
    //Comm Channels are completed removed from dom when running chrome so need to use this specifically for comm channels
    public boolean isModalAvailable() {
        List<WebElement> list = this.driverExt.findElements(By.cssSelector("[aria-describedby='" + this.describedBy + "']"), Optional.of(2));
        
        return !list.isEmpty();
    }
}
