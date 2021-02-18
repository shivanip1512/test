package com.eaton.pages.tools.trends;

import java.util.Optional;

import org.openqa.selenium.By;

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
    
    public String getResetPeakMessage() {
        return this.driverExt.findElement(By.cssSelector("#reset-peak-success-message .user-message"), Optional.of(3)).getText();
    }

    public ConfirmModal showDeleteTrendModal() {
        getActionBtn().clickAndSelectOptionByText("Delete");

        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("yukon_dialog_confirm");

        return new ConfirmModal(this.driverExt, Optional.empty(), Optional.of("yukon_dialog_confirm"));
    }
    
    public ResetPeakModal showResetPeakTrendModal() {
        String modalTitle = "Reset Peak";
        getActionBtn().clickAndSelectOptionByText(modalTitle);
        
        SeleniumTestSetup.waitUntilModalOpenByTitle(modalTitle);

        return new ResetPeakModal(this.driverExt, Optional.of(modalTitle), Optional.empty());
    }
}