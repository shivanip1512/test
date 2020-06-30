package com.eaton.elements.tabs;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class CommChannelTab extends TabElement {

    public CommChannelTab(DriverExtensions driverExt) {
        super(driverExt);
    }

    public boolean TabDisplayed(String tabName) {
       return getTabPresence(tabName);
    
    }

    public boolean getFieldLabelPresence(String field) {
        WebElement superParentElement = getTabPanel();
        List<WebElement> parentElement = superParentElement.findElements(By.cssSelector(".name"));

        boolean fieldExists = false;
        for (WebElement element : parentElement) {
            fieldExists = element.getText().contains(field);

            if (fieldExists)
                break;
        }

        return fieldExists;
    }

    public boolean getInfoFieldDataPresence(String fieldlabel, String fieldValue) {
        WebElement superParentElement = getTabPanel();
        List<WebElement> parentElement = superParentElement.findElements(By.cssSelector(".name"));
        boolean fieldlabelExists = false;
        boolean fieldValueExists = false;
        for (WebElement element : parentElement) {
            fieldlabelExists = element.getText().contains(fieldlabel);

            if (fieldlabelExists) {
                WebElement elementValue = element.findElement(By.xpath("../td[2]"));
                fieldValueExists = fieldValue.contains(elementValue.getText());
                if (fieldValueExists) {
                    break;
                }
            }
        }

        return fieldValueExists;
    }

    public boolean getConfigFieldDataPresence(String fieldlabel, String fieldValue) {
        String unitOfMeasure;
        if (!fieldlabel.equals("Additional Time Out:") && !fieldlabel.equals("Shared Port Type:")
                && !fieldlabel.equals("Socket Number:")) {
            unitOfMeasure = "ms";
        } else if (fieldlabel.equals("Additional Time Out:")) {
            unitOfMeasure = "sec";
        } else {
            unitOfMeasure = "";
        }

        WebElement superParentElement = getTabPanel();
        List<WebElement> parentElement = superParentElement.findElements(By.cssSelector(".name"));
        boolean fieldlabelExists = false;
        boolean fieldValueExists = false;
        for (WebElement element : parentElement) {
            fieldlabelExists = element.getText().contains(fieldlabel);

            if (fieldlabelExists && !fieldlabel.equals("Shared Port Type:") && !fieldlabel.equals("Socket Number:")) {
                WebElement elementValue = element.findElement(By.xpath("../td[2]"));
                String elementValueText = elementValue.getText();
                String delims = "[ ]+";
                String[] tokens = elementValueText.split(delims);

                if (tokens[0].equals(fieldValue) && tokens[1].equals(unitOfMeasure)) {
                    fieldValueExists = true;
                }

                if (fieldValueExists) {
                    break;
                }
            } else {
                WebElement elementValue = element.findElement(By.xpath("../td[2]"));

                if (fieldValue.contains(elementValue.getText())) {
                    fieldValueExists = true;
                }

                if (fieldValueExists) {
                    break;
                }
            }
        }

        return fieldValueExists;
    }
}
