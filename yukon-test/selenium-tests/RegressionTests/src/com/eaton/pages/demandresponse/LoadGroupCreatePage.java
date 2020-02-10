package com.eaton.pages.demandresponse;

import org.openqa.selenium.WebDriver;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.pages.PageBase;

public class LoadGroupCreatePage extends PageBase {

    private TextEditElement name;
    private DropDownElement type;
    private Button save;
    private Button cancel;
    
    public LoadGroupCreatePage(WebDriver driver, String pageUrl) {
        super(driver, pageUrl);

        name = new TextEditElement(this.driver, "name", null);
        type = new DropDownElement(this.driver, "type", null);
        save = new Button(this.driver, "Save", null);
        cancel = new Button(this.driver, "Cancel", null);
    }
    
    public TextEditElement getName() {
        return name;
    }
    
    public DropDownElement getType() {
        return type;
    }
    
    public Button getSave() {
        return save;
    }
    
    public Button getCancel() {
        return cancel;
    }
}
