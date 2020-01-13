package com.eaton.pages.capcontrol.orphans;

import org.openqa.selenium.WebDriver;

import com.eaton.elements.WebTable;
import com.eaton.pages.PageBase;

public class SubstationListPage extends PageBase {

    private WebTable table;

    public SubstationListPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);

        setTable(new WebTable(driver, "compact-results-table"));
    }

    public WebTable getTable() {
        return this.table;
    }

    private void setTable(WebTable table) {
        this.table = table;
    }

}
