package com.eaton.elements.modals;

import java.util.Optional;

import org.openqa.selenium.By;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.DatePickerElement;
import com.eaton.framework.DriverExtensions;

public class ResetPeakModal extends BaseModal {

    private DriverExtensions driverExt;

    private DropDownElement resetPeakTo;
    private DatePickerElement date;
    private RadioButtonElement resetPeakForAllTrends;

    public ResetPeakModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);

        this.driverExt = driverExt;
        
        resetPeakTo = new DropDownElement(this.driverExt, "resetPeakDuration");
        date = new DatePickerElement(this.driverExt, "startDate");
        resetPeakForAllTrends = new RadioButtonElement(this.driverExt, "resetPeakForAllTrends", getModal());
    }

    // Trend
    public DropDownElement getResetPeakTo() {
        return resetPeakTo;
    }
    
    public DatePickerElement getDate() {
        return date;
    }

    public RadioButtonElement getResetPeakForAllTrends() {
        return resetPeakForAllTrends;
    }

    public void clickHelpIcon() {
        this.driverExt.findElement(By.cssSelector(".icon-help"), Optional.empty()).click();
    }

    public void clickHelpCloseIcon() {
        getModal().findElement(By.cssSelector(".icon-close-x")).click();
    }

    public String getHelpTextMessage() {
        return this.driverExt.findElement(By.cssSelector(".js-help-text-message"), Optional.empty()).getText();
    }

    public Boolean isHelpClosed() {
        String classAttribute = getModal().findElement(By.cssSelector(".js-help-text-message")).getAttribute("class");

        return classAttribute.contains("dn");
    }
}
