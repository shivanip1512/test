package com.eaton.pages.capcontrol.orphans;

import org.openqa.selenium.WebDriver;

import com.eaton.elements.WebTable;
import com.eaton.pages.PageBase;

public class CbcListPage extends PageBase {

    private WebTable table;

    public CbcListPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);

        table = new WebTable(driver, "compact-results-table");
    }

    public WebTable getTable() {
        return this.table;
    }
}
