package com.eaton.elements.modals;

import java.util.Optional;

import org.openqa.selenium.By;

import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;

public class TestEmailModal  extends BaseModal {

    public TestEmailModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);

    }
    
    public TextEditElement getEmailAddress( ) {
        return new TextEditElement(this.driverExt, "to", getModal());
    }

    public String getMsg() {
        return getModal().findElement(By.cssSelector("#adminSetup-testEmail-popup>div")).getText();
    }
}
