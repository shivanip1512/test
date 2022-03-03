package com.eaton.pages.tools.trends;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;

public class TrendEditPage extends TrendPage {
    
    public TrendEditPage(DriverExtensions driverExt, String pageUrl, int id) {
        super(driverExt, pageUrl);
        
        requiresLogin = true;
        this.pageUrl = pageUrl + id + Urls.EDIT;
    }
}
