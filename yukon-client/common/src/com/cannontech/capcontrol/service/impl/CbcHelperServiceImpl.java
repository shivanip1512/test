package com.cannontech.capcontrol.service.impl;

import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.capcontrol.service.CbcHelperService;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.vendor.VendorSpecificSqlBuilder;
import com.cannontech.database.vendor.VendorSpecificSqlBuilderFactory;

public class CbcHelperServiceImpl implements CbcHelperService {
    public static final String DEFAULT_FIXED_TEXT = "Fixed";
    public static Pattern logicalPointPattern = Pattern.compile("^\\*Logical<.*> ");

    private RolePropertyDao rolePropertyDao;
    private VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory;
    @Autowired public YukonJdbcTemplate jdbcTemplate;

    @Autowired
    public CbcHelperServiceImpl(VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory) {
        this.vendorSpecificSqlBuilderFactory = vendorSpecificSqlBuilderFactory;
    }
    
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
    public int getControlPointIdForCbc(int controlDeviceID) {
        VendorSpecificSqlBuilder builder = vendorSpecificSqlBuilderFactory.create();

        CbcQueryHelper.appendDeviceControlPointQuery(controlDeviceID, builder);
        
        try {
            return jdbcTemplate.queryForInt(builder);
        } catch(IncorrectResultSizeDataAccessException ex) {
            throw new NotFoundException("Control point not found for control device ID " + controlDeviceID, ex);
        }
    }
    
    @Override
    public Integer findControlPointIdForCbc(int controlDeviceID) {
        try {
            return getControlPointIdForCbc(controlDeviceID);
        } catch(NotFoundException ex) {
            return null;
        }
    }
    
    @Override
    public SqlFragmentSource getOrphanSql() {
        VendorSpecificSqlBuilder builder = vendorSpecificSqlBuilderFactory.create();

        CbcQueryHelper.appendOrphanQuery(builder);
        
        return builder;
    }
}
