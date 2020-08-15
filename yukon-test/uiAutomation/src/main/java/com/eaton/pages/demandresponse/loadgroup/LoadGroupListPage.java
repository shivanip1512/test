package com.eaton.pages.demandresponse.loadgroup;							
							
import com.eaton.framework.DriverExtensions;							
import com.eaton.framework.SeleniumTestSetup;							
import com.eaton.framework.Urls;							
import com.eaton.pages.PageBase;							
import java.util.Optional;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.Button;
import com.eaton.elements.DropDownMultiSelectElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.WebTable;							
import com.eaton.elements.modals.CreateDRObjectModal;							
							
public class LoadGroupListPage extends PageBase {							
    							
    private ActionBtnDropDownElement actionBtn;							
    							
    public LoadGroupListPage(DriverExtensions driverExt) {							
        super(driverExt);							
        							
        requiresLogin = true;		
        pageUrl = Urls.DemandResponse.LOAD_GROUP_SETUP_LIST;
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
    
    public Button getSaveBtn() {
        return new Button(this.driverExt, "Filter");
    }
    
    public DropDownMultiSelectElement getTypes() {
        return new DropDownMultiSelectElement(this.driverExt, "types");
    }
    							
    public CreateDRObjectModal showAndWaitCreateDemandResponseObject() {        							
    	actionBtn.clickAndSelectOptionByText("Create");						
    							
    	SeleniumTestSetup.waitUntilModalOpenByDescribedBy("js-create-dr-objects-popup");						
    							
    	return new CreateDRObjectModal(this.driverExt, Optional.empty(), Optional.of("js-create-dr-objects-popup"));						
    }      							
}							
