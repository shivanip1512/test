package com.cannontech.cbc.web;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.cbc.dao.ZoneDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;

public class ParentStringPrinterFactory {
    private PointDao pointDao;
    private PaoDao paoDao;
    private ZoneDao zoneDao;
    
    public ParentStringPrinter createParentStringPrinter(final HttpServletRequest request) {
        final ParentStringPrinter printer = new ParentStringPrinter(request);
        printer.setPointDao(pointDao);
        printer.setPaoDao(paoDao);
        printer.setZoneDao(zoneDao);
        return printer;
    }    

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
    
    public void setZoneDao(ZoneDao zoneDao) {
        this.zoneDao = zoneDao;
    }
    
}
