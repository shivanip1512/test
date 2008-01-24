package com.cannontech.cbc.web;

import org.apache.commons.lang.Validate;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.util.CBCUtils;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.yukon.cbc.CBCArea;
import com.cannontech.yukon.cbc.StreamableCapObject;

public class ParentStringPrinter {

    public static final String ORPH_STRING = "---";
    private CapControlCache cbcCache;
    private boolean linkedToEditors = false;

    public ParentStringPrinter(CapControlCache cbcCache) {
        this.cbcCache = cbcCache;
    }

    public String printPAO(Integer id) {
        StreamableCapObject obj = getStreamable(id);
        Integer parentID = null;
        if (obj instanceof CBCArea)
            return ORPH_STRING;
        if (obj != null) {
            parentID = obj.getParentID();
        } else {
            parentID = getCapBankForController(id);
        }
        return printHierarchy(parentID);

    }

    private StreamableCapObject getStreamable(Integer id) {
    	Validate.notNull(id,"id cannot be null");
    	return cbcCache.getCapControlPAO(id);
    }

    private String printHierarchy(int id) {
        StreamableCapObject obj = getStreamable(id);
        if (obj != null) {
            if (obj instanceof CBCArea) {
                CBCArea area = (CBCArea) obj;
                String areaName = area.getPaoName();
                areaName = (linkedToEditors) ? addLinkToEditor(areaName, area.getPaoID()) :  areaName;
                return areaName;
            } else {
                String regName = obj.getCcName();
                regName = (linkedToEditors) ?  addLinkToEditor(regName, obj.getCcId()) :  regName;
                return printHierarchy(obj.getParentID()) + ":" + regName;
            }
        } else if (isController(id)) {
            Integer parentID = getCapBankForController(id);
            String controllerName = DaoFactory.getPaoDao().getYukonPAOName(id);
            controllerName = (linkedToEditors) ? addLinkToEditor(controllerName, parentID) : controllerName;
            if (parentID > 0) {
                return printHierarchy(parentID) + ":" + controllerName;
            } else { // buck stops here
                return ORPH_STRING;
            }
        } else {
            return ORPH_STRING;
        }
    }

    private Integer getCapBankForController(int id) {
        Integer parentID = DaoFactory.getCBCDao().getParentForController(id);
        return parentID;
    }

    private boolean isController(int id) {
        return CBCUtils.isController(id);
    }

    public String printPoint(Integer id) {
        Integer parentID = DaoFactory.getCBCDao().getParentForPoint(id);
        if (parentID > 0) {
            return printHierarchy(parentID);
        } else { // buck stops here
            return ORPH_STRING;
        }
    }
    
    /**
     * given a name and an id creates a link to CBC editor
     * @param name
     * @param id
     * @return
     */
    public String addLinkToEditor (String name, Integer id) {
        String url = "/editor/cbcBase.jsf?type=2&itemid=" + id;
        StringBuffer retStr = new StringBuffer("<a href = '");
        retStr.append( url );
        retStr.append("'>");
        retStr.append(name);
        retStr.append("</a>");
        return retStr.toString();
    }

    public boolean isLinkedToEditors() {
        return linkedToEditors;
    }

    public void setLinkedToEditors(boolean linkedToEditors) {
        this.linkedToEditors = linkedToEditors;
    }

}
