package com.eaton.elements;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.util.Strings;

import com.eaton.framework.DriverExtensions;

public class SwitchBtnMultiSelectElement {

    private DriverExtensions driverExt;
    private String elementName;
    private WebElement parentElement;

    public SwitchBtnMultiSelectElement(DriverExtensions driverExt, String elementName) {
        this.driverExt = driverExt;
        this.elementName = elementName;
    }

    public SwitchBtnMultiSelectElement(DriverExtensions driverExt, String elementName, WebElement parentElement) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentElement = parentElement;
    }    
    
    public void setTrueFalseByLabel(String label, String id, boolean checked) {
        WebElement switchElement = getSwitchBtn();
        
        List<WebElement> buttons = switchElement.findElements(By.cssSelector(".switch-btn"));
        
        WebElement el = buttons.stream().filter(x -> x.findElement(By.cssSelector(".b-label")).getText().contains(label)).findFirst().orElseThrow();
        
        WebElement switchButton = el.findElement(By.cssSelector("input[id='" + id + "_chk']"));
        
        String isChecked = switchButton.getAttribute("checked");
        
        if ((isChecked == null && checked) || (isChecked != null && !checked)) {
            el.findElement(By.cssSelector(".b-label")).click();
        }
    }

    public boolean isValueSelected(String name) {
        WebElement element = getSwitchBtn();

        WebElement switchBtn;
        if (name.contains("Load_")) {
            switchBtn = element.findElement(By.cssSelector("input[id='" + name + "_chk']"));
        } else {
            switchBtn = element.findElement(By.cssSelector("input[id='" + name.toUpperCase() + "_chk']"));
        }

        String isChecked = switchBtn.getAttribute("checked");

        return !Strings.isNullOrEmpty(isChecked);
    }

    // This method should only be used for Load Group of type Ripple
    public void setTrueFalseByBitNo(int bitNo, boolean checked) {
        WebElement switchElement = getSwitchBtn();
        WebElement switchButton = getSwitchBtnByBitNo(bitNo);
        int switchButtonIdIndex = 0;
        if (bitNo >= 17 && bitNo < 33) {
            switchButtonIdIndex = (bitNo - 17) * 2 + 1;
        } else if (bitNo >= 1 && bitNo < 16) {
            switchButtonIdIndex = bitNo * 2;
        } else
            switchButtonIdIndex = bitNo - 1;
        WebElement switchBtn = switchElement
                .findElement(By.cssSelector("input[id='" + elementName + "-chkbx_" + switchButtonIdIndex + "']"));

        String isChecked = switchBtn.getAttribute("checked");

        if ((isChecked == null && checked) || (isChecked != null && !checked)) {
            switchButton.click();
        }
    }

    public boolean isValueDisabled(String name) {
        WebElement element = getSwitchBtn();

        WebElement switchBtn = element.findElement(By.cssSelector("input[value='" + name.toUpperCase() + "']"));

        String disabled = switchBtn.getAttribute("disabled");

        return !Strings.isNullOrEmpty(disabled);
    }

    public boolean allValuesDisabled() {
        WebElement element = getSwitchBtn();

        List<WebElement> list = element.findElements(By.cssSelector("label .switch-btn-checkbox"));

        boolean allDisabled = true;
        for (WebElement webElement : list) {
            String disabled = webElement.getAttribute("disabled");

            if (!disabled.equals("true")) {
                return false;
            }
        }

        return allDisabled;
    }

    private WebElement getSwitchBtn() {
        if (this.parentElement != null) {
            return this.parentElement.findElement(By.cssSelector("." + this.elementName));
        } else {
            return this.driverExt.findElement(By.cssSelector("." + this.elementName), Optional.of(3));
        }
    }

    private WebElement getSwitchBtnByName(String switchName) {
        WebElement switchbtn = getSwitchBtn();
        List<WebElement> switchElements = switchbtn.findElements(By.cssSelector(".button .b-label"));

        return switchElements.stream().filter(x -> x.getText().contains(switchName)).findFirst().orElseThrow();
    }

    private WebElement getSwitchBtnByBitNo(int bitNo) {
        WebElement switchbtn = getSwitchBtn();
        List<WebElement> switchElements = switchbtn.findElements(By.cssSelector(".button .b-label"));

        return switchElements.get(bitNo - 1);
    }

    public int getSwitchCount() {
        WebElement switchbtn = getSwitchBtn();
        List<WebElement> switchElements = switchbtn.findElements(By.cssSelector(".button .b-label"));

        return switchElements.size();
    }
    
    public String getSwitchButtonValueString(String switchButtonId) {
        return this.driverExt.findElement(By.cssSelector("#"+switchButtonId), Optional.empty()).getAttribute("value");
    }
}
