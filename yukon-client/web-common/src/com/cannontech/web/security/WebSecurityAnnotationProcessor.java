package com.cannontech.web.security;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;

import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.web.security.annotation.AuthorizeByCparm;
import com.cannontech.web.security.annotation.CheckFalseRoleProperty;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.security.annotation.CheckGlobalSetting;
import com.cannontech.web.widget.support.WidgetMultiActionController;


public class WebSecurityAnnotationProcessor {
    @Autowired private WebSecurityChecker webSecurityChecker;

    public void process(final Object bean) throws Exception {
        final Class<?> clazz = getClass(bean);

        boolean hasAuthorizeByCparm = hasAuthorizeByCparm(clazz);
        if (hasAuthorizeByCparm) {
            doHasAuthorizeByCparm(getAuthorizeByCparm(clazz));
        }
        
        boolean hasCheckRole = hasCheckRole(clazz);
        if (hasCheckRole) {
            doHasCheckRole(getCheckRole(clazz));
        }
        
        boolean hasCheckGlobalSetting = hasCheckGlobalSetting(clazz);
        if (hasCheckGlobalSetting) {
            doHasCheckGlobalSetting(getCheckGlobalSetting(clazz));
        }
        
        boolean hasCheckRoleProperty = hasCheckRoleProperty(clazz);
        if (hasCheckRoleProperty) {
            doHasCheckRoleProperty(getCheckRoleProperty(clazz));
        }
        
        boolean hasCheckFalseRoleProperty = hasCheckFalseRoleProperty(clazz);
        if (hasCheckFalseRoleProperty) {
            doHasCheckFalseRoleProperty(getCheckFalseRoleProperty(clazz));
        }
    }
    
    private Class<?> getClass(Object bean) {
        if (isProxy(bean)) {
            return AopUtils.getTargetClass(bean);
        }
        
        if (bean instanceof WidgetMultiActionController) {
            WidgetMultiActionController controller = (WidgetMultiActionController) bean;
            Object widgetController = controller.getWidgetController();
            return widgetController.getClass();
        }
        
        return bean.getClass();
    }
    
    private void doHasAuthorizeByCparm(AuthorizeByCparm authorizeByCparm) {
        MasterConfigBooleanKeysEnum configKey = authorizeByCparm.value();
        webSecurityChecker.authorizeByCparm(configKey);
    }
    
    private void doHasCheckRole(CheckRole checkRole) throws Exception {
        YukonRole[] roles = checkRole.value();
        webSecurityChecker.checkRole(roles);
    }

    private void doHasCheckGlobalSetting(CheckGlobalSetting checkGlobalSetting) {
        GlobalSettingType setting = checkGlobalSetting.value();
        webSecurityChecker.checkGlobalSetting(setting);
    }

    private void doHasCheckRoleProperty(CheckRoleProperty checkRoleProperty) throws Exception {
        YukonRoleProperty[] roleProperties = checkRoleProperty.value();
        boolean requireAll = checkRoleProperty.requireAll();
        webSecurityChecker.checkRoleProperty(requireAll,roleProperties);
    }
    
    private void doHasCheckFalseRoleProperty(CheckFalseRoleProperty checkFalseRoleProperty) throws Exception {
        YukonRoleProperty[] roleProperties = checkFalseRoleProperty.value();
        webSecurityChecker.checkFalseRoleProperty(roleProperties);
    }
    
    private AuthorizeByCparm getAuthorizeByCparm(Class<?> clazz) {
        AuthorizeByCparm authorizeByCparm = AnnotationUtils.findAnnotation(clazz, AuthorizeByCparm.class);
        return authorizeByCparm;
    }
    
    private CheckRole getCheckRole(Class<?> clazz) {
        CheckRole checkRole = AnnotationUtils.findAnnotation(clazz, CheckRole.class);
        return checkRole;
    }
    
    private CheckRoleProperty getCheckRoleProperty(Class<?> clazz) {
        CheckRoleProperty checkRoleProperty = AnnotationUtils.findAnnotation(clazz, CheckRoleProperty.class);
        return checkRoleProperty;
    }
    
    private CheckGlobalSetting getCheckGlobalSetting(Class<?> clazz) {
        CheckGlobalSetting checkGlobalSetting = AnnotationUtils.findAnnotation(clazz, CheckGlobalSetting.class);
        return checkGlobalSetting;
    }
    
    private CheckFalseRoleProperty getCheckFalseRoleProperty(Class<?> clazz) {
    	CheckFalseRoleProperty checkFalseRoleProperty = AnnotationUtils.findAnnotation(clazz, CheckFalseRoleProperty.class);
        return checkFalseRoleProperty;
    }
    
    private boolean hasAuthorizeByCparm(Class<?> clazz) {
        AuthorizeByCparm authorizeByCparm = getAuthorizeByCparm(clazz);
        boolean hasAuthorizeByCparm = authorizeByCparm != null;
        return hasAuthorizeByCparm;
    }
    
    private boolean hasCheckRole(Class<?> clazz) {
        CheckRole checkRole = getCheckRole(clazz);
        boolean hasCheckRole = checkRole != null;
        return hasCheckRole;
    }

    private boolean hasCheckGlobalSetting(Class<?> clazz) {
        CheckGlobalSetting checkGlobalSetting = AnnotationUtils.findAnnotation(clazz, CheckGlobalSetting.class);
        return checkGlobalSetting != null;
    }
    
    private boolean hasCheckRoleProperty(Class<?> clazz) {
        CheckRoleProperty checkRoleProperty = getCheckRoleProperty(clazz);
        boolean hasCheckRoleProperty = checkRoleProperty != null;
        return hasCheckRoleProperty;
    }
    
    private boolean hasCheckFalseRoleProperty(Class<?> clazz) {
        CheckFalseRoleProperty checkFalseRoleProperty = getCheckFalseRoleProperty(clazz);
        boolean hasCheckFalseRoleProperty = checkFalseRoleProperty != null;
        return hasCheckFalseRoleProperty;
    }

    private boolean isProxy(Object bean) {
        if (AopUtils.isAopProxy(bean) || 
                AopUtils.isCglibProxy(bean) || 
                AopUtils.isJdkDynamicProxy(bean)) {
            return true;
        }
        return false;
    }
}
