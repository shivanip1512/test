package com.cannontech.web.security;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.config.MasterConfigLicenseKey;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.web.security.annotation.CheckAccessLevel;
import com.cannontech.web.security.annotation.CheckCparm;
import com.cannontech.web.security.annotation.CheckCparmString;
import com.cannontech.web.security.annotation.CheckEnergyCompanySetting;
import com.cannontech.web.security.annotation.CheckFalseRoleProperty;
import com.cannontech.web.security.annotation.CheckGlobalSetting;
import com.cannontech.web.security.annotation.CheckGlobalSettingStringExist;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;


public class WebSecurityAnnotationProcessor {

    @Autowired private WebSecurityChecker webSecurityChecker;

    public void processMethod(Method method) throws Exception {
        check(AnnotationUtils.findAnnotation(method, CheckCparm.class));
        check(AnnotationUtils.findAnnotation(method, CheckCparmString.class));
        check(AnnotationUtils.findAnnotation(method, CheckRole.class));
        check(AnnotationUtils.findAnnotation(method, CheckGlobalSetting.class));
        check(AnnotationUtils.findAnnotation(method, CheckGlobalSettingStringExist.class));
        check(AnnotationUtils.findAnnotation(method, CheckEnergyCompanySetting.class));
        check(AnnotationUtils.findAnnotation(method, CheckRoleProperty.class));
        check(AnnotationUtils.findAnnotation(method, CheckFalseRoleProperty.class));
        check(AnnotationUtils.findAnnotation(method, CheckPermissionLevel.class));
        check(AnnotationUtils.findAnnotation(method, CheckAccessLevel.class));
    }

    public void processClass(Class<?> clazz) throws Exception {
        check(AnnotationUtils.findAnnotation(clazz, CheckCparm.class));
        check(AnnotationUtils.findAnnotation(clazz, CheckCparmString.class));
        check(AnnotationUtils.findAnnotation(clazz, CheckRole.class));
        check(AnnotationUtils.findAnnotation(clazz, CheckGlobalSetting.class));
        check(AnnotationUtils.findAnnotation(clazz, CheckGlobalSettingStringExist.class));
        check(AnnotationUtils.findAnnotation(clazz, CheckEnergyCompanySetting.class));
        check(AnnotationUtils.findAnnotation(clazz, CheckRoleProperty.class));
        check(AnnotationUtils.findAnnotation(clazz, CheckFalseRoleProperty.class));
        check(AnnotationUtils.findAnnotation(clazz, CheckPermissionLevel.class));
        check(AnnotationUtils.findAnnotation(clazz, CheckAccessLevel.class));
    }

    private void check(CheckCparm annotation) {
        if (annotation != null) {
            MasterConfigBoolean configKey = annotation.value();
            boolean expecting = annotation.expecting();
            webSecurityChecker.authorizeByCparm(configKey, expecting);
        }
    }

    private void check(CheckCparmString annotation) {
        if (annotation != null) {
            MasterConfigString configKey = annotation.config();
            MasterConfigLicenseKey expecting = annotation.expecting();
            webSecurityChecker.authorizeByCparm(configKey, expecting);
        }
    }
    
    private void check(CheckRole annotation) {
        if (annotation != null) {
            YukonRole[] roles = annotation.value();
            webSecurityChecker.checkRole(roles);
        }
    }

    private void check(CheckGlobalSetting annotation) {
        if (annotation != null) {
            GlobalSettingType setting = annotation.value();
            webSecurityChecker.checkGlobalSetting(setting);
        }
    }

    private void check(CheckGlobalSettingStringExist annotation) {
        if (annotation != null) {
            GlobalSettingType setting = annotation.value();
            webSecurityChecker.checkGlobalSettingStringExist(setting);
        }
    }
    
    private void check(CheckEnergyCompanySetting annotation) {
        if (annotation != null) {
            EnergyCompanySettingType setting = annotation.value();
            webSecurityChecker.checkEnergyCompanySetting(setting);
        }
    }

    private void check(CheckRoleProperty annotation) {
        if (annotation != null) {
            YukonRoleProperty[] roleProperties = annotation.value();
            boolean requireAll = annotation.requireAll();
            webSecurityChecker.checkRoleProperty(requireAll,roleProperties);
        }
    }

    private void check(CheckFalseRoleProperty annotation) {
        if (annotation != null) {
            YukonRoleProperty[] roleProperties = annotation.value();
            webSecurityChecker.checkFalseRoleProperty(roleProperties);
         }
    }
    
    private void check(CheckPermissionLevel annotation) {
        if (annotation != null) {
            webSecurityChecker.checkLevel(annotation.property(), annotation.level());
         }
    }
    
    private void check(CheckAccessLevel annotation) {
        if (annotation != null) {
            webSecurityChecker.checkAccessLevel(annotation.property(), annotation.level());
         }
    }
}
