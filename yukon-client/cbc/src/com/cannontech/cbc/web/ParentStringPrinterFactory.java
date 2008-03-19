package com.cannontech.cbc.web;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.core.dao.CapControlDao;

public class ParentStringPrinterFactory {
    private CapControlCache capControlCache;
    private CapControlDao cbcDao;

    public ParentStringPrinter createParentStringPrinter(final HttpServletRequest request) {
        final ParentStringPrinter printer = new ParentStringPrinter(request);
        printer.setCapControlCache(capControlCache);
        printer.setCbcDao(cbcDao);
        return printer;
    }    

    public void setCapControlCache(CapControlCache capControlCache) {
        this.capControlCache = capControlCache;
    }
    
    public void setCbcDao(CapControlDao cbcDao) {
        this.cbcDao = cbcDao;
    }
    
}
