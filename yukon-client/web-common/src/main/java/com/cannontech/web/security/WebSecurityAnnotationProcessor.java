package com.cannontech.web.security;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;

import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.web.security.annotation.CheckCparm;
import com.cannontech.web.security.annotation.CheckEnergyCompanySetting;
import com.cannontech.web.security.annotation.CheckFalseRoleProperty;
import com.cannontech.web.security.annotation.CheckGlobalSetting;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;


public class WebSecurityAnnotationProcessor {

    @Autowired private WebSecurityChecker webSecurityChecker;

    public void processMethod(Method method) throws Exception {
        check(AnnotationUtils.findAnnotation(method, CheckCparm.class));
        check(AnnotationUtils.findAnnotation(method, CheckRole.class));
        check(AnnotationUtils.findAnnotation(method, CheckGlobalSetting.class));
        check(AnnotationUtils.findAnnotation(method, CheckEnergyCompanySetting.class));
        check(AnnotationUtils.findAnnotation(method, CheckRoleProperty.class));
        check(AnnotationUtils.findAnnotation(method, CheckFalseRoleProperty.class));
    }

    public void processClass(Class<?> clazz) throws Exception {
        check(AnnotationUtils.findAnnotation(clazz, CheckCparm.class));
        check(AnnotationUtils.findAnnotation(clazz, CheckRole.class));
        check(AnnotationUtils.findAnnotation(clazz, CheckGlobalSetting.class));
        check(AnnotationUtils.findAnnotation(clazz, CheckEnergyCompanySetting.class));
        check(AnnotationUtils.findAnnotation(clazz, CheckRoleProperty.class));
        check(AnnotationUtils.findAnnotation(clazz, CheckFalseRoleProperty.class));
    }

    private void check(CheckCparm annotation) {
        if (annotation != null) {
            MasterConfigBooleanKeysEnum configKey = annotation.value();
            boolean expecting = annotation.expecting();
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
}
