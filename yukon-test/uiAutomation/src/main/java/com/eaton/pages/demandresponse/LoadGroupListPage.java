package com.eaton.pages.demandresponse;							
							
import com.eaton.framework.DriverExtensions;							
import com.eaton.framework.SeleniumTestSetup;							
import com.eaton.framework.Urls;							
import com.eaton.pages.PageBase;							
import java.util.Optional;

import org.openqa.selenium.By;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.Button;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.WebTable;							
import com.eaton.elements.modals.CreateCommChannelModal;
import com.eaton.elements.modals.CreateDRObjectModal;							
							
public class LoadGroupListPage extends PageBase {							
    							
    private ActionBtnDropDownElement actionBtn;							
    							
    public LoadGroupListPage(DriverExtensions driverExt) {							
        super(driverExt);							
        							
        requiresLogin = true;							
        pageUrl = Urls.Assets.COMM_CHANNELS_LIST;							
        actionBtn = new ActionBtnDropDownElement(this.driverExt);							
    }							
							
    public WebTable getTable() {							
        return new WebTable(driverExt, "compact-results-table");							
    }							
    							
    public ActionBtnDropDownElement getActionBtn() {							
        return actionBtn;							
    }    	
    
    public TextEditElement getName() {
        return new TextEditElement(this.driverExt, "name");
    }
    public String getUserMessage() {
        return this.driverExt.findElement(By.cssSelector("span.empty-list"), Optional.of(2)).getText();
    }
    
    public Button getSaveBtn() {
        return new Button(this.driverExt, "Filter");
    }
    							
    public CreateDRObjectModal showAndWaitCreateDemandResponseObject() {        							
    	actionBtn.clickAndSelectOptionByText("Create");						
    							
    	SeleniumTestSetup.waitUntilModalVisibleByTitle("Create Demand Response Object");						
    							
    	return new CreateDRObjectModal(this.driverExt, Optional.of("Create Demand Response Object"), Optional.empty());						
    }      							
}							
