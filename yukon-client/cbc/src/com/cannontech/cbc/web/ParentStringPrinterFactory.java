package com.cannontech.cbc.web;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.core.dao.CBCDao;

public class ParentStringPrinterFactory {
    private CapControlCache capControlCache;
    private CBCDao cbcDao;

    public ParentStringPrinter createParentStringPrinter(final HttpServletRequest request) {
        final ParentStringPrinter printer = new ParentStringPrinter(request);
        printer.setCapControlCache(capControlCache);
        printer.setCbcDao(cbcDao);
        return printer;
    }    

    public void setCapControlCache(CapControlCache capControlCache) {
        this.capControlCache = capControlCache;
    }
    
    public void setCbcDao(CBCDao cbcDao) {
        this.cbcDao = cbcDao;
    }
    
}
