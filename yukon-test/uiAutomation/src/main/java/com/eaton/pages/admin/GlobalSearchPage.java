package com.eaton.pages.admin;

import com.eaton.elements.SearchBoxElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class GlobalSearchPage extends PageBase {
    
    private SearchBoxElement searchBox;

    public GlobalSearchPage(DriverExtensions driverExt) {
        super(driverExt);

        searchBox = new SearchBoxElement(this.driverExt, "toolbar", "q");
        requiresLogin = true;
        pageUrl = Urls.HOME;
    }

    public SearchBoxElement getSearchBoxElement() {
        return searchBox;
    }
}
