package com.eaton.elements.modals;

import java.util.Optional;

import org.openqa.selenium.By;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.DatePickerElement;
import com.eaton.framework.DriverExtensions;

public class ResetPeakModal extends BaseModal {

    private DriverExtensions driverExt;

    public ResetPeakModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);

        this.driverExt = driverExt;
    }

    // Trend
    public DropDownElement getResetPeakTo() {
        return new DropDownElement(this.driverExt, "resetPeakDuration");
    }

    public DatePickerElement getDate() {
        return new DatePickerElement(this.driverExt, "startDate");
    }

    public RadioButtonElement getResetPeakForAllTrends() {
        return new RadioButtonElement(this.driverExt, "resetPeakForAllTrends", getModal());
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

    public Boolean helpTextMessageClosed() {
        String classAttribute = getModal().findElement(By.cssSelector(".js-help-text-message")).getAttribute("class");
        Boolean flag = false;
        if (classAttribute.contains("dn")) {
            flag = true;
        }
        return flag;
    }
}
