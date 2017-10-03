package com.cannontech.capcontrol.service.impl;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    
    @Override
    public void trimLogicalPointName(String pointName, Consumer<String> pointNameCallback, Consumer<String> deviceNameCallback) {
        Pattern pattern = Pattern.compile("^\\*Logical<.*>");
        Matcher matcher = pattern.matcher(pointName);
        if (matcher.find()) {
            String prefix = matcher.group(0);
            pointNameCallback.accept(pointName.replace(prefix, ""));
            if (deviceNameCallback != null) {
                deviceNameCallback.accept(prefix.substring(9, prefix.length() - 1));
            }
        }
    }
    
    @Override
    public void updateLogicalPointName(String oldPointName, String newPointName, Consumer<String> pointNameCallback) {
        Pattern pattern = Pattern.compile("^\\*Logical<.*>");
        Matcher matcher = pattern.matcher(oldPointName);
        if (matcher.find()) {
            String prefix = matcher.group(0);
            pointNameCallback.accept(prefix+newPointName);
        }
    }
}
