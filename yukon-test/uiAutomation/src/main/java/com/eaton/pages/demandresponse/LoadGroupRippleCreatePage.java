package com.eaton.pages.demandresponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.SwitchBtnMultiSelectElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;

public class LoadGroupRippleCreatePage extends LoadGroupCreatePage {

    public LoadGroupRippleCreatePage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_CREATE;
    }

    public DropDownElement getShedTime() {
        return new DropDownElement(this.driverExt, "shedTime");
    }

    public DropDownElement getGroup() {
        return new DropDownElement(this.driverExt, "group");
    }

    public DropDownElement getAreaCode() {
        return new DropDownElement(this.driverExt, "areaCode");
    }

    public SwitchBtnMultiSelectElement getControlSwitchElement() {
        WebElement section = getPageSection("Double Orders").getSection();

        return new SwitchBtnMultiSelectElement(this.driverExt, "js-control-address", section);
    }

    public List<String> getControlLabels() {
        List<WebElement> controlLabels = this.driverExt.findElements(By.cssSelector("#control ~.js-value"), Optional.empty());
        List<String> names = new ArrayList<>();

        for (WebElement element : controlLabels) {
            names.add(element.getText());
        }
        return names;
    }
    
    public List<String> getRestoreLabels() {
        List<WebElement> restoreLabels = this.driverExt.findElements(By.cssSelector("#restore ~.js-value"), Optional.empty());
        List<String> names = new ArrayList<>();

        for (WebElement element : restoreLabels) {
            names.add(element.getText());
        }
        return names;
    }

    public SwitchBtnMultiSelectElement getRestoreSwitchElement() {
        WebElement section = getPageSection("Double Orders").getSection();

        return new SwitchBtnMultiSelectElement(this.driverExt, "js-restore-address", section);
    }
}
