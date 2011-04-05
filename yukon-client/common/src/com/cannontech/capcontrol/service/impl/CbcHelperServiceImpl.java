package com.cannontech.capcontrol.service.impl;

import com.cannontech.capcontrol.service.CbcHelperService;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;

public class CbcHelperServiceImpl implements CbcHelperService {
    public static final String DEFAULT_FIXED_TEXT = "Fixed";
    
    private RolePropertyDao rolePropertyDao;
    
    @Override
    public String getFixedText(LiteYukonUser yukonUser) {
        String fixedText = DEFAULT_FIXED_TEXT;
        
        //check to see if user is in Cap Bank Display role, a.k.a. CBC_ONELINE_CAP_SETTINGS
        boolean hasRole = rolePropertyDao.checkRole(YukonRole.CBC_ONELINE_CAP_SETTINGS, yukonUser);
        
        if(hasRole) {
            fixedText = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.CAP_BANK_FIXED_TEXT, yukonUser);
        } else {
            CTILogger.warn("User cannot access CAP_BANK_FIXED_TEXT property, using default text: " + DEFAULT_FIXED_TEXT);
        }
        
        return fixedText;
    }
    
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
}
