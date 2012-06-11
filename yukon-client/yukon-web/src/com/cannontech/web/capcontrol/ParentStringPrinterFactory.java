package com.cannontech.web.capcontrol;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;

public class ParentStringPrinterFactory {
    private PointDao pointDao;
    private PaoDao paoDao;
    private ZoneDao zoneDao;
    private PaoDefinitionDao paoDefinitionDao;
    
    public ParentStringPrinter createParentStringPrinter(final HttpServletRequest request) {
        final ParentStringPrinter printer = new ParentStringPrinter(request);
        printer.setPointDao(pointDao);
        printer.setPaoDao(paoDao);
        printer.setZoneDao(zoneDao);
        printer.setPaoDefinitionDao(paoDefinitionDao);
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
    
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }
}
