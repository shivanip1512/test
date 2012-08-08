package com.cannontech.web.security;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;

import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.security.annotation.CheckDevelopmentMode;
import com.cannontech.web.security.annotation.CheckFalseRoleProperty;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.widget.support.WidgetMultiActionController;


public class WebSecurityAnnotationProcessor {
    @Autowired private WebSecurityChecker webSecurityChecker;

    public void process(final Object bean) throws Exception {
        final Class<?> clazz = getClass(bean);
        
        boolean hasCheckDevelopmentMode = hasCheckDevelopmentMode(clazz);
        if (hasCheckDevelopmentMode) {
            doHasCheckDevelopmentMode(getCheckDevelopmentMode(clazz));
        }
        
        boolean hasCheckRole = hasCheckRole(clazz);
        if (hasCheckRole) {
            doHasCheckRole(getCheckRole(clazz));
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
    
    private void doHasCheckDevelopmentMode(CheckDevelopmentMode checkDevelopmentMode) {
        webSecurityChecker.checkDevelopmentMode();
    }
    
    private void doHasCheckRole(CheckRole checkRole) throws Exception {
        YukonRole[] roles = checkRole.value();
        webSecurityChecker.checkRole(roles);
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
    
    private CheckDevelopmentMode getCheckDevelopmentMode(Class<?> clazz) {
        CheckDevelopmentMode checkDevelopmentMode = AnnotationUtils.findAnnotation(clazz, CheckDevelopmentMode.class);
        return checkDevelopmentMode;
    }
    
    private CheckRole getCheckRole(Class<?> clazz) {
        CheckRole checkRole = AnnotationUtils.findAnnotation(clazz, CheckRole.class);
        return checkRole;
    }
    
    private CheckRoleProperty getCheckRoleProperty(Class<?> clazz) {
        CheckRoleProperty checkRoleProperty = AnnotationUtils.findAnnotation(clazz, CheckRoleProperty.class);
        return checkRoleProperty;
    }
    
    private CheckFalseRoleProperty getCheckFalseRoleProperty(Class<?> clazz) {
    	CheckFalseRoleProperty checkFalseRoleProperty = AnnotationUtils.findAnnotation(clazz, CheckFalseRoleProperty.class);
        return checkFalseRoleProperty;
    }
    
    private boolean hasCheckDevelopmentMode(Class<?> clazz) {
        CheckDevelopmentMode checkDevelopmentMode = getCheckDevelopmentMode(clazz);
        boolean hasCheckDevelopmentMode = checkDevelopmentMode != null;
        return hasCheckDevelopmentMode;
    }
    
    private boolean hasCheckRole(Class<?> clazz) {
        CheckRole checkRole = getCheckRole(clazz);
        boolean hasCheckRole = checkRole != null;
        return hasCheckRole;
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
