package com.eaton.pages.tools.trends;

import java.util.Optional;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.WebTable;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.ResetPeakModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class TrendsListPage extends PageBase {

    private ActionBtnDropDownElement actionBtn;

    public TrendsListPage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.Tools.TRENDS_LIST;
        actionBtn = new ActionBtnDropDownElement(this.driverExt);
    }

    public WebTable getTable() {
        return new WebTable(driverExt, "compact-results-table");
    }

    public ActionBtnDropDownElement getActionBtn() {
        return actionBtn;
    }

    public ConfirmModal showDeleteTrendModal() {
        getActionBtn().clickAndSelectOptionByText("Delete");

        SeleniumTestSetup.waitUntilModalVisibleByDescribedBy("yukon_dialog_confirm");

        return new ConfirmModal(this.driverExt, Optional.empty(), Optional.of("yukon_dialog_confirm"));
    }
    
    public ResetPeakModal showResetPeakModal() {
        String value = "Reset Peak";
        getActionBtn().clickAndSelectOptionByText(value);
        
        SeleniumTestSetup.waitUntilModalVisibleByTitle(value);
        
        return new ResetPeakModal(driverExt, Optional.of(value), Optional.empty());                
    }
}