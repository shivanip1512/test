package com.cannontech.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.config.MasterConfigLicenseKey;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.roleproperties.AccessLevel;
import com.cannontech.core.roleproperties.CapControlCommandsAccessLevel;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.web.util.SpringWebUtil;

public class WebSecurityChecker {
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private EnergyCompanySettingDao energyCompanySettingDao;
    @Autowired private EnergyCompanyDao ecDao;
    
    public void authorizeByCparm(MasterConfigBoolean configKey, boolean expecting) {
    	
        boolean result = configurationSource.getBoolean(configKey);
        if (result != expecting) {
            throw new NotAuthorizedException("User is not authorized to access this page.");
        } 
    }
    
    public void authorizeByLicense(MasterConfigLicenseKey license) {
        if (!configurationSource.isLicenseEnabled(license)) {
            throw new NotAuthorizedException("User is not authorized to access this page.");
        } 
    }
    
    public void checkRole(YukonRole... roles) {
        final LiteYukonUser user = getYukonUser();
        
        for (final YukonRole role : roles) {
            boolean hasRole = rolePropertyDao.checkRole(role, user);
            if (hasRole) return;
        }
        
        throw new NotAuthorizedException("User " + user + " is not authorized to access this page.");
    }
    
    public void checkRoleProperty(boolean requireAll, YukonRoleProperty... rolePropertyIds) {
        final LiteYukonUser user = getYukonUser();
        
        // Authorized starts out true before AND operation
        // and false before OR operation
        boolean authorized = requireAll;
        
        for (final YukonRoleProperty property : rolePropertyIds) {
            boolean checkProperty = rolePropertyDao.checkProperty(property, user);
            authorized = requireAll ? authorized && checkProperty : authorized || checkProperty;
        }
        
        if (!authorized) {
            throw new NotAuthorizedException("User " + user + " is not authorized to access this page.");
        }
    }
    
    public void checkGlobalSetting(GlobalSettingType setting) {
    	
        if (!globalSettingDao.getBoolean(setting)) {
            throw new NotAuthorizedException("User is not authorized to access this page.");
        }
        
    }

    public void checkGlobalSettingStringExist(GlobalSettingType setting) {
        
        if (globalSettingDao.getString(setting).isEmpty()) {
            throw new NotAuthorizedException("User is not authorized to access this page.");
        }
        
    }
    
    public void checkFalseRoleProperty(YukonRoleProperty... rolePropertyIds) {
        final LiteYukonUser user = getYukonUser();
        
        for (final YukonRoleProperty property : rolePropertyIds) {
            boolean hasRoleProperty = rolePropertyDao.checkProperty(property, user);
            if (hasRoleProperty) {
            	throw new NotAuthorizedException("User " + user + " is not authorized to access this page.");
            }
        }
    }
    
    public void checkLevel(YukonRoleProperty property, HierarchyPermissionLevel minLevel) {
        rolePropertyDao.verifyLevel(property, minLevel, getYukonUser());
    }
    
    public void checkAccessLevel(YukonRoleProperty property, AccessLevel minLevel) {
        AccessLevel userLevel = rolePropertyDao.getPropertyEnumValue(property, AccessLevel.class, getYukonUser());
        if (userLevel.grantAccess(minLevel)) {
            return;
        }
        throw new NotAuthorizedException("User " + getYukonUser() + " is not authorized to access this page.");

    }
    
    public void checkCapControlCommandsAccessLevel(YukonRoleProperty property, CapControlCommandsAccessLevel[] levels) {
        if (rolePropertyDao.checkAnyLevel(property, levels, getYukonUser())) {
            return;
        }
        throw new NotAuthorizedException("User " + getYukonUser() + " is not authorized to access this page.");
    }
    
    private LiteYukonUser getYukonUser() {
        try {
            LiteYukonUser user = SpringWebUtil.getYukonUser();
            return user;
        } catch (ServletRequestBindingException e) {
            throw new UnsupportedOperationException("Web security checks cannont be done outside of a DispatcherServlet request");
        }
    }

    public void checkEnergyCompanySetting(EnergyCompanySettingType setting) {
        final LiteYukonUser user = getYukonUser();
        YukonEnergyCompany yec = ecDao.getEnergyCompanyByOperator(user);
        
        if (yec == null || !energyCompanySettingDao.getBoolean(setting, yec.getEnergyCompanyId())) {
            throw new NotAuthorizedException("User " + user + " is not authorized to access this page.");
        }
    }

}
