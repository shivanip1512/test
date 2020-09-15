package com.eaton.elements.modals.virtualdevices;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.elements.SwitchBtnYesNoElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.modals.BaseModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;

public class CreateVirtualDeviceModal extends BaseModal {
    
    private TextEditElement name;

    protected static final String ARIADESCRIBEDBY = "js-create-virtual-device-popup";
    
    public CreateVirtualDeviceModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
        
        name = new TextEditElement(this.driverExt, "name", ARIADESCRIBEDBY);
    }

    public TextEditElement getName() {
    	return name;
    }        
    
    public SwitchBtnYesNoElement getStatus() {
        return new SwitchBtnYesNoElement(this.driverExt, "enable", getModal());
    }
    
    public List<String> getVirtualDeviceModelLabels() {
        List<WebElement> nameElements = getModal().findElements(By.cssSelector("table tr .name"));

        List<String> names = new ArrayList<>();
        for (WebElement element : nameElements) {
            names.add(element.getText());
        }

        return names;
    }
        
    public void virtualDeviceClickCancelAndWait() {
        List<WebElement> list = getModal().findElements(By.cssSelector(".ui-dialog-buttonset button"));

        list.stream().filter(x -> x.getText().contains("Cancel")).findFirst().orElseThrow().click();
        
        SeleniumTestSetup.waitUntilModalClosedDisplayNone(ARIADESCRIBEDBY);
    }
}
